package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.InputType
import kotlinx.html.TagConsumer
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.stream.createHTML
import kotlinx.html.td
import kotlinx.html.tr
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.ServerFilters
import org.http4k.lens.FormField
import org.http4k.lens.Query
import org.http4k.lens.Validator
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.lens.webForm
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.webJars
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.template.ThymeleafTemplates
import org.http4k.template.ViewModel
import org.http4k.template.viewModel
import java.util.UUID

val firstNameField = FormField.string().required("firstName")
val lastNameField = FormField.string().required("lastName")
val emailField = FormField.string().required("email")
val personLens = Body.webForm(Validator.Strict, firstNameField, lastNameField, emailField)
    .map { Person(firstNameField(it), lastNameField(it), emailField(it)) }
    .toLens()
val pageLens = Query.int().required("page")
val makeLens = Query.string().required("make")

interface HtmlViewModel : ViewModel {
    override fun template() = javaClass.simpleName + ".html"
}

data class Person(val firstName: String, val lastName: String, val email: String)

data class ClickToEdit(val content: String) : HtmlViewModel

data class Agent(val number: Int, val name: String, val email: String, val id: String)

data class ClickToLoad(val agentsList: String) : HtmlViewModel

data class InfiniteScroll(val agentsListInfinite: String) : HtmlViewModel

data class IdName(val id: String, val name: String)

data class ValueSelect(val makes: List<IdName>) : HtmlViewModel

@Suppress("ClassName")
object index : HtmlViewModel

private const val port = 8000

fun main() {
    val htmlLens =
        Body.string(TEXT_HTML).map<String>({ throw UnsupportedOperationException("Cannot parse") }, { it }).toLens()

    val renderer = ThymeleafTemplates().CachingClasspath("templates")
    val thymeleafLens = Body.viewModel(renderer, TEXT_HTML).toLens()

    var person = Person("Bob", "Smith", "bsmith@example.com")

    val agents = generateSequence(Agent(1, "Agent Smith", "void1@null.com", UUID.randomUUID().toString())) {
        Agent(it.number + 1, "Agent Smith", "void${it.number + 1}@null.com", UUID.randomUUID().toString())
    }

    val makes = listOf(IdName("audi", "Audi"), IdName("toyota", "Toyota"), IdName("bmw", "BMW"))
    val models = mapOf(
        "audi" to listOf(IdName("a1", "A1"), IdName("a3", "A3"), IdName("a6", "A6")),
        "toyota" to listOf(IdName("landcruiser", "Landcruiser"), IdName("tacoma", "Tacoma"), IdName("yaris", "Yaris")),
        "bmw" to listOf(IdName("325i", "325i"), IdName("325ix", "325ix"), IdName("X5", "X5"))
    )

    val app = routes(
        "/" bind GET to {
            Response(OK).with(thymeleafLens of index)
        },
        "/click-to-edit" bind GET to {
            Response(OK).with(thymeleafLens of ClickToEdit(viewPerson(person)))
        },
        "/click-to-load" bind GET to {
            Response(OK).with(thymeleafLens of ClickToLoad(agentsList(agents.take(10).toList(), 1)))
        },
        "/infinite-scroll" bind GET to {
            Response(OK).with(thymeleafLens of InfiniteScroll(agentsListInfinite(agents.take(10).toList(), 1)))
        },
        "/value-select" bind GET to {
            Response(OK).with(thymeleafLens of ValueSelect(makes))
        },

        "/person" bind GET to {
            Response(OK).with(htmlLens of viewPerson(person))
        },
        "/person/edit" bind GET to {
            Response(OK).with(htmlLens of editPerson(person))
        },
        "/person" bind PUT to { request ->
            person = personLens(request)
            println("Person updated: $person")
            Response(OK).with(htmlLens of viewPerson(person))
        },
        "/agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of agentsList(agents.drop(10 * page).take(10).toList(), page + 1))
        },
        "/infinite-agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of agentsListInfinite(agents.drop(10 * page).take(10).toList(), page + 1))
        },
        "/models" bind GET to { request ->
            val make = makeLens(request)
            models[make]?.let {
                Response(OK).with(htmlLens of options(it))
            } ?: Response(NOT_FOUND)
        },

        webJars()
    )

    ServerFilters.CatchAll { t ->
        t.printStackTrace()
        Response(Status.INTERNAL_SERVER_ERROR)
    }.then(app).asServer(SunHttp(port)).start()
    println("Listening on $port")
}

private fun options(choices: List<IdName>) =
    createHTML().fragment {
        choices.forEach { choice ->
            option {
                value = choice.id
                +choice.name
            }
        }
    }

private fun agentsList(agents: List<Agent>, page: Int) =
    createHTML().fragment {
        agents.forEach { agent ->
            agentRow(agent)
        }
        tr {
            id = "replaceMe"
            td {
                attributes["colspan"] = "3"
                button(classes = "btn") {
                    attributes["data-hx-get"] = "/agents/?page=${page}"
                    attributes["data-hx-target"] = "#replaceMe"
                    attributes["data-hx-swap"] = "outerHTML"
                    +"Load More Agents..."
                }
            }
        }
    }

private fun agentsListInfinite(agents: List<Agent>, page: Int) =
    createHTML().fragment {
        agents.dropLast(1).forEach { agent ->
            agentRow(agent)
        }
        tr {
            attributes["data-hx-get"] = "/infinite-agents/?page=${page}"
            attributes["data-hx-trigger"] = "revealed"
            attributes["data-hx-swap"] = "afterend"
            td { +agents.last().name }
            td { +agents.last().email }
            td { +agents.last().id }
        }
    }

private fun TagConsumer<String>.agentRow(agent: Agent) {
    tr {
        td { +agent.name }
        td { +agent.email }
        td { +agent.id }
    }
}

private fun viewPerson(person: Person) =
    createHTML()
        .div {
            attributes["data-hx-target"] = "this"
            attributes["data-hx-swap"] = "outerHTML"
            viewControl("First Name", person.firstName)
            viewControl("Last Name", person.lastName)
            viewControl("Email", person.email)
            button {
                classes = setOf("btn", "btn-primary")
                attributes["data-hx-get"] = "/person/edit"
                +"Click To Edit"
            }
        }

private fun DIV.viewControl(label: String, value: String) {
    div {
        label {
            +label
        }
        +": $value"
    }
}

private fun editPerson(person: Person) =
    createHTML()
        .form {
            attributes["data-hx-put"] = "/person"
            attributes["data-hx-target"] = "this"
            attributes["data-hx-swap"] = "outerHTML"
            editControl("First Name", "firstName", person.firstName)
            editControl("Last Name", "lastName", person.lastName)
            editControl("Email", "email", person.email)
            button {
                classes = setOf("btn")
                +"Submit"
            }
            button {
                classes = setOf("btn")
                attributes["data-hx-get"] = "/person"
                +"Cancel"
            }
        }

private fun FORM.editControl(label: String, id: String, theValue: String) {
    div {
        classes = setOf("form-group")
        label {
            +label
        }
        input {
            type = InputType.text
            name = id
            value = theValue
        }
    }
}

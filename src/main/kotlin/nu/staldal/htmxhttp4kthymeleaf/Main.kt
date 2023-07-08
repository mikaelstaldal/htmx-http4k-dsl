package nu.staldal.htmxhttp4kthymeleaf

import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Method.*
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
    .map { PersonData(firstNameField(it), lastNameField(it), emailField(it)) }
    .toLens()
val pageLens = Query.int().required("page")
val makeLens = Query.string().required("make")

interface HtmlViewModel : ViewModel {
    override fun template() = javaClass.simpleName + ".html"
}

interface Person {
    val firstName: String
    val lastName: String
    val email: String
}

data class PersonData(override val firstName: String, override val lastName: String, override val email: String) :
    Person

data class ClickToEdit(val personData: PersonData) : HtmlViewModel, Person by personData
data class ViewPerson(val personData: PersonData) : HtmlViewModel, Person by personData
data class EditPerson(val personData: PersonData) : HtmlViewModel, Person by personData

data class Agent(val number: Int, val name: String, val email: String, val id: String)

data class ClickToLoad(val agents: List<Agent>, val page: Int) : HtmlViewModel
data class AgentsList(val agents: List<Agent>, val page: Int) : HtmlViewModel

data class InfiniteScroll(val agents: List<Agent>, val page: Int) : HtmlViewModel
data class AgentsListInfinite(val agents: List<Agent>, val page: Int) : HtmlViewModel

data class IdName(val id: String, val name: String)

data class ValueSelect(val makes: List<IdName>) : HtmlViewModel
data class Models(val models: List<IdName>) : HtmlViewModel

@Suppress("ClassName")
object index : HtmlViewModel

private const val port = 8000

fun main() {
    val renderer = ThymeleafTemplates().CachingClasspath("templates")
    val htmlLens = Body.viewModel(renderer, TEXT_HTML).toLens()

    var person = PersonData("Bob", "Smith", "bsmith@example.com")

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
            Response(OK).with(htmlLens of index)
        },
        "/click-to-edit" bind GET to {
            Response(OK).with(htmlLens of ClickToEdit(person))
        },
        "/click-to-load" bind GET to {
            Response(OK).with(htmlLens of ClickToLoad(agents.take(10).toList(), 1))
        },
        "/infinite-scroll" bind GET to {
            Response(OK).with(htmlLens of InfiniteScroll(agents.take(10).toList(), 1))
        },
        "/value-select" bind GET to {
            Response(OK).with(htmlLens of ValueSelect(makes))
        },

        "/person" bind GET to {
            Response(OK).with(htmlLens of ViewPerson(person))
        },
        "/person/edit" bind GET to {
            Response(OK).with(htmlLens of EditPerson(person))
        },
        "/person" bind PUT to { request ->
            person = personLens(request)
            println("Person updated: $person")
            Response(OK).with(htmlLens of ViewPerson(person))
        },
        "/agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of AgentsList(agents.drop(10 * page).take(10).toList(), page + 1))
        },
        "/infinite-agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of AgentsListInfinite(agents.drop(10 * page).take(10).toList(), page + 1))
        },
        "/models" bind GET to { request ->
            val make = makeLens(request)
            models[make]?.let {
                Response(OK).with(htmlLens of Models(it))
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

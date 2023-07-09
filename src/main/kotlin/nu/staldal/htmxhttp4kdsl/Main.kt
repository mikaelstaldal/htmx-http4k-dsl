package nu.staldal.htmxhttp4kdsl

import kotlinx.html.stream.createHTML
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
import java.util.UUID

val firstNameField = FormField.string().required("firstName")
val lastNameField = FormField.string().required("lastName")
val emailField = FormField.string().required("email")
val personLens = Body.webForm(Validator.Strict, firstNameField, lastNameField, emailField)
    .map { Person(firstNameField(it), lastNameField(it), emailField(it)) }
    .toLens()
val pageLens = Query.int().required("page")
val makeLens = Query.string().required("make")

data class Person(val firstName: String, val lastName: String, val email: String)

data class Agent(val number: Int, val name: String, val email: String, val id: String)

data class IdName(val id: String, val name: String)

private const val port = 8000

fun main() {
    val htmlLens =
        Body.string(TEXT_HTML).map<String>({ throw UnsupportedOperationException("Cannot parse") }, { it }).toLens()

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
            Response(OK).with(htmlLens of createHTML().index())
        },
        "/click-to-edit" bind GET to {
            Response(OK).with(htmlLens of createHTML().clickToEdit(person))
        },
        "/click-to-load" bind GET to {
            Response(OK).with(htmlLens of createHTML().clickToLoad(agents))
        },
        "/infinite-scroll" bind GET to {
            Response(OK).with(htmlLens of createHTML().infiniteScroll(agents))
        },
        "/value-select" bind GET to {
            Response(OK).with(htmlLens of createHTML().valueSelect(makes))
        },

        "/person" bind GET to {
            Response(OK).with(htmlLens of createHTML().fragment { viewPerson(person) })
        },
        "/person/edit" bind GET to {
            Response(OK).with(htmlLens of createHTML().fragment { editPerson(person) })
        },
        "/person" bind PUT to { request ->
            person = personLens(request)
            println("Person updated: $person")
            Response(OK).with(htmlLens of createHTML().fragment { viewPerson(person) })
        },
        "/agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of createHTML().rows {
                agentsList(
                    agents.drop(10 * page).take(10).toList(),
                    page + 1
                )
            })
        },
        "/infinite-agents" bind GET to { request ->
            val page = pageLens(request)
            Response(OK).with(htmlLens of createHTML().rows {
                agentsListInfinite(
                    agents.drop(10 * page).take(10).toList(), page + 1
                )
            })
        },
        "/models" bind GET to { request ->
            val make = makeLens(request)
            models[make]?.let {
                Response(OK).with(htmlLens of createHTML().options { options(it) })
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

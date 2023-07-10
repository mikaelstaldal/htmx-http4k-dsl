package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.SELECT
import kotlinx.html.TBODY
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.lang
import kotlinx.html.li
import kotlinx.html.meta
import kotlinx.html.option
import kotlinx.html.script
import kotlinx.html.select
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.ul

private const val mainTitle = "htmx with http4k and Kotlin's HTML DSL (kotlinx.html)"

fun HTML.page(subtitle: String, block: DIV.() -> Unit) {
    lang = "en"
    head {
        meta {
            charset = "UTF-8"
        }
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }
        script {
            type = "text/javascript"
            src = "/webjars/htmx.org/1.9.2/dist/htmx.min.js"
        }
        title {
            +mainTitle
        }
    }
    body {
        h1 { +mainTitle }
        h2 { +subtitle }
        div {
            block()
        }
    }
}

fun HTML.index() {
    page("Examples") {
        ul {
            li {
                a {
                    href = "/click-to-edit"
                    +"""Click To Edit"""
                }
            }
            li {
                a {
                    href = "/click-to-load"
                    +"""Click To Load"""
                }
            }
            li {
                a {
                    href = "/infinite-scroll"
                    +"""Infinite scroll"""
                }
            }
            li {
                a {
                    href = "/value-select"
                    +"""Value select"""
                }
            }
        }
    }
}

fun HTML.clickToEdit(person: Person) {
    page("Click to edit") {
        viewPerson(person)
    }
}

fun HTML.clickToLoad(agents: Sequence<Agent>) {
    page("Click to load") {
        table {
            thead {
                tr {
                    th { +"""Name""" }
                    th { +"""Email""" }
                    th { +"""ID""" }
                }
            }
            tbody {
                agentsList(agents.take(10).toList(), 1)
            }
        }
    }
}

fun HTML.infiniteScroll(agents: Sequence<Agent>) {
    page("Infinite scroll") {
        table {
            thead {
                tr {
                    th { +"""Name""" }
                    th { +"""Email""" }
                    th { +"""ID""" }
                }
            }
            tbody {
                agentsListInfinite(agents.take(10).toList(), 1)
            }
        }
    }
}

fun HTML.valueSelect(makes: List<IdName>) {
    page("Value select") {
        div {
            div {
                label { +"""Make""" }
                select {
                    name = "make"
                    attributes["data-hx-get"] = "/models"
                    attributes["data-hx-target"] = "#models"
                    attributes["data-hx-indicator"] = ".htmx-indicator"
                    option {
                        value = ""
                        selected = true
                    }
                    options(makes)
                }
            }
            div {
                label { +"Model" }
                select {
                    id = "models"
                    name = "model"
                }
            }
        }
    }
}

fun SELECT.options(choices: List<IdName>) {
    choices.forEach { choice ->
        option {
            value = choice.id
            +choice.name
        }
    }
}

fun TBODY.agentsList(agents: List<Agent>, page: Int) {
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

fun TBODY.agentsListInfinite(agents: List<Agent>, page: Int) {
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

fun TBODY.agentRow(agent: Agent) {
    tr {
        td { +agent.name }
        td { +agent.email }
        td { +agent.id }
    }
}

fun FlowContent.viewPerson(person: Person) {
    div {
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
}

fun DIV.viewControl(label: String, value: String) {
    div {
        label {
            +label
        }
        +": $value"
    }
}

fun FlowContent.editPerson(person: Person) {
    form {
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
}

fun FORM.editControl(label: String, id: String, theValue: String) {
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

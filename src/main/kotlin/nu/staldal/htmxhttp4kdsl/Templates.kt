package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.Entities
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.SELECT
import kotlinx.html.TBODY
import kotlinx.html.ThScope
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
import kotlinx.html.tfoot
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
        script {
            type = "text/javascript"
            src = "/webjars/hyperscript.org/0.9.8/dist/_hyperscript.min.js"
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
                    +"Click To Edit"
                }
            }
            li {
                a {
                    href = "/click-to-load"
                    +"Click To Load"
                }
            }
            li {
                a {
                    href = "/infinite-scroll"
                    +"Infinite scroll"
                }
            }
            li {
                a {
                    href = "/value-select"
                    +"Value select"
                }
            }
            li {
                a {
                    href = "/todo-list"
                    +"To do list"
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
                    th { +"Name" }
                    th { +"Email" }
                    th { +"ID" }
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
                    th { +"Name" }
                    th { +"Email" }
                    th { +"ID" }
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
            label { +"Make" }
            select {
                name = "make"
                attributes["hx-get"] = "/models"
                attributes["hx-target"] = "#models"
                attributes["hx-indicator"] = ".htmx-indicator"
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

fun HTML.todoList(todoList: Iterable<Todo>) {
    page("To do list") {
        table("table table-hover") {
            thead {
                tr {
                    th {
                        scope = ThScope.col
                        +"What to do"
                    }
                    th {
                        scope = ThScope.col
                        +"done"
                    }
                    th {
                        scope = ThScope.col
                        +Entities.nbsp
                    }
                }
            }
            tbody {
                id = "todo-list"
                todoList.forEach { todo ->
                    todoRow(todo)
                }
            }
            tfoot {
                tr {
                    td {
                        attributes["colspan"] = "2"
                        input {
                            id = "description"
                            name = "description"
                            placeholder = "To do..."
                            required = true
                            type = InputType.text
                            attributes["autofocus"] = "autofocus"
                        }
                    }
                    td {
                        button {
                            attributes["_"] = "on htmx:afterRequest put '' into #description.value"
                            attributes["hx-post"] = "/todo"
                            attributes["hx-include"] = "#description"
                            attributes["hx-target"] = "#todo-list"
                            attributes["hx-swap"] = "beforeend"
                            +"Add"
                        }
                    }
                }
            }
        }
    }
}

fun TBODY.todoRow(todo: Todo) {
    tr {
        td {
            +todo.description
        }
        td {
            input {
                type = InputType.checkBox
                name = "done"
                value = "true"
                checked = todo.done
                attributes["hx-patch"] = "/todo/${todo.id}"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML"
            }
        }
        td {
            button(classes = "btn btn-danger") {
                attributes["hx-confirm"] = "Are you sure?"
                attributes["hx-delete"] = "/todo/${todo.id}"
                attributes["hx-target"] = "closest tr"
                attributes["hx-swap"] = "outerHTML swap:1s"
                +"Delete"
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
                attributes["hx-get"] = "/agents/?page=${page}"
                attributes["hx-target"] = "#replaceMe"
                attributes["hx-swap"] = "outerHTML"
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
        attributes["hx-get"] = "/infinite-agents/?page=${page}"
        attributes["hx-trigger"] = "revealed"
        attributes["hx-swap"] = "afterend"
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
        attributes["hx-target"] = "this"
        attributes["hx-swap"] = "outerHTML"
        viewControl("First Name", person.firstName)
        viewControl("Last Name", person.lastName)
        viewControl("Email", person.email)
        button {
            classes = setOf("btn", "btn-primary")
            attributes["hx-get"] = "/person/edit"
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
        attributes["hx-put"] = "/person"
        attributes["hx-target"] = "this"
        attributes["hx-swap"] = "outerHTML"
        editControl("First Name", "firstName", person.firstName)
        editControl("Last Name", "lastName", person.lastName)
        editControl("Email", "email", person.email)
        button {
            classes = setOf("btn")
            +"Submit"
        }
        button {
            classes = setOf("btn")
            attributes["hx-get"] = "/person"
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

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
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
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
import kotlinx.html.link
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
        link {
            rel = "stylesheet"
            href = "/webjars/bootstrap/5.3.0/dist/css/bootstrap.css"
        }
        title {
            +mainTitle
        }
    }
    body {
        div(classes = "container") {
            h1 { +mainTitle }
            h2 { +subtitle }
            block()
        }
    }
}

fun HTML.index() {
    page("Examples") {
        ul {
            menuItem("/click-to-edit", "Click To Edit")
            menuItem("/click-to-load", "Click To Load")
            menuItem("/infinite-scroll", "Infinite scroll")
            menuItem("/value-select", "Value select")
            menuItem("/todo-list", "To do list")
        }
    }
}

fun UL.menuItem(url: String, text: String) {
    li {
        a {
            href = url
            +text
        }
    }
}

fun HTML.clickToEdit(person: Person) {
    page("Click to edit") {
        viewPerson(person)
    }
}

fun FlowContent.viewPerson(person: Person) {
    div {
        attributes["hx-target"] = "this"
        attributes["hx-swap"] = "outerHTML"
        viewControl("First Name", "firstName", person.firstName)
        viewControl("Last Name", "lastName", person.lastName)
        viewControl("Email", "email", person.email)
        div {
            button(classes = "btn btn-primary") {
                attributes["hx-get"] = "/person/edit"
                +"Click To Edit"
            }
        }
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
        div {
            button(classes = "btn btn-primary me-1") {
                +"Submit"
            }
            button(classes = "btn btn-secondary") {
                attributes["hx-get"] = "/person"
                +"Cancel"
            }
        }
    }
}

fun DIV.viewControl(label: String, id: String, theValue: String) {
    div(classes = "row mb-1") {
        label(classes = "col-sm-1 col-form-label") {
            attributes["for"] = id
            +label
        }
        div(classes = "col-sm-4") {
            input(classes = "form-control-plaintext") {
                type = InputType.text
                readonly = true
                name = id
                value = theValue
            }
        }
    }
}

fun FORM.editControl(label: String, id: String, theValue: String) {
    div(classes = "row mb-1") {
        label(classes = "col-sm-1 col-form-label") {
            attributes["for"] = id
            +label
        }
        div(classes = "col-sm-4") {
            input(classes = "form-control") {
                type = InputType.text
                name = id
                value = theValue
            }
        }
    }
}

fun HTML.clickToLoad(agents: Sequence<Agent>) {
    page("Click to load") {
        table(classes = "table") {
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

fun TBODY.agentsList(agents: List<Agent>, page: Int) {
    agents.forEach { agent ->
        tr {
            td { +agent.name }
            td { +agent.email }
            td { +agent.id }
        }
    }
    tr {
        id = "replaceMe"
        td {
            attributes["colspan"] = "3"
            button(classes = "btn btn-light") {
                attributes["hx-get"] = "/agents/?page=${page}"
                attributes["hx-target"] = "#replaceMe"
                attributes["hx-swap"] = "outerHTML"
                +"Load More Agents..."
            }
        }
    }
}

fun HTML.infiniteScroll(agents: Sequence<Agent>) {
    page("Infinite scroll") {
        table(classes = "table") {
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

fun TBODY.agentsListInfinite(agents: List<Agent>, page: Int) {
    agents.dropLast(1).forEach { agent ->
        tr {
            td { +agent.name }
            td { +agent.email }
            td { +agent.id }
        }
    }
    val lastAgent = agents.last()
    tr {
        attributes["hx-get"] = "/infinite-agents/?page=${page}"
        attributes["hx-trigger"] = "revealed"
        attributes["hx-swap"] = "afterend"
        td { +lastAgent.name }
        td { +lastAgent.email }
        td { +lastAgent.id }
    }
}

fun HTML.valueSelect(makes: List<IdName>) {
    page("Value select") {
        div {
            div(classes = "row mb-3") {
                label(classes = "col-sm-1 col-form-label") {
                    attributes["for"] = "make"
                    +"Make"
                }
                div(classes = "col-sm-2") {
                    select(classes = "form-select") {
                        name = "make"
                        attributes["hx-get"] = "/models"
                        attributes["hx-target"] = "#model"
                        attributes["hx-indicator"] = ".htmx-indicator"
                        option {
                            value = ""
                            selected = true
                        }
                        options(makes)
                    }
                }
            }
            div(classes = "row mb-3") {
                label(classes = "col-sm-1 col-form-label") {
                    attributes["for"] = "model"
                    +"Model"
                }
                div(classes = "col-sm-2") {
                    select(classes = "form-select") {
                        id = "model"
                        name = "model"
                    }
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

fun HTML.todoList(todoList: Iterable<Todo>) {
    page("To do list") {
        table(classes = "table table-hover") {
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
                        button(classes = "btn btn-primary") {
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
                attributes["hx-swap"] = "outerHTML swap:0.5s"
                +"Delete"
            }
        }
    }
}

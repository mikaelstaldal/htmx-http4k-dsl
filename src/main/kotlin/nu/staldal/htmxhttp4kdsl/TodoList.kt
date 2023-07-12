package nu.staldal.htmxhttp4kdsl

import kotlinx.html.Entities
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.ThScope
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.tfoot
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

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
                        input(classes = "form-control") {
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

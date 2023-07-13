package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.TBODY
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.unsafe

fun HTML.bulkUpdate(contacts: Iterable<Contact>) {
    page("Bulk update", {
        style {
            unsafe {
                raw(
                    """
                        .htmx-settling tr.deactivate td {
                            background: lightcoral;
                        }
                        .htmx-settling tr.activate td {
                            background: darkseagreen;
                        }
                        tr td {
                          transition: all 1.2s;
                        }
                        """
                )
            }
        }
    }) {
        div {
            attributes["hx-include"] = "#checked-contacts"
            attributes["hx-target"] = "#tbody"
            button(classes = "btn btn-primary me-1") {
                attributes["hx-put"] = "/contacts/activate"
                +"Activate"
            }
            button(classes = "btn btn-primary") {
                attributes["hx-put"] = "/contacts/deactivate"
                +"Deactivate"
            }
        }
        form {
            id = "checked-contacts"
            table(classes = "table") {
                thead {
                    tr {
                        th {
                            input {
                                type = InputType.checkBox
                                attributes["_"] = """
                                    on change
                                    get the <input[type=checkbox]/> in the <tbody/> in the closest <table/>
                                    set its checked to my checked
                                """
                            }
                        }
                        th { +"Name" }
                        th { +"Email" }
                        th { +"Status" }
                    }
                }
                tbody {
                    id = "tbody"
                    contactsList(contacts.map { it to false }, false)
                }
            }
        }
    }
}

fun TBODY.contactsList(contacts: Iterable<Pair<Contact, Boolean>>, activate: Boolean) {
    contacts.forEach { (contact, wasMutated) ->
        tr(classes = if (wasMutated) (if (activate) "activate" else "deactivate") else "") {
            td {
                input {
                    type = InputType.checkBox
                    name = "ids"
                    value = contact.id
                }
            }
            td { +contact.name }
            td { +contact.email }
            td { +if (contact.active) "Active" else "Inactive" }
        }
    }
}

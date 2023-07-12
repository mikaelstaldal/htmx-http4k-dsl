package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.input
import kotlinx.html.label

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

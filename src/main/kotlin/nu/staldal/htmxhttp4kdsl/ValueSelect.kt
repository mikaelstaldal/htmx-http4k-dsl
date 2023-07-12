package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.SELECT
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select

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

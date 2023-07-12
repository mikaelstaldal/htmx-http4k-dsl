package nu.staldal.htmxhttp4kdsl

import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.id
import kotlinx.html.p
import kotlinx.html.style

fun HTML.modalDialog() {
    page("Modal dialog") {
        button(classes = "btn btn-primary") {
            attributes["hx-get"] = "/modal"
            attributes["hx-target"] = "#modals-here"
            attributes["hx-trigger"] = "click"
            attributes["_"] = """
                on htmx:afterOnLoad 
                wait 10ms 
                then add .show to #modal 
                then add .show to #modal-backdrop
                """
            +"Open modal"
        }
        div {
            id = "modals-here"
        }
    }
}

fun FlowContent.dialogContent() {
    div("modal-backdrop fade") {
        id = "modal-backdrop"
        style = "display:block;"
    }
    div("modal fade") {
        id = "modal"
        attributes["tabindex"] = "-1"
        style = "display:block;"
        div("modal-dialog modal-dialog-centered") {
            div("modal-content") {
                div("modal-header") {
                    h5("modal-title") { +"Modal title" }
                }
                div("modal-body") {
                    p { +"Modal body text goes here." }
                }
                div("modal-footer") {
                    button(classes = "btn btn-secondary") {
                        type = ButtonType.button
                        attributes["_"] = """
                            on click 
                            wait 10ms 
                            then remove .show from #modal 
                            then remove .show from #modal-backdrop 
                            then wait 200ms 
                            then remove #modal-backdrop 
                            then remove #modal
                            """
                        +"Close"
                    }
                }
            }
        }
    }
}

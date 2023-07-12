package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.h3
import kotlinx.html.li
import kotlinx.html.ul

fun HTML.index() {
    page("Examples") {
        h3 { +"htmx UI" }
        ul {
            menuItem("/click-to-edit", "Click to edit")
            menuItem("/click-to-load", "Click to load")
            menuItem("/infinite-scroll", "Infinite scroll")
            menuItem("/value-select", "Value select")
            menuItem("/modal-dialog", "Modal dialog using Bootstrap")
        }
        h3 { +"Complete use cases" }
        ul {
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

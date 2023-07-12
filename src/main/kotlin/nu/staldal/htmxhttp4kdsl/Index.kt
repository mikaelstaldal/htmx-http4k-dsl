package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.li
import kotlinx.html.ul

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

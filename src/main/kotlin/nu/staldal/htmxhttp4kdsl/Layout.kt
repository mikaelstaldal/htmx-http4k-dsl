package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.lang
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

private const val mainTitle = "htmx with http4k and Kotlin's HTML DSL (kotlinx.html)"

fun HTML.page(subtitle: String, headExtra: HEAD.() -> Unit = {}, block: DIV.() -> Unit) {
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
        headExtra()
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

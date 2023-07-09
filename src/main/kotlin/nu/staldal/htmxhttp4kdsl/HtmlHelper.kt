package nu.staldal.htmxhttp4kdsl

import kotlinx.html.DIV
import kotlinx.html.HtmlTagMarker
import kotlinx.html.SELECT
import kotlinx.html.TBODY
import kotlinx.html.TagConsumer

@HtmlTagMarker
inline fun <T, C : TagConsumer<T>> C.fragment(crossinline block: DIV.() -> Unit): T {
    DIV(kotlinx.html.emptyMap, this).block()
    return this.finalize()
}

@HtmlTagMarker
inline fun <T, C : TagConsumer<T>> C.options(crossinline block: SELECT.() -> Unit): T {
    SELECT(kotlinx.html.emptyMap, this).block()
    return this.finalize()
}

@HtmlTagMarker
inline fun <T, C : TagConsumer<T>> C.rows(crossinline block: TBODY.() -> Unit): T {
    TBODY(kotlinx.html.emptyMap, this).block()
    return this.finalize()
}

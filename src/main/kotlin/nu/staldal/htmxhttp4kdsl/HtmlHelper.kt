package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlTagMarker
import kotlinx.html.TagConsumer

@HtmlTagMarker
inline fun <T, C : TagConsumer<T>> C.fragment(crossinline block: TagConsumer<T>.() -> Unit): T {
    try {
        this.block()
    } catch (err: Throwable) {
        this.onTagError(HTMLTag("", this, emptyMap(), null, inlineTag = false, emptyTag = false), err)
    }
    return this.finalize()
}

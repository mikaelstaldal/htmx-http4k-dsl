package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Header

fun htmlPage(status: Status = Status.OK, block: HTML.() -> Unit): Response {
    val text = buildString {
        append("<!DOCTYPE html>\n")
        appendHTML().html(block = block)
    }
    return Response(status)
        .with(Header.CONTENT_TYPE of TEXT_HTML)
        .body(text)
}

fun htmlFragment(status: Status = Status.OK, text: String): Response = Response(status)
    .with(Header.CONTENT_TYPE of TEXT_HTML)
    .body(text)

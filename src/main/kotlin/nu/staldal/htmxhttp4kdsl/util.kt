package nu.staldal.htmxhttp4kdsl

import org.http4k.routing.ResourceLoader
import org.http4k.routing.static

fun webjar(name: String, version: String) =
    static(ResourceLoader.Classpath("/META-INF/resources/webjars/$name/$version/dist"))

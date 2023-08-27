package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.TBODY
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun HTML.infiniteScroll(agents: Sequence<Agent>) {
    page("Infinite scroll") {
        table(classes = "table") {
            thead {
                tr {
                    th { +"Name" }
                    th { +"Email" }
                    th { +"ID" }
                }
            }
            tbody {
                agentsListInfinite(agents.take(10).toList(), 1)
            }
        }
    }
}

fun TBODY.agentsListInfinite(agents: List<Agent>, page: Int) {
    agents.dropLast(1).forEach { agent ->
        tr {
            td { +agent.name }
            td { +agent.email }
            td { +agent.id }
        }
    }
    val lastAgent = agents.last()
    tr {
        attributes["hx-get"] = "?page=${page}"
        attributes["hx-trigger"] = "revealed"
        attributes["hx-swap"] = "afterend"
        td { +lastAgent.name }
        td { +lastAgent.email }
        td { +lastAgent.id }
    }
}

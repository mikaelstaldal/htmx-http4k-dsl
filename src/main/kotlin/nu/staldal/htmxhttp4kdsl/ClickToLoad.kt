package nu.staldal.htmxhttp4kdsl

import kotlinx.html.HTML
import kotlinx.html.TBODY
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

fun HTML.clickToLoad(agents: Sequence<Agent>) {
    page("Click to load") {
        table(classes = "table") {
            thead {
                tr {
                    th { +"Name" }
                    th { +"Email" }
                    th { +"ID" }
                }
            }
            tbody {
                agentsList(agents.take(10).toList(), 1)
            }
        }
    }
}

fun TBODY.agentsList(agents: List<Agent>, page: Int) {
    agents.forEach { agent ->
        tr {
            td { +agent.name }
            td { +agent.email }
            td { +agent.id }
        }
    }
    tr {
        id = "replaceMe"
        td {
            attributes["colspan"] = "3"
            button(classes = "btn btn-light") {
                attributes["hx-get"] = "?page=${page}"
                attributes["hx-target"] = "#replaceMe"
                attributes["hx-swap"] = "outerHTML"
                +"Load More Agents..."
            }
        }
    }
}

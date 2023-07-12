package nu.staldal.htmxhttp4kdsl

data class Person(val firstName: String, val lastName: String, val email: String)

data class Agent(val number: Int, val name: String, val email: String, val id: String)

data class IdName(val id: String, val name: String)

data class Todo(val id: String, val description: String, var done: Boolean = false)

package nu.staldal.htmxhttp4kdsl

import org.http4k.core.Body
import org.http4k.lens.FormField
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.Validator
import org.http4k.lens.int
import org.http4k.lens.string
import org.http4k.lens.webForm
import java.lang.Boolean.parseBoolean

val firstNameField = FormField.string().required("firstName")
val lastNameField = FormField.string().required("lastName")
val emailField = FormField.string().required("email")
val personLens = Body.webForm(Validator.Strict, firstNameField, lastNameField, emailField)
    .map { Person(firstNameField(it), lastNameField(it), emailField(it)) }
    .toLens()


val idsField = FormField.string().multi.required("ids")
val idsLens =
    Body.webForm(Validator.Ignore, idsField).map { if (it.fields.containsKey("ids")) idsField(it) else emptyList() }
        .toLens()

val pageLens = Query.int().required("page")

val makeLens = Query.string().required("make")

val idLens = Path.of("id")
val descriptionField = FormField.string().required("description")
val descriptionLens = Body.webForm(Validator.Strict, descriptionField).map { descriptionField(it) }.toLens()
val doneField = FormField.string().optional("done")
val doneLens =
    Body.webForm(Validator.Strict, doneField).map { form -> doneField(form)?.let { parseBoolean(it) } ?: false }
        .toLens()

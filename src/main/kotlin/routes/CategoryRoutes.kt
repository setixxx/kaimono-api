package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.services.CategoryService

fun Route.categoryRoutes() {
    val categoryService by inject<CategoryService>()

    route("/categories") {
        get {
            try {
                val parentId = call.request.queryParameters["parent_id"]?.toLongOrNull()

                val categories = if (call.request.queryParameters.contains("parent_id")) {
                    categoryService.getCategoriesByParentId(parentId)
                } else {
                    categoryService.getAllCategories()
                }

                call.respond(HttpStatusCode.OK, categories)
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to retrieve categories")
                )
                e.printStackTrace()
            }
        }

        get("/{id}") {
            val categoryId = call.parameters["id"]?.toLongOrNull()
            if (categoryId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid category ID"))
                return@get
            }

            try {
                val category = categoryService.getCategoryById(categoryId)
                call.respond(HttpStatusCode.OK, category)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "Category not found")))
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("error" to "Failed to retrieve category")
                )
                e.printStackTrace()
            }
        }
    }
}
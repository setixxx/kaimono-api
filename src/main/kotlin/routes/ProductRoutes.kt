package setixx.software.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import setixx.software.services.ProductSearchService

fun Route.productRoutes() {
    val productSearchService by inject<ProductSearchService>()

    route("/products") {

        get {
            try {
                val query = call.request.queryParameters["query"]

                val categoryIds = call.request.queryParameters["category_ids"]
                    ?.split(",")
                    ?.mapNotNull { it.trim().toLongOrNull() }

                val minPrice = call.request.queryParameters["min_price"]?.toDoubleOrNull()
                val maxPrice = call.request.queryParameters["max_price"]?.toDoubleOrNull()

                val inStock = call.request.queryParameters["in_stock"]?.toBooleanStrictOrNull()

                val sortBy = call.request.queryParameters["sort_by"]
                val sortOrder = call.request.queryParameters["sort_order"]

                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["page_size"]?.toIntOrNull() ?: 10

                val result = productSearchService.searchProducts(
                    query = query,
                    categoryIds = categoryIds,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    inStock = inStock,
                    sortBy = sortBy,
                    sortOrder = sortOrder,
                    page = page,
                    pageSize = pageSize
                )

                call.respond(HttpStatusCode.OK, result)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Invalid request")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to search products"))
                e.printStackTrace()
            }
        }

        get("/{publicId}") {
            val publicId = call.parameters["publicId"]
            if (publicId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid product ID"))
                return@get
            }

            try {
                val product = productSearchService.getProductByPublicId(publicId)
                call.respond(HttpStatusCode.OK, product)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "Product not found")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Failed to retrieve product"))
                e.printStackTrace()
            }
        }
    }
}
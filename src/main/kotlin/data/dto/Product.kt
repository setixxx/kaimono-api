package setixx.software.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryRequest(
    val name: String,
    val description: String? = null,

    @SerialName("parent_id")
    val parentId: Long? = null
)

@Serializable
data class CategoryResponse(
    val id: Long,
    val name: String,
    val description: String?,

    @SerialName("parent_id")
    val parentId: Long?
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,

    @SerialName("base_price")
    val basePrice: Double,

    @SerialName("category_ids")
    val categoryIds: List<Long>,

    val sizes: List<CreateProductSizeRequest>,
    val images: List<CreateProductImageRequest>? = null
)

@Serializable
data class CreateProductSizeRequest(
    val size: String,

    @SerialName("stock_quantity")
    val stockQuantity: Int,

    @SerialName("price_modifier")
    val priceModifier: Double = 0.0
)

@Serializable
data class CreateProductImageRequest(
    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("is_primary")
    val isPrimary: Boolean = false,

    @SerialName("display_order")
    val displayOrder: Int = 0
)

@Serializable
data class ProductResponse(
    val id: Long,

    @SerialName("public_id")
    val publicId: String,

    val name: String,
    val description: String,

    @SerialName("base_price")
    val basePrice: String,

    @SerialName("is_available")
    val isAvailable: Boolean,

    val categories: List<CategoryResponse>,
    val sizes: List<ProductSizeResponse>,
    val images: List<ProductImageResponse>,

    @SerialName("average_rating")
    val averageRating: Double?,

    @SerialName("review_count")
    val reviewCount: Long
)

@Serializable
data class ProductSizeResponse(
    val id: Long,
    val size: String,

    @SerialName("stock_quantity")
    val stockQuantity: Int,

    @SerialName("price_modifier")
    val priceModifier: String
)

@Serializable
data class ProductImageResponse(
    val id: Long,

    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("is_primary")
    val isPrimary: Boolean,

    @SerialName("display_order")
    val displayOrder: Int
)

@Serializable
data class ProductListResponse(
    val products: List<ProductResponse>,

    @SerialName("total_count")
    val totalCount: Long,

    val page: Int,

    @SerialName("page_size")
    val pageSize: Int
)
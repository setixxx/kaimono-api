package setixx.software.services

import setixx.software.data.dto.CategoryResponse
import setixx.software.data.repositories.CategoryRepository

class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    suspend fun getAllCategories(): List<CategoryResponse> {
        val categories = categoryRepository.findAllCategories()

        return categories.map { category ->
            CategoryResponse(
                id = category.id,
                name = category.name,
                description = category.description,
                parentId = category.parentId
            )
        }
    }

    suspend fun getCategoriesByParentId(parentId: Long?): List<CategoryResponse> {
        val categories = categoryRepository.findCategoriesByParentId(parentId)

        return categories.map { category ->
            CategoryResponse(
                id = category.id,
                name = category.name,
                description = category.description,
                parentId = category.parentId
            )
        }
    }

    suspend fun getCategoryById(id: Long): CategoryResponse {
        val category = categoryRepository.findCategoryById(id)
            ?: throw IllegalArgumentException("Category not found")

        return CategoryResponse(
            id = category.id,
            name = category.name,
            description = category.description,
            parentId = category.parentId
        )
    }
}
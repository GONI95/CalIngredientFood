package sang.gondroid.calingredientfood.domain.repository

import sang.gondroid.calingredientfood.data.util.TaskResult

/**
 * Gon [22.01.12] : GetFoodNtrIrdntUseCase/FoodNtrIrdntRepositoryImpl 사이를 중재해주는 Interface
 *                  Domain / Data Layer 사이를 중재해주는 매개체 역할을 하는 Repository
 */
interface FoodNtrIrdntRepository {
    suspend fun getFoodNtrIrdnt(value: String) : TaskResult<*>
}
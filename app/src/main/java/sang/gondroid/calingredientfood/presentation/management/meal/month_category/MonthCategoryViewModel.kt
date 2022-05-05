package sang.gondroid.calingredientfood.presentation.management.meal.month_category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sang.gondroid.calingredientfood.R
import sang.gondroid.calingredientfood.domain.model.MealNtrIrdntModel
import sang.gondroid.calingredientfood.domain.use_case.GetMealNtrIrdntListForMonthUseCase
import sang.gondroid.calingredientfood.presentation.base.BaseViewModel
import sang.gondroid.calingredientfood.presentation.util.DebugLog
import sang.gondroid.calingredientfood.presentation.util.MealNtrIrdntSort
import sang.gondroid.calingredientfood.presentation.util.MonthCategory

internal class MonthCategoryViewModel(
    private val monthCategory: MonthCategory,
    private val monthRange: Pair<String, String>,
    private val getMealNtrIrdntListForMonthUseCase: GetMealNtrIrdntListForMonthUseCase,
    private var mealNtrIrdntSort: MealNtrIrdntSort = MealNtrIrdntSort.INITIALIZE
) : BaseViewModel() {

    private val _mealNtrIrdntPagingDataFlow = MutableStateFlow<PagingData<MealNtrIrdntModel>?>(
        null
    )
    val mealNtrIrdntPagingDataFlow = _mealNtrIrdntPagingDataFlow.asStateFlow()

    private val checkedMealNtrIrdntSet = mutableSetOf<Long>()
    private val _checkedMealNtrIrdntSize = MutableLiveData(0)
    val checkedMealNtrIrdntSize: LiveData<Int>
        get() = _checkedMealNtrIrdntSize

    val onMenuItemClick: (Int) -> Boolean = this::onMenuItemClick

    override fun fetchData(): Job = viewModelScope.launch {
        DebugLog.d("fetchData firstDay : ${monthRange.first}, lastDay : ${monthRange.second}, mealNtrIrdntSort : $mealNtrIrdntSort")

        /**
         * Gon [22.04.28] : 식단 영양 성분에 대한 PagingData를 요청
         */
        getMealNtrIrdntListForMonthUseCase
            .invoke(monthRange.first, monthRange.second, mealNtrIrdntSort)
            .cachedIn(this)
            .flowOn(Dispatchers.IO)
            .collectLatest {
                _mealNtrIrdntPagingDataFlow.emit(it)
            }
    }

    /**
     * Gon [22.04.28] : MealNtrIrdntSort 상수를 mealNtrIrdntSort 프로퍼티에 할당하고 fetchData() 호출
     */
    fun setMealNtrIrdntSort(mealNtrIrdntSort: MealNtrIrdntSort) {
        this.mealNtrIrdntSort = mealNtrIrdntSort
        fetchData()
    }

    fun handlingCheckedMealNtrIrdntSet(model: MealNtrIrdntModel, isChecked: Boolean) {
        if (isChecked) {
            checkedMealNtrIrdntSet.add(model.id)
        } else {
            checkedMealNtrIrdntSet.remove(model.id)
        }

        model.checkState = isChecked
        _checkedMealNtrIrdntSize.value = checkedMealNtrIrdntSet.size
    }

    private fun onMenuItemClick(itemId: Int): Boolean =
        when (itemId) {
            R.id.delete_selection -> {
                DebugLog.d("선택 삭제")
                true
            }
            R.id.delete_all -> {
                DebugLog.d("전체 삭제")
                true
            }
            else -> {
                false
            }
        }
}

package sang.gondroid.calingredientfood.presentation.management.meal.month_category

import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import sang.gondroid.calingredientfood.databinding.FragmentMonthCategoryBinding
import sang.gondroid.calingredientfood.domain.model.MealNtrIrdntModel
import sang.gondroid.calingredientfood.domain.model.Model
import sang.gondroid.calingredientfood.presentation.base.BaseFragment
import sang.gondroid.calingredientfood.presentation.util.DebugLog
import sang.gondroid.calingredientfood.presentation.util.MonthCategory
import sang.gondroid.calingredientfood.presentation.widget.adapter.BasePagingDataAdapter
import sang.gondroid.calingredientfood.presentation.widget.decorator.GridSpacingDecoration
import sang.gondroid.calingredientfood.presentation.widget.listener.MealNtrIrdntListener

internal class MonthCategoryFragment :
    BaseFragment<FragmentMonthCategoryBinding, MonthCategoryViewModel>() {

    override val viewModel: MonthCategoryViewModel by viewModel {
        parametersOf(
            todoCategory,
            monthRange
        )
    }

    override fun getDataBinding(): FragmentMonthCategoryBinding =
        FragmentMonthCategoryBinding.inflate(layoutInflater)

    // Gon [22.04.22] : MealManagementActivity에서 newInstance()를 통해 전달한 category값을 Bundle로부터 가져옴
    private val todoCategory by lazy { arguments?.getSerializable(MONTH_CATEGORY_KEY) as MonthCategory }
    private val monthRange by lazy { arguments?.getSerializable(MONTH_RANGE_KEY) as Pair<*, *> }

    private val mealNtrIrdntPagingDataAdapter by lazy {
        BasePagingDataAdapter<MealNtrIrdntModel>(
            object : MealNtrIrdntListener {
                override fun onCheckedChanged(model: MealNtrIrdntModel, isChecked: Boolean) {
                    DebugLog.d("${model.id}, ${model.checkState}")

                    viewModel.handlingCheckedMealNtrIrdntSet(model, isChecked)
                }

                override fun onClickItem(model: Model) {
                    DebugLog.d("$model")
                }
            }
        )
    }

    /**
     * Gon [22.04.27] : GridLayoutManager(context, 가로 item 수, 출력 방향, item의 첫, 끝 중 시작 위치)
     */
    override fun initViews() {
        binding.basePagingDataAdapter = mealNtrIrdntPagingDataAdapter
        binding.viewModel = viewModel

        with(binding.mealNtrIrdntRecyclerView) {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridSpacingDecoration(2, 30, true))
            adapter = mealNtrIrdntPagingDataAdapter
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun observeData() {
        lifecycleScope.launch {
            viewModel.mealNtrIrdntPagingDataFlow.collectLatest { pagingData ->
                pagingData?.let {
                    mealNtrIrdntPagingDataAdapter.submitData(pagingData as PagingData<Model>)
                }
            }
        }
    }

    companion object {
        const val MONTH_CATEGORY_KEY = "MONTHCATEGORY"
        const val MONTH_RANGE_KEY = "MONTHRANGE"

        // Gon [22.04.22] : MonthCategoryFragment 인스턴스를 생성하면서 arguments에 데이터를 넘겨주는 코드
        fun newInstance(
            monthCategory: MonthCategory,
            monthRange: Pair<String, String>
        ) = MonthCategoryFragment().apply {
            arguments = bundleOf(
                MONTH_CATEGORY_KEY to monthCategory,
                MONTH_RANGE_KEY to monthRange
            )

            DebugLog.i("$arguments, $MONTH_CATEGORY_KEY, ${hashCode()}")
        }
    }
}

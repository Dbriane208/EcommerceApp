package daniel.brian.ecommerceapp.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// adding extra space between our recyclerview items
class VerticalItemDecoration(private val amount: Int = 30) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }
}
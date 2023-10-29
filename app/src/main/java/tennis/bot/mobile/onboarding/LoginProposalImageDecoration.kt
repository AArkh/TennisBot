package tennis.bot.mobile.onboarding

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject

class LoginProposalImageDecoration @Inject constructor(
    @ApplicationContext context: Context,
) : RecyclerView.ItemDecoration() {

    private val spanOneSpacing = context.dpToPx(10)
    private val spanTwoSpacing = context.dpToPx(40)
    private val spanThreeSpacing = context.dpToPx(120)
    private val spanFourSpacing = context.dpToPx(80)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spacing = when(layoutParams.spanIndex) {
            0 -> spanOneSpacing
            1 -> spanTwoSpacing
            2 -> spanThreeSpacing
            3 -> spanFourSpacing
            else -> throw IllegalArgumentException("Span index must be between 0 and 3")
        }
        outRect.top = -spacing
        outRect.bottom = spacing
    }
}
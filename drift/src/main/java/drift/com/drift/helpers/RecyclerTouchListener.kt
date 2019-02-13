package drift.com.drift.helpers

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import androidx.recyclerview.widget.RecyclerView


internal class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: (view: View, position: Int) -> Unit) : RecyclerView.OnItemTouchListener {

    private val gestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {

        }
    })


    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && gestureDetector.onTouchEvent(e)) {
            clickListener(child, rv.getChildLayoutPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}
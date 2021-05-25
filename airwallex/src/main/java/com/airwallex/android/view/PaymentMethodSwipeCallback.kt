package com.airwallex.android.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.R
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("ClickableViewAccessibility")
abstract class PaymentMethodSwipeCallback(val context: Context, private val recyclerView: RecyclerView) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private var buttons: MutableList<UnderlayButton> = ArrayList()
    private var swipedPosition = -1
    private var swipeThreshold = 0.5f
    private val buttonWidth = context.resources.getDimension(R.dimen.swipe_button_width)
    private val rect = Rect()

    private val recoverQueue: LinkedList<Int> = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            return if (contains(element)) false else super.add(element)
        }
    }

    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            for (button in buttons) {
                if (button.onClick(e.x, e.y)) break
            }
            return true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { _, e ->
        if (swipedPosition < 0) return@OnTouchListener false
        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedViewHolder = recyclerView.findViewHolderForAdapterPosition(swipedPosition)
            ?: return@OnTouchListener false
        val swipedItem = swipedViewHolder.itemView
        swipedItem.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(e) else {
                recoverQueue.add(swipedPosition)
                swipedPosition = -1
                recoverSwipedItem()
            }
        }
        false
    }
    private val gestureDetector: GestureDetector = GestureDetector(context, gestureListener)
    private val buttonsBuffer: MutableMap<Int, ArrayList<UnderlayButton>> = HashMap()

    init {
        recyclerView.setOnTouchListener(onTouchListener)
        attachSwipe()
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipedPosition != pos) recoverQueue.add(swipedPosition)
        swipedPosition = pos
        if (buttonsBuffer.containsKey(swipedPosition)) {
            buttons = buttonsBuffer[swipedPosition]!!
        } else {
            buttons.clear()
        }
        buttonsBuffer.clear()
        swipeThreshold = 0.5f * buttons.size * buttonWidth
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (viewHolder !is PaymentMethodsAdapter.CardHolder) {
            return
        }
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: ArrayList<UnderlayButton> = ArrayList()
                if (!buttonsBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer)
                    buttonsBuffer[pos] = buffer
                } else {
                    buffer = buttonsBuffer[pos]!!
                }
                translationX = dX * buffer.size * buttonWidth / itemView.width
                drawButtons(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll()
            if (position != null && position > -1) {
                recyclerView.adapter?.notifyItemChanged(position)
            }
        }
    }

    private fun drawButtons(c: Canvas, itemView: View, buffer: List<UnderlayButton>, position: Int, dX: Float) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                position
            )
            right = left
        }
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class UnderlayButton(private val text: String, private val textSize: Int, private val color: Int, private val clickListener: UnderlayButtonClickListener) {
        private var position = 0
        private var clickRegion: RectF? = null
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(position)
                return true
            }
            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()

            // Draw background
            p.color = color
            c.drawRect(rect, p)

            // Draw Text
            p.color = Color.WHITE
            p.textSize = textSize.toFloat()
            val r = Rect()
            val cHeight = rect.height()
            val cWidth = rect.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            val x = cWidth / 2f - r.width() / 2f - r.left
            val y = cHeight / 2f + r.height() / 2f - r.bottom
            c.drawText(text, rect.left + x, rect.top + y, p)
            clickRegion = rect
            this.position = pos
        }
    }

    interface UnderlayButtonClickListener {
        fun onClick(position: Int)
    }

    abstract fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder?, underlayButtons: ArrayList<UnderlayButton>)
}

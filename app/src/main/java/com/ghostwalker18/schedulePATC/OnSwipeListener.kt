/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.schedulePATC

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

/**
 * Этот класс используется для реализации обработки смахивания вправо или влево.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
open class OnSwipeListener(context: Context?) : OnTouchListener {
    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    /**
     * Этот метод используется для обработки смахивания вверх.
     */
    open fun onSwipeTop() { /*To override*/ }

    /**
     * Этот метод используется для обработки смахивания вниз.
     */
    fun onSwipeBottom() { /*To override*/ }

    /**
     * Этот метод используется для обработки смахивания влево.
     */
    open fun onSwipeLeft() { /*To override*/ }

    /**
     * Этот метод используется для обработки смахивания вправо.
     */
    open fun onSwipeRight() { /*To override*/ }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {
            val distanceX = e2.x - e1!!.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY)
                && abs(distanceX) > Companion.SWIPE_DISTANCE_THRESHOLD
                && abs(velocityX) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight()
                else
                    onSwipeLeft()
                return true
            }
            if (abs(distanceY) > abs(distanceX)
                && abs(distanceY) > Companion.SWIPE_DISTANCE_THRESHOLD
                && abs(velocityY) > Companion.SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY > 0)
                    onSwipeBottom()
                else
                    onSwipeTop()
                return true
            }
            return false
        }
    }
}
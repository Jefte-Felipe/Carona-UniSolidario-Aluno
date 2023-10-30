package com.tcc.unisolidaria.utils

import android.animation.ValueAnimator
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import androidx.lifecycle.LifecycleObserver
import kotlin.math.abs

class CircleAnimationUtil(
    private val circles: List<View>
) : LifecycleObserver {

    companion object {
        private const val CIRCLE_ANIMATION_DURATION = 1500L
        private const val CIRCLE_EVEN_MAX_TRANSLATE_Y = 50f
        private const val CIRCLE_ODD_MAX_TRANSLATE_Y = -50f
        private val translateYs = listOf(CIRCLE_EVEN_MAX_TRANSLATE_Y, CIRCLE_ODD_MAX_TRANSLATE_Y)
    }

    private val animations = SparseArray<ValueAnimator>()

    fun start() {
        circles.forEachIndexed { index, _ ->
            val translateY = getTranslateY(index)
            moveCircle(index, translateY)
        }
    }

    private fun getTranslateY(index: Int): Float {
        return translateYs[index % translateYs.size]
    }

    private fun moveCircle(index: Int, translateY: Float, startDelay: Long = 0) {
        val circleView: View = circles[index]

        val initialTranslateY = circleView.translationY
        val circleAnimation = ValueAnimator.ofFloat(initialTranslateY, translateY)
                .setDuration(CIRCLE_ANIMATION_DURATION)
        circleAnimation.startDelay = startDelay

        circleAnimation.addUpdateListener { animator ->
            val currentTranslateY = animator.animatedValue as Float
            circleView.translationY = currentTranslateY

            if (currentTranslateY == translateY) {
                val nextTranslateY = if (abs(translateY) > 0f) {
                    0f
                } else {
                    getTranslateY(index)
                }
                moveCircle(index, nextTranslateY)
            }
        }

        circleAnimation.start()
        animations.put(index, circleAnimation)
    }

    fun cancel() {
        animations.forEach { _, value ->
            value.cancel()
        }
    }

}
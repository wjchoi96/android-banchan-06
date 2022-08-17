package com.woowahan.banchan.extension

import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * https://stackoverflow.com/questions/57587618/viewpager2-with-horizontal-scrollview-inside
 * 를 기반으로 변경
 */
fun RecyclerView.addControlHorizontalScrollListener(){
    this.let {
        val onTouchListener: RecyclerView.OnItemTouchListener = object :
            RecyclerView.OnItemTouchListener {
            var lastX = 0
            var lastY = 0
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = e.x.toInt()
                        lastY = e.y.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val distanceX = abs(lastX - e.x)
                        val distanceY = abs(lastY - e.y)
                        if(distanceY > distanceX) { // y 축 이동거리가 더 길다면, 세로 스크롤로 간주
                            it.parent.requestDisallowInterceptTouchEvent(false) // 부모 스크롤 허용
                            return false
                        }
                        val isScrollingRight = e.x < lastX

                        // 오른쪽 스크롤이며, 마지막 item 까지 스크롤 된 경우
                        val canNotScrollRight = isScrollingRight && (it.layoutManager as LinearLayoutManager?)?.findLastCompletelyVisibleItemPosition() == it.adapter!!.itemCount - 1
                        // 왼쪽 스크롤이며, 첫번째 item 까지 스크롤 된 경우
                        val canNotScrollLeft = !isScrollingRight && (it.layoutManager as LinearLayoutManager?)?.findFirstCompletelyVisibleItemPosition() == 0

                        val canScrollHorizontal = !(canNotScrollRight || canNotScrollLeft)
                        it.parent.requestDisallowInterceptTouchEvent(canScrollHorizontal) // 부모 스크롤 설정(true 전달시, 부모 스크롤 비허용)
                    }
                    MotionEvent.ACTION_UP -> {
                        lastX = 0
                        lastY = 0
                        it.parent.requestDisallowInterceptTouchEvent(false) // 부모 스크롤 허용
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }
        it.addOnItemTouchListener(onTouchListener)
    }
}
package com.woowahan.banchan

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

class LifeCycleLogObserver(
    private val TAG: String
): DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Timber.tag(TAG).d("onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Timber.tag(TAG).d("onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        Timber.tag(TAG).d("onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Timber.tag(TAG).d("onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Timber.tag(TAG).d("onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Timber.tag(TAG).d("onDestroy")
    }
}
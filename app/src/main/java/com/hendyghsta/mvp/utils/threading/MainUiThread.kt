package com.hendyghsta.mvp.utils.threading

import android.os.Looper
import android.os.Handler

/**
 * Created by hendyghsta on 04/22/2022.
 */
class MainUiThread private constructor() {

    private val handler: Handler

    init {
        handler = Handler(Looper.getMainLooper())
    }

    fun post(runnable: Runnable) {
        handler.post(runnable)
    }

    companion object {
        private var mainUiThread: MainUiThread? = null

        val instance: MainUiThread
            @Synchronized get() {
                if (mainUiThread == null) {
                    mainUiThread = MainUiThread()
                }
                return mainUiThread!!
            }
    }
}
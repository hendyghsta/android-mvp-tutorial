package com.hendyghsta.mvp.utils.threading

/**
 * Created by hendyghsta on 04/22/2022.
 */
class SelfThread private constructor() {

    private var thread: Thread? = null

    init {
        thread = Thread()
    }

    fun startThread(runnable: Runnable) {
        if (thread != null)
            thread!!.interrupt()
        thread = Thread(runnable)
        thread!!.start()
    }

    fun stopThread() {
        if (thread != null)
            thread = null
    }

    companion object {

        private var selfThread: SelfThread? = null

        val instance: SelfThread
            @Synchronized get() {
                if (selfThread == null)
                    selfThread = SelfThread()
                return selfThread!!
            }
    }
}
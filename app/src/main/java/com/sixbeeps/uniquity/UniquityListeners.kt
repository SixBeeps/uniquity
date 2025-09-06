package com.sixbeeps.uniquity

import android.os.Looper
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import com.sixbeeps.uniquity.UniquityKeyboardView.UniquityKeyboardListener

@Suppress("DEPRECATION")
class UniquityListeners {
    /**
     * A class to handle incoming click events
     */
    class ClickListener(
        val listener: UniquityKeyboardListener?,
        val key: UniquityKey
    ) : View.OnClickListener {
        override fun onClick(view: View?) {
            if (listener != null) {
                UniquityKeyboardView.vibrator?.vibrate(20)
                val type = key.type
                if (type == UniquityKey.KeyType.DELETE) {
                    listener.onDelete()
                } else if (type == UniquityKey.KeyType.ENTER) {
                    listener.onEnter()
                } else if (type == UniquityKey.KeyType.NORMAL) {
                    val contents = key.contents
                    if (contents != null && !contents.isEmpty()) {
                        listener.onKey(key.contents)
                    }
                }
            }
        }
    }

    /**
     * A class to handle incoming touch events and handles logic for held keys
     */
    class TouchListener(
        val listener: UniquityKeyboardListener?,
        val key: UniquityKey
    ) : View.OnTouchListener {
        private var touched = false
        private val handler = Handler(Looper.getMainLooper())
        private var isRepeating = false

        override fun onTouch(view: View?, event: MotionEvent): Boolean {
            if (listener != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        touched = true

                        if (key.type == UniquityKey.KeyType.DELETE) {
                            isRepeating = true
                            listener.onDelete()
                            UniquityKeyboardView.vibrator?.vibrate(20)

                            // Fun surprise feature: this handler never gets cancelled when released
                            // which means spamming the delete key then holding makes text delete
                            // faster. This might get patched out later, but I kinda love this
                            // behavior, so it's staying in for now.
                            handler.postDelayed(repeatDeleteRunnable, 500)
                            return true
                        }
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        touched = false
                    }
                }
            }
            return false
        }

        private val repeatDeleteRunnable = object : Runnable {
            override fun run() {
                if (touched) {
                    UniquityKeyboardView.vibrator?.vibrate(10)
                    listener?.onDelete()
                    handler.postDelayed(this, 100)
                } else {
                    isRepeating = false
                }
            }
        }
    }

    companion object {
        /**
         * Binds all the listeners to the given view
         */
        fun bindAllListeners(view: View?, listener: UniquityKeyboardListener?, key: UniquityKey) {
            if (view != null) {
                view.setOnClickListener(ClickListener(listener, key))
                view.setOnTouchListener(TouchListener(listener, key))
            }
        }
    }
}
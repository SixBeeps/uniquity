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
            if (listener == null) return
            UniquityKeyboardView.vibrator?.vibrate(20)
            if (key.type == UniquityKey.KeyType.DELETE) {
                listener.onDelete()
            } else if (key.type == UniquityKey.KeyType.ENTER) {
                listener.onEnter()
            } else if (key.type == UniquityKey.KeyType.NORMAL) {
                val contents = key.contents
                if (contents != null && !contents.isEmpty()) {
                    listener.onKey(contents)
                }
            }
        }
    }

    /**
     * A class to handle incoming long-press events
     */
    class LongClickListener(
        val listener: UniquityKeyboardListener?,
        val key: UniquityKey
    ) : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            if (listener == null) return false
            UniquityKeyboardView.vibrator?.vibrate(20)
            if (key.type == UniquityKey.KeyType.NORMAL) {
                // Add key to favorites
                listener.onLongPress(key.getHexCodepoint())
            }
            return true
        }
    }

    /**
     * A class to handle incoming touch events
     * The DELETE key hold logic is implemented here instead of LongClickListener because the
     * LongClickListener does not provide an event for when the key is released, which is needed
     * to cancel the repeating runnable.
     */
    class TouchListener(
        val listener: UniquityKeyboardListener?,
        val key: UniquityKey
    ) : View.OnTouchListener {
        private var touched = false
        private val handler = Handler(Looper.getMainLooper())
        private var isRepeating = false

        override fun onTouch(view: View?, event: MotionEvent): Boolean {
            if (listener == null) return false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touched = true

                    if (key.type == UniquityKey.KeyType.DELETE) {
                        isRepeating = true
                        handler.postDelayed(repeatDeleteRunnable, 500)
                        view?.performClick()
                        return true
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    touched = false
                    isRepeating = false
                    handler.removeCallbacks(repeatDeleteRunnable)
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
                view.setOnLongClickListener(LongClickListener(listener, key))
                view.setOnTouchListener(TouchListener(listener, key))
            }
        }
    }
}
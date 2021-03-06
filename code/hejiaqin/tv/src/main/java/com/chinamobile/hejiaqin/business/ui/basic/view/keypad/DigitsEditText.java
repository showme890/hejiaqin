/*
 * 文件名: DigitsEditText.java
 * 版    权：  Copyright Huawei Tech. Co. Ltd. All Rights Reserved.
 * 描    述: 阻止软键盘弹出的EditText
 * 创建人: fengdai
 * 创建时间:2012-8-8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

package com.chinamobile.hejiaqin.business.ui.basic.view.keypad;

import android.content.Context;
import android.graphics.Rect;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 阻止软键盘弹出的EditText
 */
public class DigitsEditText extends EditText {
    /**
     * 构造函数
     * @param context context
     * @param attrs attrs
     */
    public DigitsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    /**
     * 当EditText焦点改变时，阻止软件盘弹出<BR>
     * @param focused focused
     * @param direction direction
     * @param previouslyFocusedRect previouslyFocusedRect
     * @see android.widget.TextView#onFocusChanged(boolean, int, Rect)
     */
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
    }

    /**
     * 当EditText被触摸时，阻止软件盘弹出<BR>
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     * @see android.widget.TextView#onTouchEvent(MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean ret = super.onTouchEvent(event);
        // Must be done after super.onTouchEvent()
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive(this)) {
            imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
        }
        return ret;
    }

    /**
     * [一句话功能简述]<BR>
     * @param event The event to send.
     * @see android.view.View#sendAccessibilityEventUnchecked(AccessibilityEvent)
     */
    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            // Since we're replacing the text every time we add or remove a
            // character, only read the difference. (issue 5337550)
            final int added = event.getAddedCount();
            final int removed = event.getRemovedCount();
            final int length = event.getBeforeText().length();
            if (added > removed) {
                event.setRemovedCount(0);
                event.setAddedCount(1);
                event.setFromIndex(length);
            } else if (removed > added) {
                event.setRemovedCount(1);
                event.setAddedCount(0);
                event.setFromIndex(length - 1);
            } else {
                return;
            }
        } else if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            // The parent EditText class lets tts read "edit box" when this View has a focus, which
            // confuses users on app launch (issue 5275935).
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }
}

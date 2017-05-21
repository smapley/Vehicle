package com.smapley.vehicle.utils;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.smapley.vehicle.R;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by wuzhixiong on 2017/5/20.
 */

public class LicenseKeyboardUtil {
    private KeyboardView keyboardView;
    private Keyboard k1;// 省份简称键盘
    private Keyboard k2;// 数字字母键盘
    private String provinceShort[];
    private String letterAndDigit[];
    private EditText edits[];
    private int currentEditText = 0;//默认当前光标在第一个EditText

    public LicenseKeyboardUtil(Activity ctx, EditText edits[]) {
        this.edits = edits;
        k1 = new Keyboard(ctx, R.xml.province_short_keyboard);
        k2 = new Keyboard(ctx, R.xml.lettersanddigit_keyboard);
        keyboardView = (KeyboardView) ((Activity) ctx).findViewById(R.id.set_keyboard);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        //设置为true时,当按下一个按键时会有一个popup来显示<key>元素设置的android:popupCharacters=""
        keyboardView.setPreviewEnabled(false);
        //设置键盘按键监听器
        keyboardView.setOnKeyboardActionListener(listener);
        provinceShort = new String[]{"京", "津", "冀", "鲁", "晋", "蒙", "辽", "吉", "黑"
                , "沪", "苏", "浙", "皖", "闽", "赣", "豫", "鄂", "湘"
                , "粤", "桂", "渝", "川", "贵", "云", "藏", "陕", "甘"
                , "青", "琼", "新", "港", "澳", "台", "宁"};

        letterAndDigit = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                , "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"
                , "A", "S", "D", "F", "G", "H", "J", "K", "L"
                , "Z", "X", "C", "V", "B", "N", "M"};
        if (ArrayUtils.isNotEmpty(edits)) {
            for (int i = 0; i < edits.length; i++) {
                setSoftKeyboradHide(ctx, edits[i]);
                edits[i].setCursorVisible(false);
                int finalI = i;
                edits[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            currentEditText = finalI;
                            if (currentEditText < 1) {
                                //切换为省份简称键盘
                                keyboardView.setKeyboard(k1);
                            } else {
                                keyboardView.setKeyboard(k2);
                            }
                            showKeyboard();
                            v.setBackgroundColor(ContextCompat.getColor(ctx, R.color.cyan2));
                        } else {
                            v.setBackgroundColor(ContextCompat.getColor(ctx, R.color.gray3));
                        }
                    }
                });
            }
        }
    }

    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (primaryCode == 112) { //xml中定义的删除键值为112
                edits[currentEditText].setText("");//将当前EditText置为""并currentEditText-1
                currentEditText--;
                if (currentEditText < 1) {
                    //切换为省份简称键盘
                    keyboardView.setKeyboard(k1);
                }
                if (currentEditText < 0) {
                    currentEditText = 0;
                }
                edits[currentEditText].requestFocus();
            } else if (primaryCode == 66) { //xml中定义的完成键值为66
                hideKeyboard();
            } else { //其它字符按键
                if (currentEditText == 0) {
                    //如果currentEditText==0代表当前为省份键盘,
                    // 按下一个按键后,设置相应的EditText的值
                    // 然后切换为字母数字键盘
                    //currentEditText+1
                    edits[0].setText(provinceShort[primaryCode]);
                    currentEditText = 1;
                    //切换为字母数字键盘
                    keyboardView.setKeyboard(k2);
                } else {
                    //第二位必须大写字母
                    if (currentEditText == 1 && !letterAndDigit[primaryCode].matches("[A-Z]{1}")) {
                        return;
                    }
                    edits[currentEditText].setText(letterAndDigit[primaryCode]);
                    currentEditText++;
                    if (currentEditText > 6) {
                        currentEditText = 6;
                    }
                }
                edits[currentEditText].requestFocus();
            }
        }
    };

    public void setData(String string) {
        if (StringUtils.isNotEmpty(string) && ArrayUtils.isNotEmpty(edits) && string.length() >= edits.length) {

            for (int i = 0; i < edits.length; i++) {
                edits[i].setText(string.substring(i, i + 1));
            }
        }
    }

    public String getData() {
        StringBuilder sb = new StringBuilder();
        if (ArrayUtils.isNotEmpty(edits)) {
            for (EditText editText : edits) {
                sb.append(editText.getText().toString());
            }
        }
        return sb.toString();
    }


    private void setSoftKeyboradHide(Activity ctx, EditText edit) {
        ctx.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        try {
            Class<EditText> cls = EditText.class;
            String method_setSoftInputOnFocus = "setShowSoftInputOnFocus";
//            method_setSoftInputOnFocus = "setSoftInputOnFocus";
            Method setShowSoftInputOnFocus = cls.getMethod(method_setSoftInputOnFocus, boolean.class);
            setShowSoftInputOnFocus.setAccessible(false);
            setShowSoftInputOnFocus.invoke(edit, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示键盘
     */
    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
        if (ArrayUtils.isNotEmpty(edits)) {
            for (EditText editext : edits) {
                editext.clearFocus();
            }
        }
    }

    public boolean isShow() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            return true;
        } else {
            return false;
        }
    }
}
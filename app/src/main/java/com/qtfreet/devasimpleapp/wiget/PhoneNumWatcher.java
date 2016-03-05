package com.qtfreet.devasimpleapp.wiget;

import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by hehe on 2016/3/4.
 */
public class PhoneNumWatcher implements TextWatcher {
    int maxLeng;

    EditText editText;
    Button btn;


    public PhoneNumWatcher(int len, EditText edt) {
        this.maxLeng = len;
        this.editText = edt;
    }

    public PhoneNumWatcher(int len, EditText edt, Button button) {
        this.maxLeng = len;
        this.editText = edt;
        this.btn = button;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Editable editable = editText.getText();
        int len = editable.length();
        int maxLen = maxLeng;
        if (len > maxLen) {
            int selEndIndex = Selection.getSelectionEnd(editable);
            String str = editable.toString();
            String newStr = str.substring(0, maxLen);
            editText.setText(newStr);
            editable = editText.getText();
            int newLen = editable.length();
            if (selEndIndex > newLen) {
                selEndIndex = editable.length();
            }
            Selection.setSelection(editable, selEndIndex);
        } else if (len == maxLen) {
            btn.setClickable(true);
            btn.setTextColor(Color.BLACK);
        } else if (len < maxLen) {
            btn.setClickable(false);
            btn.setTextColor(Color.GRAY);

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

package com.smapley.base.activity;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smapley.base.R;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by wuzhixiong on 2017/3/19.
 * 标题栏
 */

public class MyActionBarActivity extends AppCompatActivity {

    private TextView tab1;
    private TextView tab2;

    protected void showBg() {
        View view = findViewById(R.id.action_bar_layout);
        view.setBackgroundResource(R.color.base);
    }

    protected void showSearch() {
        View view = findViewById(R.id.tbar_search_layout);
        view.setVisibility(View.VISIBLE);
        EditText editText = (EditText) findViewById(R.id.tbar_search_text);
        View search = findViewById(R.id.tbar_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editText.getText().toString();
                if (StringUtils.isNoneEmpty(data)) {
                    onSearch(data);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                }
            }
        });
    }

    protected void onSearch(String data) {
    }

    public void setTitle(int title) {
        setTitle(getString(title));

    }

    public void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);
    }


    protected void showBack() {
        View back = findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });

    }

    protected void onBackClick() {
        finish();
    }

    protected void showRightText(int text) {
        TextView textView = (TextView) findViewById(R.id.right_text);
        textView.setText(getString(text));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightText(v);
            }
        });

    }

    protected void showConfirm() {
        View confirm = findViewById(R.id.confirm);
        confirm.setVisibility(View.VISIBLE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm(v);
            }
        });
    }

    protected void showCenterImg(Integer imgSrc) {
        ImageView imageView = (ImageView) findViewById(R.id.center_img);
        imageView.setImageResource(imgSrc);
        imageView.setVisibility(View.VISIBLE);
    }

    protected void showRightImg(Integer imgSrc) {
        ImageView imageView = (ImageView) findViewById(R.id.right_img);
        imageView.setImageResource(imgSrc);
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRightImg(v);
            }
        });
    }

    protected void onRightText(View v) {

    }

    protected void onRightImg(View v) {
    }

    protected void onConfirm(View view) {

    }


    protected void showTab(int name1, int name2) {

        View tab = findViewById(R.id.tab);
        tab.setVisibility(View.VISIBLE);
        tab1 = (TextView) tab.findViewById(R.id.tab1);
        tab2 = (TextView) tab.findViewById(R.id.tab2);
        tab1.setText(getString(name1));
        tab2.setText(getString(name2));
        tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseTab(0);
            }
        });
        tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseTab(1);
            }
        });
    }

    protected void choseTab(int position) {
        if (position == 0) {
            tab1.setBackgroundResource(R.mipmap.icon_tab_a_pre);
            tab1.setTextColor(ContextCompat.getColor(this, R.color.brown9));
            tab2.setBackgroundResource(R.mipmap.icon_tab_b_nor);
            tab2.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            tab1.setBackgroundResource(R.mipmap.icon_tab_a_nor);
            tab1.setTextColor(ContextCompat.getColor(this, R.color.white));
            tab2.setBackgroundResource(R.mipmap.icon_tab_b_pre);
            tab2.setTextColor(ContextCompat.getColor(this, R.color.brown9));
        }
    }
}

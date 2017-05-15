package com.smapley.base.utils;

import android.app.Activity;

import com.smapley.base.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * Created by smapley on 15/10/22.
 */
public class ActivityStack {

    private static ActivityStack mSingleInstance;
    private Stack<Activity> mActivityStack;

    private ActivityStack() {
        mActivityStack = new Stack<Activity>();
    }

    public static ActivityStack getInstance() {
        if (null == mSingleInstance) {
            mSingleInstance = new ActivityStack();
        }

        return mSingleInstance;
    }

    public Stack<Activity> getStack() {
        return mActivityStack;
    }

    /**
     * 入栈
     */
    public void addActivity(Activity activity) {
        mActivityStack.push(activity);
    }

    /**
     * 出栈
     */
    public void removeActivity(Activity activity) {
        mActivityStack.remove(activity);
    }

    /**
     * 彻底退出
     */
    public void finishAllActivity() {
        Activity activity;
        while (!mActivityStack.empty()) {
            activity = mActivityStack.pop();
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void finishActivityButMain() {
        Activity activity;
        while (mActivityStack.size() > 1) {
            activity = mActivityStack.pop();
            if (activity != null ) {
                activity.finish();
            }
        }
    }

    /**
     * finish制定的Activity
     */
    public boolean finishActivity(Class<? extends Activity> actCls) {
        Activity activity = findActivityByClass(actCls);
        if (null != activity && !activity.isFinishing()) {
            activity.finish();
            return true;
        }
        return false;
    }

    public Activity findActivityByClass(Class<? extends Activity> actCls) {
        Activity activity = null;
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            activity = iterator.next();
            if (null != activity && activity.getClass().getName().equals(actCls.getName()) && !activity.isFinishing()) {
                break;
            }
            activity = null;
        }

        return activity;
    }

    /**
     * finish指定的Activity之上的所有Activity
     */
    public boolean finishToActivity(Class<? extends Activity> actCls, boolean isIncludeSelf) {
        List<Activity> buf = new ArrayList<>();
        int size = mActivityStack.size();
        Activity activity = null;
        for (int i = size - 1; i >= 0; i--) {
            activity = mActivityStack.get(i);
            if (activity.getClass().isAssignableFrom(actCls)) {
                for (Activity a : buf) {
                    a.finish();
                }
                return true;
            } else if (i == size - 1 && isIncludeSelf) {
                buf.add(activity);
            } else if (i != size - 1) {
                buf.add(activity);
            }
        }
        return false;
    }

}

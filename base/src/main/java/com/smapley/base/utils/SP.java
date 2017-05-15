package com.smapley.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wuzhixiong on 2017/5/12.
 */

public class SP {

    private static SharedPreferences userShared;
    private static SharedPreferences setShared;
    private static SharedPreferences dataShared;
    private static Context mcontext;


    public static void init(Context context) {
        mcontext = context;
        //初始化本地存储
        userShared = mcontext.getSharedPreferences(BaseConstant.SP_USER, MODE_PRIVATE);
        setShared = mcontext.getSharedPreferences(BaseConstant.SP_SET, MODE_PRIVATE);
        dataShared = mcontext.getSharedPreferences(BaseConstant.SP_DATA, MODE_PRIVATE);
    }


    public static void saveUser(String key, Object value) {
        save(userShared, key, value, true);
    }

    public static void saveSet(String key, Object value) {
        save(setShared, key, value, true);
    }

    public static Object getSet(String key) {
        return getSet(key, null);
    }

    public static Object getSet(String key, Object defaultValue) {
        return get(setShared, key, defaultValue);
    }

    public static Object getUser(String key) {
        return getUser(key, null);
    }

    public static Object getUser(String key, Object defaultValue) {
        return get(userShared, key, defaultValue);
    }

    public static void clearUser() {
        clear(userShared);
    }

    public static void clearSet() {
        clear(setShared);
    }

    private static void save(SharedPreferences shared, String key, Object value, boolean record) {
        SharedPreferences.Editor edit = shared.edit();
        if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
            if (record)
                save(dataShared, key, Integer.class.getName(), false);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
            if (record)
                save(dataShared, key, String.class.getName(), false);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
            if (record)
                save(dataShared, key, Boolean.class.getName(), false);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
            if (record)
                save(dataShared, key, Float.class.getName(), false);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
            if (record)
                save(dataShared, key, Long.class.getName(), false);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
            if (record)
                save(dataShared, key, Set.class.getName(), false);
        }
        edit.apply();
    }

    private static Object get(SharedPreferences shared, String key, Object defaultValue) {
        String dataType = dataShared.getString(key, "");
        if (StringUtils.isEmpty(dataType))
            return defaultValue;
        if (dataType.equals(Integer.class.getName())) {
            return shared.getInt(key, 0);
        } else if (dataType.equals(String.class.getName())) {
            return shared.getString(key, "");
        } else if (dataType.equals(Boolean.class.getName())) {
            return shared.getBoolean(key, false);
        } else if (dataType.equals(Float.class.getName())) {
            return shared.getFloat(key, (float) 0);
        } else if (dataType.equals(Long.class.getName())) {
            return shared.getLong(key, (long) 0);
        } else if (dataType.equals(Set.class.getName())) {
            return shared.getStringSet(key, new HashSet<>());
        }
        return defaultValue;
    }

    private static void clear(SharedPreferences shared) {
        shared.edit().clear().apply();
    }

}

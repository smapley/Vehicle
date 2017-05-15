package com.smapley.base.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.activity.ForgetActivity;

import org.xutils.x;

/**
 * Created by eric on 2017/3/18.
 * 基础
 */

public abstract class BaseFragment extends Fragment {

    private boolean injected =false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        injected = true;
        View view =x.view().inject(this,inflater,container);
        initArgument();
        initView();
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!injected){
            x.view().inject(this,this.getView());
        }
    }

    public abstract void initArgument();

    public abstract void initView();


    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }
}

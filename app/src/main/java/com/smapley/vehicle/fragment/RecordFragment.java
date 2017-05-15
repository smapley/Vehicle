package com.smapley.vehicle.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.smapley.base.fragment.BaseFragment;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.vehicle.R;
import com.smapley.vehicle.activity.PayActivity;
import com.smapley.vehicle.adapter.RecordAdapter;
import com.smapley.vehicle.http.Pay;
import com.smapley.vehicle.utils.Constant;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wuzhixiong on 2017/5/9.
 */
@ContentView(R.layout.fragment_record)
public class RecordFragment extends BaseFragment {

    @ViewInject(R.id.record_listView)
    private ListView listView;

    private RecordAdapter adapter;
    private List<Map> list;

    @Override
    public void initArgument() {

    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        adapter = new RecordAdapter(getContext(),list);
        listView.setAdapter(adapter);
        listView.setDivider(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).get("bcid") != null)
                    upData(null, list.get(position).get("bcid").toString());
            }
        });
    }

    private void upData(String mess, String bcid) {
        RequestParams params = new RequestParams(Constant.URL_GETSJNAME);
        params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
        params.addBodyParameter("bcid", bcid);
        params.addBodyParameter("mess", mess);
        x.http().post(params, new BaseCallback<Pay>() {
            @Override
            public void success(Pay result) {
                if (result.getResult() != null && !result.getResult().isEmpty()) {
                    Intent intent = new Intent(getActivity(), PayActivity.class);
                    intent.putExtra("data", new Gson().toJson(result.getResult()));
                    startActivity(intent);
                }
            }
        });
    }

    public void setData(List lsfkdx) {
        list.addAll(lsfkdx);
        adapter.notifyDataSetChanged();
    }
}

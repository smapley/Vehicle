package com.smapley.vehicle.activity;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.google.gson.Gson;
import com.smapley.base.activity.BaseActivity;
import com.smapley.base.adapter.MyFragmentAdapter;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.base.utils.ShakeManager;
import com.smapley.base.utils.ThreadSleep;
import com.smapley.base.widget.CircleImageView;
import com.smapley.vehicle.R;
import com.smapley.vehicle.fragment.DetailFragment;
import com.smapley.vehicle.fragment.RecordFragment;
import com.smapley.vehicle.http.MainResponse;
import com.smapley.vehicle.http.Pay;
import com.smapley.vehicle.utils.BluetoothUtils;
import com.smapley.vehicle.utils.Constant;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.Image;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.main_gold)
    private TextView gold;
    @ViewInject(R.id.main_image)
    private CircleImageView iamge;

    @ViewInject(R.id.main_viewPage)
    private ViewPager viewPager;

    private RecordFragment recordFragment;
    private DetailFragment detailFragment;

    private ShakeManager shakeManager;
    private BluetoothUtils bluetoothUtils;


    private Map<String, String> map = new HashMap<>();
    private ThreadSleep  blueThread = new ThreadSleep();


    @Override
    public void initArgument() {
        getData();

    }

    private void upData(String mess, String bcid) {
        if (checkLogin()) {
            RequestParams params = new RequestParams(Constant.URL_GETSJNAME);
            params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
            params.addBodyParameter("bcid", bcid);
            params.addBodyParameter("mess", mess);
            x.http().post(params, new BaseCallback<Pay>() {
                @Override
                public void success(Pay result) {
                    try {
                        if (result.getResult() != null && !result.getResult().isEmpty()) {
                            Intent intent = new Intent(MainActivity.this, PayActivity.class);
                            intent.putExtra("data", new Gson().toJson(result.getResult()));
                            startActivity(intent);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    @Override
    public void initView() {
        setTitle("Username");
        showRightImg(R.mipmap.icon_mine);
        isExit = true;

        initTab();


        //加载蓝牙
        bluetoothUtils = new BluetoothUtils(this, new BluetoothUtils.BlueToothCallback() {

            @Override
            public void before() {
                map.clear();
            }

            @Override
            public void onSearch(String mac, int rssi, String data) {
                map.put(mac, mac + "," + rssi + "," + data);
            }

            @Override
            public void onStop() {
                blueThread.sleep(10000, new ThreadSleep.Callback() {
                    @Override
                    public void onCallback(int number) {
                        if (rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            bluetoothUtils.start();
                        }
                    }
                });
            }
        });
        //请求定位权限
        rxPermissions
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        bluetoothUtils.start();
                    } else {
                    }
                });


        //摇一摇
        shakeManager = new ShakeManager(this);
        shakeManager.setShakeCallback(new ShakeManager.ShakeCallback() {
            @Override
            public void onShake() {
                if (!rxPermissions.isGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showToast(R.string.location_permission);
                }else{
                    bluetoothUtils.start();
                }
            }

            @Override
            public void endShake() {
                try {
                    if (!map.isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String data : map.values()) {
                            stringBuilder.append(data).append(",");
                        }
                        String mess = stringBuilder.toString();
                        if (!StringUtils.isEmpty(mess)) {
                            mess = mess.substring(0, mess.length() - 1);
                            upData(mess, null);
                        }
                    }else {
                        showToast(R.string.main_no_message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initTab() {
        List<Fragment> fragments = new ArrayList<>();
        recordFragment = new RecordFragment();
        detailFragment = new DetailFragment();
        fragments.add(recordFragment);
        fragments.add(detailFragment);
        viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), fragments));

    }

    @Event({R.id.main_withdraw, R.id.main_recharge, R.id.main_shake, R.id.main_record, R.id.main_detail, R.id.main_image})
    private void onClick(View view) {
        if (checkLogin())
            switch (view.getId()) {
                case R.id.main_record:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.main_detail:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.main_recharge:
                    startActivity(new Intent(MainActivity.this, RechargeActivity.class));
                    break;
                case R.id.main_withdraw:
                    startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                    break;
                case R.id.main_shake:
                    shakeManager.startShake();
                    break;
                case R.id.main_image:
                    selectPic();
                    break;
            }
    }


    /**
     * 从相册选择头像
     */
    private void selectPic() {
        int selectedMode = MultiImageSelectorActivity.MODE_SINGLE;
        boolean showCamera = true;
        int maxNum = 1;
        Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
//                if (mSelectPath != null && mSelectPath.size() > 0) {
//                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
//                }
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && requestCode == 0) {
                List<String> resultList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Uri uri = Uri.parse(resultList.get(0));
                iamge.setImageURI(uri);
            }
        } catch (Exception e) {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        shakeManager.start();
    }


    @Override
    protected void onPause() {
        shakeManager.close();
        super.onPause();
    }


    public void getData() {
        RequestParams params = new RequestParams(Constant.URL_MAIN);
        params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
        x.http().post(params, new BaseCallback<MainResponse>() {
            @Override
            public void success(MainResponse result) {
                gold.setText(result.getSygold());
                recordFragment.setData(result.getLsfkdx());
            }
        });

    }

    @Override
    protected void onRightImg(View v) {
        if (checkLogin())
            startActivity(new Intent(MainActivity.this, UserActivity.class));

    }

    @Override
    protected void onDestroy() {
        blueThread.stop();
        super.onDestroy();
    }
}

package com.smapley.vehicle.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.base.widget.CircleImageView;
import com.smapley.vehicle.R;
import com.smapley.vehicle.http.Set;
import com.smapley.vehicle.utils.Constant;
import com.smapley.vehicle.utils.LicenseKeyboardUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by wuzhixiong on 2017/5/20.
 */
@ContentView(R.layout.activity_set
)
public class SetActivity extends BaseActivity {

    @ViewInject(R.id.set_cp1)
    private EditText inputbox1;
    @ViewInject(R.id.set_cp2)
    private EditText inputbox2;
    @ViewInject(R.id.set_cp3)
    private EditText inputbox3;
    @ViewInject(R.id.set_cp4)
    private EditText inputbox4;
    @ViewInject(R.id.set_cp5)
    private EditText inputbox5;
    @ViewInject(R.id.set_cp6)
    private EditText inputbox6;
    @ViewInject(R.id.set_cp7)
    private EditText inputbox7;

    @ViewInject(R.id.set_image)
    private ImageView image;


    private LicenseKeyboardUtil keyboardUtil;

    @Override
    public void initArgument() {
    }

    private void getData() {
        RequestParams params = new RequestParams(Constant.URL_GETSHEZHI);
        params.addBodyParameter("ukey", (String) SP.getUser(Constant.SP_UKEY));
        x.http().post(params, new BaseCallback<Set>() {
            @Override
            public void success(Set result) {
                keyboardUtil.setData(result.getCp());
                x.image().bind(image, Constant.URL_IMG + result.getPic(), circleImage);
            }
        });
    }


    @Override
    public void initView() {
        getData();
        keyboardUtil = new LicenseKeyboardUtil(this, new EditText[]{inputbox1, inputbox2, inputbox3,
                inputbox4, inputbox5, inputbox6, inputbox7});
    }

    @Event({R.id.set_image, R.id.set_conform})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_image:
                selectPic();
                break;
            case R.id.set_conform:
                checkCP();
                break;
        }
        keyboardUtil.hideKeyboard();
    }

    private void checkCP() {
        String cp = keyboardUtil.getData();
        if (StringUtils.isNoneEmpty(cp) && cp.length() == 7) {
            updateCP(cp);
        } else {
            showToast(R.string.set_right_cp);
        }
    }

    private void updateCP(String cp) {
        RequestParams params = new RequestParams(Constant.URL_UPDATESHEZHI);
        params.addBodyParameter("ukey", (String) SP.getUser("ukey"));
        params.addBodyParameter("cp", cp);
        x.http().post(params, new BaseCallback<Set>() {
            @Override
            public void success(Set result) {
                keyboardUtil.setData(result.getCp());
                showToast(R.string.set_cp_success);
            }
        });
    }

    private void updatePic(File file) {
        if (file != null) {
            RequestParams params = new RequestParams(Constant.URL_ADDPHOTO);
            params.addBodyParameter("ukey", (String) SP.getUser("ukey"));
            params.addBodyParameter("name", file);
            x.http().post(params, new BaseCallback<Map>() {
                @Override
                public void success(Map result) {
                    x.image().bind(image, Constant.URL_IMG + result.get("filename"), circleImage);
                }
            });
        }

    }

    /**
     * 从相册选择头像
     */
    private void selectPic() {
        int selectedMode = MultiImageSelectorActivity.MODE_SINGLE;
        boolean showCamera = true;
        int maxNum = 1;
        Intent intent = new Intent(SetActivity.this, MultiImageSelectorActivity.class);
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
                File file = new File(resultList.get(0));
                updatePic(file);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected boolean onBack(int keyCode, KeyEvent event) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard();
            return false;
        } else {
            return super.onBack(keyCode, event);
        }
    }
}

package com.smapley.vehicle.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.smapley.base.activity.BaseActivity;
import com.smapley.base.http.BaseCallback;
import com.smapley.base.utils.SP;
import com.smapley.vehicle.R;
import com.smapley.vehicle.http.Set;
import com.smapley.vehicle.utils.Constant;
import com.smapley.vehicle.utils.LicenseKeyboardUtil;
import com.yalantis.ucrop.UCrop;

import org.apache.commons.lang3.StringUtils;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static android.R.attr.maxHeight;
import static android.R.attr.maxWidth;

/**
 * Created by wuzhixiong on 2017/5/20.
 */
@ContentView(R.layout.activity_set)
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
                if (StringUtils.isNoneEmpty(result.getPic()))
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
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在上传照片。。。");
            RequestParams params = new RequestParams(Constant.URL_ADDPHOTO);
            params.addBodyParameter("ukey", (String) SP.getUser("ukey"));
            params.addBodyParameter("name", file);
            dialog.show();
            x.http().post(params, new BaseCallback<Map>(dialog) {
                @Override
                public void success(Map result) {
                    if (StringUtils.isNoneEmpty(result.get("filename").toString()))
                        x.image().bind(image, Constant.URL_IMG + result.get("filename"), circleImage);
                }
            });
        }

    }

    /**
     * 从相册选择头像
     */
    private void selectPic() {
        MultiImageSelector.create()
                .showCamera(true) // 是否显示相机. 默认为显示
                .single() // 单选模式
                .start(this, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case 0:
                        List<String> resultList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                        //裁剪图片
                        Uri sourceUri = Uri.fromFile(new File(resultList.get(0)));
                        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "SampleCropImage.jpeg"));
                        UCrop.of(sourceUri, destinationUri)
                                .withAspectRatio(1, 1)
                                .withMaxResultSize(maxWidth, maxHeight)
                                .start(SetActivity.this);
                        break;
                    case UCrop.REQUEST_CROP:
                        //上传图片
                        final Uri resultUri = UCrop.getOutput(data);
                        File file = new File(resultUri.getPath());
                        updatePic(file);
                        break;
                }


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

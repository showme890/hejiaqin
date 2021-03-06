package com.chinamobile.hejiaqin.business.ui.more;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chinamobile.hejiaqin.R;
import com.chinamobile.hejiaqin.business.BussinessConstants;
import com.chinamobile.hejiaqin.business.logic.setting.ISettingLogic;
import com.chinamobile.hejiaqin.business.manager.UserInfoCacheManager;
import com.chinamobile.hejiaqin.business.ui.basic.BasicActivity;
import com.chinamobile.hejiaqin.business.ui.basic.dialog.PhotoManage;
import com.chinamobile.hejiaqin.business.ui.basic.view.HeaderView;
import com.customer.framework.component.qrcode.QRCodeDecoder;
import com.customer.framework.component.qrcode.ZXingView;
import com.customer.framework.component.qrcode.core.QRCodeView;
import com.customer.framework.utils.FileUtil;
import com.customer.framework.utils.LogUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by eshaohu on 16/6/1.
 */
public class ScanActivity extends BasicActivity implements View.OnClickListener,
        QRCodeView.Delegate, QRCodeDecoder.Delegate {
    private HeaderView mHeaderView;
    private QRCodeView mQRCodeView;
    private ISettingLogic settingLogic;
    private final static String TAG = "ScanActivity";
    public static final String IMAGE_TYPE = "image/*";
    public static final int IMAGE_CODE = 1;//相册

    @Override
    protected void handleStateMessage(Message msg) {
        super.handleStateMessage(msg);
        switch (msg.what) {
            case BussinessConstants.SettingMsgID.STATUS_DELIVERY_OK:
            case BussinessConstants.SettingMsgID.STATUS_DISPLAY_OK:
                showToast(R.string.waiting_for_respond);
                break;
            case BussinessConstants.SettingMsgID.STATUS_SEND_FAILED:
            case BussinessConstants.SettingMsgID.STATUS_UNDELIVERED:
                showToast(getString(R.string.sending_bind_request_failed), Toast.LENGTH_LONG, null);
                break;
            case BussinessConstants.SettingMsgID.BIND_SUCCESS:
                showToast("绑定成功", Toast.LENGTH_SHORT, null);
                settingLogic.bindSuccNotify();
                doBack();
                break;
            case BussinessConstants.SettingMsgID.SENDING_BIND_REQUEST:
                showToast(getString(R.string.sending_bind_request), Toast.LENGTH_SHORT, null);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initView() {
        mQRCodeView = (ZXingView) findViewById(R.id.more_zxingview);
        mHeaderView = (HeaderView) findViewById(R.id.more_scan_header);
        mHeaderView.title.setText(getString(R.string.more_bind_tv_dialog_by_scan));
        mHeaderView.backImageView.setImageResource(R.mipmap.title_icon_back_nor);
        mHeaderView.backImageView.setClickable(true);
        mHeaderView.rightBtn.setVisibility(View.GONE);
        mHeaderView.setadd.setVisibility(View.VISIBLE);
        mHeaderView.tvRight.setVisibility(View.VISIBLE);
        mHeaderView.tvRight.setText(getString(R.string.more_album));
        mHeaderView.tvRight.setClickable(true);
    }

    @Override
    protected void initDate() {

    }

    @Override
    protected void initListener() {
        mQRCodeView.setResultHandler(this);
        mHeaderView.backImageView.setOnClickListener(this);
        mHeaderView.tvRight.setOnClickListener(this);
    }

    @Override
    protected void initLogics() {
        settingLogic = (ISettingLogic) getLogicByInterfaceClass(ISettingLogic.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.tvRight:
                Intent getAlbum = new Intent(); // "android.intent.action.GET_CONTENT"
                if (Build.VERSION.SDK_INT < 19) {
                    getAlbum.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    //                    getAlbum.setAction(Intent.ACTION_OPEN_DOCUMENT);
                    getAlbum.setAction(Intent.ACTION_PICK);
                }
                getAlbum.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(getAlbum, "选择二维码图片");
                startActivityForResult(wrapperIntent, IMAGE_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CODE:
                String photoPath = null;
                String[] proj = { MediaStore.Images.Media.DATA };
                // 获取选中图片的路径
                Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);

                if (cursor.moveToFirst()) {

                    int columnIndexOrThrow = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    photoPath = cursor.getString(columnIndexOrThrow);
                    if (photoPath == null) {
                        photoPath = PhotoManage.getPath(getApplicationContext(), data.getData());
                        LogUtil.i(TAG, photoPath);
                    }
                    LogUtil.i(TAG, photoPath);

                }

                cursor.close();

                decodeQECodeFromAlbum(photoPath);
                break;
            default:
                break;
        }
    }

    private void decodeQECodeFromAlbum(String photoPath) {
        long photoSize = FileUtil.getFileSize(photoPath);
        if (photoSize <= 0) {
            showToast("图片异常", Toast.LENGTH_LONG, null);
            return;
        }
        Bitmap bm = null;

        if (photoSize > 1024 * 1024 * 10) {
            showToast("图片过大", Toast.LENGTH_LONG, null);
            return;
        }
        if (photoSize > 1024 * 1024 * 2) {
            InputStream is = null;
            try {
                is = new FileInputStream(photoPath);
            } catch (FileNotFoundException e) {
                LogUtil.e(TAG, "File not found", e);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    LogUtil.i(tag, e.getMessage());
                }
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 10; //width，hight设为原来的十分一
            bm = BitmapFactory.decodeStream(is, null, options);
        } else {
            bm = BitmapFactory.decodeFile(photoPath);
        }
        QRCodeDecoder.decodeQRCode(bm, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeView.startSpotAndShowRect();
    }

    @Override
    protected void onPause() {
        mQRCodeView.stopSpot();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogUtil.d(TAG, "result:" + result);
        vibrate();
        //TODO finish();
        if (result.length() > 0) {
            settingLogic.sendBindReq(result,
                    UserInfoCacheManager.getUserInfo(getApplicationContext()).getPhone());
            //            finish();
        } else {
            showToast("错误的二维码", Toast.LENGTH_SHORT, null);
            mQRCodeView.startSpotAndShowRect();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onDecodeQRCodeSuccess(String result) {
        vibrate();
        if (result.length() > 0) {
            settingLogic.sendBindReq(result,
                    UserInfoCacheManager.getUserInfo(getApplicationContext()).getPhone());
            //finish();
        } else {
            showToast("错误的二维码", Toast.LENGTH_SHORT, null);
            mQRCodeView.startSpotAndShowRect();
        }
    }

    @Override
    public void onDecodeQRCodeFailure() {
        Toast.makeText(this, "不能识别的图片二维码", Toast.LENGTH_SHORT).show();
    }
}

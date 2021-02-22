package com.ev.opencvlena.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.ev.opencvlena.MainActivity;
import com.ev.opencvlena.TessTwoHelper;
import com.ev.opencvlena.util.Constant;

import org.opencv.android.CameraBridgeViewBase;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 权限申请
 *
 * @Author Administrator
 * @Date 2020/11/26 2020/11/26
 * @Version V1.0
 **/
public class Power {
    /**
     * 获取相机权限
     *
     * @param activity 活动对象
     */
//    public static void getCamera(FragmentActivity activity) {
//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(activity, "请授予摄像头权限", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, Constant.CAMERA_POWER);
//        } else { //有权限直接调用系统相机拍照
//        }
//    }

    /**
     * 获取权限
     *
     * @param activity  活动对象
     * @param powerName 权限名称数组
     */
    public static void getPower(FragmentActivity activity, String[] powerName, CameraBridgeViewBase camer) {
        boolean isGranted = true;
        for (String name : powerName) {
            if (ActivityCompat.checkSelfPermission(activity, name) != PackageManager.PERMISSION_GRANTED)
                isGranted = false;
        }
        if (!isGranted) {
            Toast.makeText(activity, "请授予权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(activity, powerName, Constant.POWER);
        } else { //有权限直接调用系统相机拍照
            camer.enableView();// 使能相机
            if (!TessTwoHelper.tessTwoFileExist())
                TessTwoHelper.initTessTwoFile(activity);// 初始化TessTwo数据文件
        }
    }

}

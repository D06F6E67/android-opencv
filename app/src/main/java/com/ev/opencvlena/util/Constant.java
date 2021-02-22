package com.ev.opencvlena.util;

import android.Manifest;

import org.opencv.core.Rect;

import java.io.File;

/**
 * 常量类
 *
 * @Author Administrator
 * @Date 2020/11/30 2020/11/30
 * @Version V1.0
 **/
public class Constant {
    // >>>>>>>>>>>>>>>>>>>> 权限相关 >>>>>>>>>>>>>>>>>>>>
    /**默认权限数组*/
    public static final String[] POWER_NAME = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    /**权限返回值*/
    public static final int POWER = 0;
    // <<<<<<<<<<<<<<<<<<<< 权限相关 <<<<<<<<<<<<<<<<<<<<

    // >>>>>>>>>>>>>>>>>>>> TessTwo相关 >>>>>>>>>>>>>>>>>>>>
    /**TessTwo语言数据路径*/
    public static final String TESS_TWO_DATA_PATH = "/storage/emulated/0/tessdata";
    /**TessTwo语言数据名称*/
    public static final String[] TESS_TWO_DATA_NAME = {"eng.traineddata","number.traineddata"};
    /**TessTwo语言名*/
    public static final String TESS_TWO_LANGUAGE = "eng+number";
    // <<<<<<<<<<<<<<<<<<<< TessTwo相关 <<<<<<<<<<<<<<<<<<<<

    // >>>>>>>>>>>>>>>>>>>> 图像识别相关 >>>>>>>>>>>>>>>>>>>>
    /**图像保存目录*/
    public static final String SAVE_BITMAP_PATH = "/storage/emulated/0/bitmap";
    /**默认识别范围*/
    public static final Rect RANGE = new Rect(200 , 200, 300, 100);
//    public static final Rect RANGE = new Rect(0 , 0, 1280, 720);
    /**画笔宽度*/
    public static final int BRUSH_WIDE = 1;
    /**点击移动最小距离*/
    public static final int MIN_MOVE = 10;
    /**二值化默认值*/
    public static final int THRESH = 60;
    /**阈值调节小范围*/
    public static final int ADJUST_FEW = 1;
    /**阈值调节大范围*/
    public static final int ADJUST_MORE = 10;
    /**阈值最小值*/
    public static final int ADJUST_MIN = 0;
    /**阈值最大值*/
    public static final int ADJUST_MAX = 255;
    // <<<<<<<<<<<<<<<<<<<< 图像识别相关 <<<<<<<<<<<<<<<<<<<<
}

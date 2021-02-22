package com.ev.opencvlena;

import android.graphics.Bitmap;
import android.util.Log;

import com.ev.opencvlena.util.Constant;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.Canny;
import static org.opencv.imgproc.Imgproc.blur;

/**
 * 图像处理类
 *
 * @Author Administrator
 * @Date 2020/11/30 2020/11/30
 * @Version V1.0
 **/
public class ImageProcessing {
    private static String TAG = "ImageProcessing";
    /**
     * 过渡Mat
     */
    private static Mat midMat = new Mat();
    /**
     * 处理Mat
     */
    private static Mat disposeMat = new Mat();
    /**
     * 颜色
     */
    private static Scalar scalar = new Scalar(255, 0, 0);
    /**
     * 识别区域
     */
    private static Rect range;
    /**
     * 二值化阈值
     */
    private static int thresh;

    private static TessTwoHelper tessTwoHelper = new TessTwoHelper();// 数字识别对象

    /**
     * 边缘检测
     *
     * @param inMat 要处理的图像
     */
    public static void canny(Mat inMat) {
        blur(inMat, midMat, new Size(3, 3)); // 模糊图像
        Canny(midMat, inMat, 100, 60); // 边缘检测
    }

    /**
     * 设置识别区域
     *
     * @param range 识别区域
     */
    public static void setRange(Rect range) {
        if (range.width > Constant.MIN_MOVE && range.height > Constant.MIN_MOVE)
            ImageProcessing.range = range;
    }

    /**
     * 获得识别区域
     *
     * @return 识别区域
     */
    public static Rect getRange() {
        return range;
    }

    /**
     * 设置二值化阈值
     *
     * @param thresh 二值化阈值
     */
    public static void setThresh(int thresh) {
        if (thresh < Constant.ADJUST_MIN) thresh = Constant.ADJUST_MIN;
        if (thresh > Constant.ADJUST_MAX) thresh = Constant.ADJUST_MAX;
        ImageProcessing.thresh = thresh;
    }

    /**
     * 处理Mat
     *
     * @param inMat 要处理的mat
     * @return 处理后的Bitmap
     */
    public static Bitmap disposeMat(Mat inMat) {
        // 截取范围
        if (range.empty()) range = Constant.RANGE;
        Range rowRange = new Range(range.y + Constant.BRUSH_WIDE, range.y + range.height - Constant.BRUSH_WIDE);
        Range colRange = new Range(range.x + Constant.BRUSH_WIDE, range.x + range.width - Constant.BRUSH_WIDE);
        // 判断输入Mat是否为空，是否超出范围
        if (inMat == null || !(0 <= rowRange.start && rowRange.start <= rowRange.end && rowRange.end <= inMat.rows()) ||
                !(0 <= colRange.start && colRange.start <= colRange.end && colRange.end <= inMat.cols()))
            return null;
        // 截取出识别区域
        midMat = new Mat(inMat, rowRange, colRange);
        // 灰度
        Imgproc.cvtColor(midMat, midMat, Imgproc.COLOR_RGB2GRAY);
        // 二值化
        Imgproc.threshold(midMat, disposeMat, thresh, 255, Imgproc.THRESH_BINARY_INV);
        // 将Mat转为bitmap
        Bitmap bitmap = Bitmap.createBitmap(disposeMat.width(), disposeMat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(disposeMat, bitmap);
        return bitmap;
    }

    /**
     * 数字识别
     *
     * @param bitmap Bitmap数组
     * @return 识别出的内容
     */
    public static String numberDisccern(Bitmap bitmap) {
        // 初始化数字识别
        tessTwoHelper.init();
        return tessTwoHelper.getText(bitmap);
    }

    /**
     * 绘制识别区域 (区域范围通过setRange设置)
     *
     * @param inMat 识别图像
     */
    public static void showRange(Mat inMat) {
        // 绘制出识别区域
        if (range.empty()) range = Constant.RANGE;
        Imgproc.rectangle(inMat, range, scalar, Constant.BRUSH_WIDE);
    }

    /**
     * 释放变量
     */
    public static void release() {
        midMat.release();
        disposeMat.release();
        scalar.clone();
    }
}

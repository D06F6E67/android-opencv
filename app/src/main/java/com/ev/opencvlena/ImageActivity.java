package com.ev.opencvlena;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ev.opencvlena.util.Click;
import com.ev.opencvlena.util.Constant;
import com.ev.opencvlena.util.FileStorageHelper;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * 图片处理测试
 *
 * @Author Administrator
 * @Date 2020/12/11 2020/12/11
 * @Version V1.0
 **/
public class ImageActivity extends AppCompatActivity{

    private static String TAG = "ImageActivity";

    /**显示要处理图片*/
    private ImageView frontImageView;
    /**显示处理后图片*/
    private ImageView laterImageView;
    /**阈值增加按钮*/
    private Button addButton;
    /**阈值显示*/
    private TextView threshText;
    /**阈值减少按钮*/
    private Button minusButton;
    /**保存按钮*/
    private Button saveButton;
    /**显示识别内容*/
    private TextView resultText;

    Bitmap frontBitmap;
    Bitmap laterBitmap;
    Mat mat = new Mat();
    /**阈值*/
    private static int threshInt = Constant.THRESH;
    /**识别内容*/
    private static String resultString;
    static {
        OpenCVLoader.initDebug();
    }
    /**定时任务*/
    Handler handler = new Handler();
    Runnable showText = new Runnable() {
        @Override
        public void run() {
            laterBitmap = ImageProcessing.disposeMat(mat);
            resultString = ImageProcessing.numberDisccern(laterBitmap);
            threshText.setText(Integer.valueOf(threshInt).toString());
            resultText.setText(resultString);
            laterImageView.setImageBitmap(laterBitmap);
            handler.postDelayed(this, 500);// 每0.5秒启动
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // 去除页面的标题
        setContentView(R.layout.activity_image);

        TessTwoHelper.initTessTwoFile(this);

        initView();
        initEvent();

        /**
         * number1 x80 y90 w600 h90 t60
         * number2 x80 y140 w400 h100 t60
         * number_ w430 h75 t60  6t70
         * test_1 w105 h85 t110
         * test_2 w180 h70 t60 y70
         * test_3 w530 h180 t60
         */
        // 图像转为bitmap
        frontBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.number2);
        Utils.bitmapToMat(frontBitmap, mat);
        // 设置识别范围
        ImageProcessing.setRange(new Rect(80,140, 400, 100));
        handler.postDelayed(showText, 500);// 启动线程
        // 显示识别范围
        ImageProcessing.showRange(mat);
        // 原图像添加识别范围后转为bitmap
        Utils.matToBitmap(mat, frontBitmap);
        frontImageView.setImageBitmap(frontBitmap);
    }

    /**
     * 初始化控件
     */
    private void initView(){
        frontImageView = findViewById(R.id.frontImageView);
        laterImageView = findViewById(R.id.laterImageView);
        addButton = findViewById(R.id.addButton);
        threshText = findViewById(R.id.threshText);
        minusButton = findViewById(R.id.minusButton);
        saveButton = findViewById(R.id.saveButton);
        resultText = findViewById(R.id.resultText);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        addButton.setOnTouchListener(new Click(new Click.CustomClick() {
            @Override
            public void oneClick() {
                threshAdjust(Constant.ADJUST_FEW);
            }

            @Override
            public void doubleClick() {
                threshAdjust(Constant.ADJUST_MORE);
            }
        }));

        minusButton.setOnTouchListener(new Click(new Click.CustomClick() {
            @Override
            public void oneClick() {
                threshAdjust(-Constant.ADJUST_FEW);
            }

            @Override
            public void doubleClick() {
                threshAdjust(-Constant.ADJUST_MORE);
            }
        }));

        saveButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileStorageHelper.saveBitmap(laterBitmap, threshInt+".png","test");
            }
        });

    }

    /**
     * 调节参数值
     * @param value 调节值
     */
    private void threshAdjust(int value){
        threshInt += value;
        if (threshInt < Constant.ADJUST_MIN) threshInt = Constant.ADJUST_MIN;
        if (threshInt > Constant.ADJUST_MAX) threshInt = Constant.ADJUST_MAX;
        ImageProcessing.setThresh(threshInt);
    }

}

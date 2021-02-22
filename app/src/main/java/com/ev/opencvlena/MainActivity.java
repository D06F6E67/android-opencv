package com.ev.opencvlena;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ev.opencvlena.util.Click;
import com.ev.opencvlena.util.Constant;
import com.ev.opencvlena.util.Power;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;


public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static String TAG = "MainActivity";

    /**相机预览部件*/
    private CameraBridgeViewBase cameraView;
    /**控制调参控件显示*/
    private CheckBox allControlCheckBox;
    /**阈值增加按钮*/
    private Button addButton;
    /**阈值显示*/
    private TextView threshText;
    /**阈值减少按钮*/
    private Button minusButton;
    /**显示处理后的识别区域*/
    private ImageView disposeImage;
    /**显示识别内容*/
    private TextView resultText;

    /**缓存相机每帧输入的数据*/
    private Mat rgbaMat;
    /**识别区域处理后的Bitmap*/
    private static Bitmap disposeBitmap;
    /**阈值*/
    private static int threshInt;
    /**识别内容*/
    private static String resultString;

    /**点击的开始位置*/
    private Point startPoint;
    /**离开屏幕的位置*/
    private Point endPoint;
    /**画面缩放比例*/
    private float scale;
    /**顶部状态栏高度*/
    private int topBarHeight;
    /**左侧空白宽度*/
    private float leftMarginWidth;

    /**数据持久化*/
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    static {
        OpenCVLoader.initDebug();
    }

    Handler handler = new Handler();
    /**图像识别线程*/
    Runnable imageDiscern = new Runnable() {
        @Override
        public void run() {
            // 处理识别区域
            disposeBitmap = ImageProcessing.disposeMat(rgbaMat);
            // 数字识别
            if (TessTwoHelper.tessTwoFileExist() && disposeBitmap != null)
                resultString = ImageProcessing.numberDisccern(disposeBitmap);
            disposeImage.setImageBitmap(disposeBitmap);
            threshText.setText(Integer.valueOf(threshInt).toString());
            resultText.setText(resultString);
            handler.postDelayed(this, 500);// 每0.5秒启动
        }
    };
    /**数据保存线程*/
    Runnable saveData = new Runnable() {
        @Override
        public void run() {
            editor.putInt("threshInt", threshInt);
            Rect range = ImageProcessing.getRange();
            editor.putInt("rangeX", range.x);
            editor.putInt("rangeY", range.y);
            editor.putInt("rangeW", range.width);
            editor.putInt("rangeH", range.height);
            editor.apply();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // 去除页面的标题
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initEvent();

        Power.getPower(this, Constant.POWER_NAME, cameraView);// 获取权限

        handler.postDelayed(imageDiscern, 1000);// 启动线程
    }

    /**
     * 初始化控件
     */
    private void initView() {
        cameraView = findViewById(R.id.camera_view);
        allControlCheckBox = findViewById(R.id.allControlCheckBox);
        addButton = findViewById(R.id.addButton);
        threshText = findViewById(R.id.threshText);
        minusButton = findViewById(R.id.minusButton);
        disposeImage = findViewById(R.id.binaryImage);
        resultText = findViewById(R.id.resultText);
    }

    private void initData(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();// 初始化SP对象

        threshInt = sharedPreferences.getInt("threshInt", Constant.THRESH);
        ImageProcessing.setThresh(threshInt);// 阈值
        Rect rect = new Rect(sharedPreferences.getInt("rangeX", Constant.RANGE.x),
                sharedPreferences.getInt("rangeY",Constant.RANGE.y),
                sharedPreferences.getInt("rangeW",Constant.RANGE.width),
                sharedPreferences.getInt("rangeH",Constant.RANGE.height));
        ImageProcessing.setRange(rect); // 识别区域
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        cameraView.setCvCameraViewListener(this);
        allControlCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 设置调参控件状态
                if (allControlCheckBox.isChecked()) showView(View.VISIBLE);
                else {
                    showView(View.GONE);
                    handler.post(saveData);// 保存数据
                }
            }
        });

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

    }

    /**
     * 调节参数值
     *
     * @param value 调节值
     */
    private void threshAdjust(int value) {
        threshInt += value;
        if (threshInt < Constant.ADJUST_MIN) threshInt = Constant.ADJUST_MIN;
        if (threshInt > Constant.ADJUST_MAX) threshInt = Constant.ADJUST_MAX;
        ImageProcessing.setThresh(threshInt); //设置算法阈值
        threshText.setText(Integer.valueOf(threshInt).toString()); //设置显示阈值
    }

    /**
     * 设置调参控件状态
     *
     * @param state View.VISIBLE    -- 可见
     *              View.INVISIBLE  -- 不可见
     *              View.GONE       -- 隐藏
     */
    private void showView(int state) {
        if (state == View.VISIBLE || state == View.INVISIBLE || state == View.GONE) {
            addButton.setVisibility(state);
            threshText.setVisibility(state);
            minusButton.setVisibility(state);
            disposeImage.setVisibility(state);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.POWER:// 权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.enableView();// 使能视图 否则无画面
                    TessTwoHelper.initTessTwoFile(this);// 初始化TessTwo数据文件
                } else {
                    Toast.makeText(this, "权限已拒绝,程序无法正常使用,请前往设置开启！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    /**
     * 获取点击的位置
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getDeviation();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:// 点击的开始位置
                startPoint = new Point((ev.getX() - leftMarginWidth) / scale, (ev.getY() - topBarHeight) / scale);
                break;
            case MotionEvent.ACTION_MOVE:// 触屏实时位置
                break;
            case MotionEvent.ACTION_UP:// 离开屏幕的位置
                endPoint = new Point((ev.getX() - leftMarginWidth) / scale, (ev.getY() - topBarHeight) / scale);
                ImageProcessing.setRange(new Rect(startPoint, endPoint));
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取点击位置偏差值(因为画面存在一定比例的缩放)
     */
    private void getDeviation() {
        scale = cameraView.getmScale();// 缩放比例
        Resources resources = getResources();
        // 顶部状态栏高度
        topBarHeight = resources.getDimensionPixelSize(resources.getIdentifier
                ("status_bar_height", "dimen", "android"));
        // 底部导航栏高度
        int bottomHeight = resources.getDimensionPixelSize(resources.getIdentifier
                ("navigation_bar_height", "dimen", "android"));

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        float aspectRatio = Float.valueOf(dm.widthPixels) / dm.heightPixels; // 屏幕宽高比

        leftMarginWidth = (aspectRatio * (topBarHeight + bottomHeight))/2; // 左侧空白宽度
    }

    /**
     * 对象实例化及基本属性的设置，包括长度、宽度和图像类型标志
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        rgbaMat = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    protected void onDestroy() {
        if (cameraView != null) {
            cameraView.disableView();
        }
        super.onDestroy();
    }

    /**
     * 获取相机的图像存入rgbaMat中
     *
     * @param inputFrame 相机输入
     * @return rgbaMat
     */
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        rgbaMat = inputFrame.rgba();  //一定要有！！！不然数据保存不进MAT中！！！
        // 处理写在定时任务中
        // 检测范围
        ImageProcessing.showRange(rgbaMat);
        //直接返回输入视频预览图的RGB数据并存放在Mat数据中
        return rgbaMat;
    }

    /**
     * 结束时释放
     */
    @Override
    public void onCameraViewStopped() {
        rgbaMat.release();
        ImageProcessing.release();
    }

}

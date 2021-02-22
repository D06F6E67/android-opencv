package com.ev.opencvlena.util;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * 实现单击和双击
 *
 * @Author Administrator
 * @Date 2020/12/29 2020/12/29
 * @Version V1.0
 **/
public class Click implements View.OnTouchListener {

    private static int timeout=400;//双击间四百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private Handler handler;
    private CustomClick customClick;

    public interface CustomClick{
        void oneClick();//点击一次的回调
        void doubleClick();//连续点击两次的回调

    }

    public Click(CustomClick customClick) {
        this.customClick = customClick;
        handler = new Handler();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            clickCount++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickCount == 1) {
                        customClick.oneClick();
                    }else if(clickCount==2){
                        customClick.doubleClick();
                    }
                    handler.removeCallbacksAndMessages(null);
                    //清空handler延时，并防内存泄漏
                    clickCount = 0;//计数清零
                }
            },timeout);//延时timeout后执行run方法中的代码
        }
        return false;//让点击事件继续传播，方便再给View添加其他事件监听
    }
}

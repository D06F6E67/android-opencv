package com.ev.opencvlena;

import android.content.Context;
import android.graphics.Bitmap;

import com.ev.opencvlena.util.Constant;
import com.ev.opencvlena.util.FileStorageHelper;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

/**
 * 该类封装了TessBaseApi的相关方法，实现文字识别
 *
 * @Author Administrator
 * @Date 2020/12/14 2020/12/14
 * @Version V1.0
 **/
public class TessTwoHelper {

    private TessBaseAPI tessBaseAPI = new TessBaseAPI();

    /**
     * 初始化TessTwo需要的训练文件
     * @param context
     */
    public static void initTessTwoFile(final Context context) {
        if (!tessTwoFileExist()) {
            File dir = new File(Constant.TESS_TWO_DATA_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            /*将TessTwo所需的数据文件复制到SD卡*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String assetsPath: Constant.TESS_TWO_DATA_NAME) {
                         FileStorageHelper.copyFilesFromAssets(context, assetsPath, Constant.TESS_TWO_DATA_PATH);
                    }
                }
            }).start();
        }
    }

    /**
     * 判断TessTwo需要的文件是否存在
     * @return 结果
     */
    public static boolean tessTwoFileExist(){
        for (String name: Constant.TESS_TWO_DATA_NAME) {
            File file = new File(Constant.TESS_TWO_DATA_PATH + File.separator + name);
            if (!file.exists()) return false;
        }
        return true;
    }

    public void init() {
        // 第一个参数为训练数据路径 eng为识别语言
        tessBaseAPI.init("/storage/emulated/0/", Constant.TESS_TWO_LANGUAGE);
        
        tessBaseAPI.setDebug(true);
        // 设置识别白名单
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789.");
        // 设置识别黑名单
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()_+=-[]}{;:'\"\\|~`,/<>?");
        // 设置识别模式
        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
    }

    public String getText(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        return tessBaseAPI.getUTF8Text();
    }

}

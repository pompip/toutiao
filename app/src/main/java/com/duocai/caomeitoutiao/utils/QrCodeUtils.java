package com.duocai.caomeitoutiao.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dinosa on 2018/4/12.
 * 二维码的生成工具；
 */

public class QrCodeUtils {

    /**
     * 生成一个二维码的操作；
     * @param content
     * @param width
     * @param height
     * @return
     */
    public static Bitmap generateBitmap(String content, int width, int height) {

        Bitmap qrCodeBitmap = getQrCodeBitmap(content);
        if(qrCodeBitmap!=null){
            return qrCodeBitmap;
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);

            //这里我们要做的一个操作就是缓存二维码；
            //将二维码存储起来；
            saveQrCodeBitmap(bitmap,content);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getQrCodeBitmap(String qrCodeUrl){

        //这里我们在生成二维码的时候，我们要做的一个操作就是将二维码换缓存起来；

        File storeQrCodeFile = getStoreQrCodeFile(qrCodeUrl);
        if (storeQrCodeFile.exists() && storeQrCodeFile.isFile()) {
            //这里返回对应的Bitmap对象；
            System.out.println("来自缓存的二维码操作；");
            return BitmapFactory.decodeFile(storeQrCodeFile.getAbsolutePath());
        }

        return null;
    }

    public static void saveQrCodeBitmap(Bitmap bitmap,String qrCodeUrl) throws FileNotFoundException {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,new FileOutputStream(getStoreQrCodeFile(qrCodeUrl)));
    }

    /**
     * 返回存储二维码的路径
     * @param qrCodeUrl
     * @return
     */
    public static File getStoreQrCodeFile(String qrCodeUrl){

        String storePath  = Environment.getExternalStorageDirectory().getAbsoluteFile()+"/quanmingnews/imgs";
        String fileName = FormatUtils.md5(qrCodeUrl);

        File storeQrCodeDir = new File(storePath);
        if(!storeQrCodeDir.exists() || storeQrCodeDir.isFile()){
            storeQrCodeDir.mkdirs();
        }

        File qrCodeFile = new File(storeQrCodeDir, fileName);
        return qrCodeFile;
    }
}

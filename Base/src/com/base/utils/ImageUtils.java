
package com.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class ImageUtils {
    private ImageUtils() {
    };

    public static BitmapDrawable readDrawable(Context context, int resId) {
        return readDrawable(context, resId, null);
    }

    public static BitmapDrawable readDrawable(Context context, int resId, Config bitmapConfig) {
        return readDrawable(context.getResources(), resId, bitmapConfig);
    }

    public static BitmapDrawable readDrawable(Resources res, int resId, Config bitmapConfig) {
        BitmapDrawable drawable = null;
        Bitmap bitmap = null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if (bitmapConfig != null) {
            opts.inPreferredConfig = bitmapConfig;
        }
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        try {
            InputStream ips = res.openRawResource(resId);
            if (ips != null) {
                bitmap = BitmapFactory.decodeStream(ips, null, opts);
            }
            if (bitmap != null) {
                drawable = new BitmapDrawable(res, bitmap);
            }
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    public static BitmapDrawable readDrawable(Resources res, File file) {
        BitmapDrawable drawable = null;
        Bitmap bitmap = null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        try {
            InputStream ips = new FileInputStream(file);
            if (ips != null) {
                bitmap = BitmapFactory.decodeStream(ips, null, opts);
            }
            if (bitmap != null) {
                drawable = new BitmapDrawable(res, bitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * 
     * @see imageButton_fav.setImageResource(R.drawable.guide_fav_1)
     *      </br>修改为</br>
     *      imageButton_fav.setImageBitmap(BitmapUtils.readBitMap(this,
     *      R.drawable.guide_fav_1));
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        try {
            InputStream is = context.getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.showcase.tabra.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import java.io.*;

public class PictureUtils {

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static File getImage(Bitmap newImage, File path) {
        //My bitmap
        //Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        //create a file to write bitmap data
        File f = new File(path, "product_photo.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        newImage.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxHeight, int maxWidth ) {

        if (maxHeight > 0 && maxWidth > 0) {

            int sourceWidth = image.getWidth();
            int sourceHeight = image.getHeight();

            int targetWidth = maxWidth;
            int targetHeight = maxHeight;

            float sourceRatio = (float) sourceWidth / (float) sourceHeight;
            float targetRatio = (float) maxWidth / (float) maxHeight;

            if (targetRatio > sourceRatio) {
                targetWidth = (int) ((float) maxHeight * sourceRatio);
            } else {
                targetHeight = (int) ((float) maxWidth / sourceRatio);
            }

            return Bitmap.createScaledBitmap(image, targetWidth, targetHeight, true);

        } else {
            return image;
        }
    }
}

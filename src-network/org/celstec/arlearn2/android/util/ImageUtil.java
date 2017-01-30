package org.celstec.arlearn2.android.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.widget.ImageView;
import org.celstec.arlearn2.android.R;
import org.celstec.dao.gen.GameFileLocalObject;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Stefaan Ternier
 * ****************************************************************************
 */
public class ImageUtil {

    public static boolean setBackgroundImage(Activity ctx, String imageId, long gameIdToUseForMainSplashScreen, int viewId) {

        Bitmap background = GameFileLocalObject.getBitmapFullScreen(ctx, gameIdToUseForMainSplashScreen, imageId);
        if (android.os.Build.VERSION.SDK_INT >= 11)
            if (background != null) {
                ((ImageView) ctx.findViewById(viewId)).setImageBitmap(background);

                return true;
            }
        return false;
    }

    public static boolean setBackgroundImage(Activity ctx, int drawableId, long gameIdToUseForMainSplashScreen, int viewId) {

        Bitmap background = getBitmapFullScreen(ctx, drawableId);
        if (android.os.Build.VERSION.SDK_INT >= 11)
            if (background != null) {
                ((ImageView) ctx.findViewById(viewId)).setImageBitmap(background);

                return true;
            }
        return false;
    }

    public static Bitmap getBitmapFullScreen(Activity ctx, int drawableId) {
        Display display = ctx.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int reqWidth = size.x;
        int reqHeight = size.y;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(ctx.getResources(), drawableId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(ctx.getResources(), drawableId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}

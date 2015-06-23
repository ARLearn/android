package org.celstec.arlearn2.android.store.map;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import org.celstec.arlearn2.android.R;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.osmdroid.views.overlay.OverlayItem;

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
public class GameOverlayItem extends OverlayItem {

    private StoreGameLocalObject game;
    private Context ctx;


    public GameOverlayItem(StoreGameLocalObject game, Context ctx) {
        super(game.getTitle(), "", new org.osmdroid.util.GeoPoint(game.getLat(), game.getLng()));
        this.game = game;
        this.ctx = ctx;

        setMarker(getDrawable());
    }

    @Override
    public Drawable getDrawable() {
        Bitmap originalBitmap = null;
        byte[] data = game.getIcon();
        if (data != null && data.length!=0) {
            originalBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        } else {
             originalBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_mygames);
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 75, 75, false);
        Drawable icon =new BitmapDrawable(ctx.getResources(),getRoundedCornerBitmap(resizedBitmap,7));
        icon.setBounds(0, 0, 100, 100);
        return icon;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

//    @Override
//    public int getWidth() {
//        return 15;
//    }
//
//    @Override
//    public int getHeight() {
//        return 15;
//    }

    public StoreGameLocalObject getGame() {
        return game;
    }

    //    @Override
//    public Drawable getMarker(int stateBitset) {
//
//        Drawable icon = ctx.getResources().getDrawable(R.drawable.ic_mygames); //TODO game icon
//        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
//        setMarker(icon);
//        return ctx.getResources().getDrawable(R.drawable.ic_mygames); //TODO game icon
//    }



//    @Override
//    public int getWidth() {
//        return 30;
//    }
//
//    @Override
//    public int getHeight(){
//        return 30;
//    }
}

package org.celstec.arlearn2.android.store.map;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.dao.gen.StoreGameLocalObject;
import org.osmdroid.util.GeoPoint;
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
        Bitmap originalBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_mygames);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, false);
        Drawable icon =new BitmapDrawable(ctx.getResources(),resizedBitmap);
        icon.setBounds(0, 0, 100, 100);
        return icon;
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

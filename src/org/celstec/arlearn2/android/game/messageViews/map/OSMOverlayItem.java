package org.celstec.arlearn2.android.game.messageViews.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.HashMap;

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
public class OSMOverlayItem extends OverlayItem {


    private GeneralItem gi;
    private GeneralItemLocalObject generalItemLocalObject;
    private long runId;
    private Context ctx;
    private static HashMap<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    private static int currentTheme = 0;
    private Drawable drawable;
    private Drawable readDrawable;

    public OSMOverlayItem(GeneralItem gi, long runId, Context ctx) {
        super(gi.getName(), "", new org.osmdroid.util.GeoPoint(gi.getLat(), gi.getLng()));
        this.gi = gi;
        this.ctx = ctx;
        this.runId = runId;
        generalItemLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(gi.getId());
    }


    @Override
    public Drawable getMarker(int stateBitset) {
        if (generalItemLocalObject.isRead(runId)) {
            if (readDrawable != null) return readDrawable;
        } else {

            if (drawable != null) return drawable;

        }

        if (DrawableUtil.styleUtil.getTheme() != currentTheme) {
            currentTheme = DrawableUtil.styleUtil.getTheme();
            drawableHashMap = new HashMap<String, Drawable>();
        }
        GameFileLocalObject gameFileLocalObjectIcon = GameFileLocalObject.getGameFileLocalObject(gi.getGameId(), "/generalItems/" + gi.getId() + "/icon");
        if (gameFileLocalObjectIcon == null && drawableHashMap.containsKey(gi.getType()))
            return drawableHashMap.get(gi.getType());

        Bitmap bitmapNoIcon = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.game_general_item_marker_noicon);
        Bitmap icon = GameFileLocalObject.getBitmap(ARL.ctx, gameFileLocalObjectIcon);
        if (icon == null) {
            icon = BitmapFactory.decodeResource(ctx.getResources(), GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.mapBeanToConstant(gi)));
        }

        int widthNoIcon = bitmapNoIcon.getWidth();
        int heightNoIcon = bitmapNoIcon.getHeight();
        int widthIcon = icon.getWidth();
        int heightIcon = icon.getHeight();
        int newWidthNoIcon = DrawableUtil.dipToPixels(100) / 2;
        int newHeightNoIcon = DrawableUtil.dipToPixels(125) / 2;
        int padding = DrawableUtil.dipToPixels(20) / 2;
        int newWidthIcon = newWidthNoIcon - padding - padding;

        float scaleWidthNoIcon = ((float) newWidthNoIcon) / widthNoIcon;
        float scaleHeightNoIcon = ((float) newHeightNoIcon) / heightNoIcon;

        float scaleWidthIcon = ((float) newWidthIcon) / widthIcon;
        float scaleHeightIcon = ((float) newWidthIcon) / heightIcon;


        Matrix matrixNoIcon = new Matrix();
        matrixNoIcon.postScale(scaleWidthNoIcon, scaleHeightNoIcon);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapNoIcon, 0, 0, widthNoIcon, heightNoIcon, matrixNoIcon, true);
        BitmapDrawable bmdNoIcon = new BitmapDrawable(resizedBitmap);

        Matrix matrixIcon = new Matrix();
        matrixIcon.postScale(scaleWidthIcon, scaleHeightIcon);
        Bitmap resizeIcon = Bitmap.createBitmap(icon, 0, 0, widthIcon, heightIcon, matrixIcon, true);
        BitmapDrawable iconDrawable = new BitmapDrawable(resizeIcon);

//        bmd.getPaint().setColor(DrawableUtil.styleUtil.getPrimaryColor());
        ColorMatrixColorFilter filter = null;
        if (generalItemLocalObject.isRead(runId)) {
            filter = DrawableUtil.getBlackWhiteFilter(DrawableUtil.styleUtil.getTextInactive());
        } else {
            filter = DrawableUtil.getBlackWhiteFilter(DrawableUtil.styleUtil.getPrimaryColor());
        }
        bmdNoIcon.setColorFilter(filter);

        Drawable[] layers = new Drawable[2];

        layers[0] = bmdNoIcon;
        layers[1] = iconDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, padding, padding, padding, newHeightNoIcon - newWidthNoIcon + padding);
        drawableHashMap.put(gi.getType(), layerDrawable);
        if (generalItemLocalObject.isRead(runId)) {
            readDrawable = layerDrawable;
        } else {
            drawable = layerDrawable;
        }

        return layerDrawable;
    }

    @Override
    public int getHeight() {
        BitmapDrawable bd = (BitmapDrawable) getMarker(0);
        if (bd != null) {
            return ((BitmapDrawable) getMarker(0)).getBitmap().getHeight();
        }
        return 0;
    }

    public long getGeneralItemId() {
        return gi.getId();
    }
}


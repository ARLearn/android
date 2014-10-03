package org.celstec.arlearn2.android.game.messageViews.map;

import android.content.Context;
import org.celstec.arlearn2.android.game.messageViews.GameMap;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

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
public class OsmGeneralItemizedIconOverlay extends ItemizedIconOverlay<OverlayItem> {


    Context ctx;

    public OsmGeneralItemizedIconOverlay(Context pContext, List<OverlayItem> list, OnItemGestureListener<OverlayItem> pOnItemGestureListener) {
        super(pContext, list, pOnItemGestureListener);
        this.ctx = pContext;
    }


    public void setGeneralItemList(GeneralItem[] gis) {
        removeAllItems();
        for (int i = 0; i < gis.length; i++) {
            addItem(new OSMOverlayItem(gis[i], ctx));

        }
    }

}

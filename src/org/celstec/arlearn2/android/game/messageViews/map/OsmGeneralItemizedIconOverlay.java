package org.celstec.arlearn2.android.game.messageViews.map;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.MotionEvent;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.game.messageViews.GameMap;
import org.celstec.arlearn2.android.listadapter.AbstractGeneralItemsVisibilityAdapter;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
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

    private QueryBuilder qb;
    protected LazyList<GeneralItemVisibilityLocalObject> lazyList;
    protected long runId;

    GameMap ctx;

    public OsmGeneralItemizedIconOverlay(GameMap pContext, long runId, long gameId) {
        super(pContext, new ArrayList<OverlayItem>(), null);
        this.ctx = pContext;
        this.runId = runId;
        qb = AbstractGeneralItemsVisibilityAdapter.getQueryBuilder(runId, gameId);
        lazyList = qb.listLazy();
        setGeneralItemList();
        ARL.eventBus.register(this);
    }

    public void onEventMainThread(GeneralItemEvent event) {
        if (lazyList != null) lazyList.close();
        lazyList = qb.listLazy();
        setGeneralItemList();
        notifyAll();
    }

    public void close() {
        if (lazyList != null)lazyList.close();
        ARL.eventBus.unregister(this);
    }

    private void setGeneralItemList() {
        removeAllItems();
        for (GeneralItemVisibilityLocalObject object: lazyList) {
            GeneralItem generalItem = object.getGeneralItemLocalObject().getGeneralItemBean();
            if (generalItem.getLat()!= null) {
                OverlayItem overlayItem = new OSMOverlayItem(generalItem, runId, ctx);
                addItem(overlayItem);
            }
        }
    }

    @Override
    protected boolean onTap(int index) {
        ctx.openGeneralItem(getItem(index));
        return true;
    }

}

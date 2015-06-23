package org.celstec.arlearn2.android.game.generalItem.itemTypes.sortQuestion;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.itemTypes.SortQuestionFeatures;
import org.celstec.arlearn2.beans.generalItem.SortQuestion;

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
 * ************************************ ****************************************
 */
public class DragListener implements View.OnDragListener {
//    Drawable enterShape; //= getResources().getDrawable(R.drawable.shap_droptarget);
//    Drawable normalShape;// = getResources().getDrawable(R.drawable.shape);
    private SortQuestionFeatures sortQuestionFeatures;

    public DragListener(SortQuestionFeatures context) {
        this.sortQuestionFeatures = context;
//        enterShape = context.getResources().getDrawable(R.drawable.shap_droptarget);
//        normalShape = context.getResources().getDrawable(R.drawable.shape);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:

                return true;
            case DragEvent.ACTION_DRAG_ENTERED:
                return false;
            case DragEvent.ACTION_DRAG_EXITED:
            return true;
            case DragEvent.ACTION_DROP:
                LinearLayout targetContainer = (LinearLayout) v;
                sortQuestionFeatures.viewDropped((View) event.getLocalState(), targetContainer);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                sortQuestionFeatures.resetView((View) event.getLocalState());
                return true;
//                break;
//                v.setBackgroundDrawable(normalShape);
            default:
                System.out.println(event.getAction());
                break;
        }
        return true;
    }
}

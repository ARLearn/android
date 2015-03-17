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
 * ****************************************************************************
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
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing


                break;
            case DragEvent.ACTION_DRAG_ENTERED:
//                v.setBackgroundDrawable(enterShape);
                //((View) event.getLocalState()).setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorDrawable());
                //v.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorDrawable());

                break;
            case DragEvent.ACTION_DRAG_EXITED:
//                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup

//                View view = (View) event.getLocalState();
//                ViewGroup owner = (ViewGroup) view.getParent();
//                owner.removeView(view);

                LinearLayout targetContainer = (LinearLayout) v;
//                RelativeLayout existingView =  (RelativeLayout) targetContainer.getChildAt(0);
//                targetContainer.removeView(existingView);
//
//                targetContainer.addView(view);
//                view.setVisibility(View.VISIBLE);
//                view.setBackgroundDrawable(null);
//
//                owner.addView(existingView);
                sortQuestionFeatures.viewDropped((View) event.getLocalState(), targetContainer);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
//                v.setBackgroundDrawable(normalShape);
            default:
                break;
        }
        return true;
    }
}

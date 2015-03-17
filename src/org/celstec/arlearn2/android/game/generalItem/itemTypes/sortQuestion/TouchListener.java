package org.celstec.arlearn2.android.game.generalItem.itemTypes.sortQuestion;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import org.celstec.arlearn2.android.delegators.ARL;

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
public class TouchListener implements View.OnTouchListener {

//    Drawable enterShape; //= getResources().getDrawable(R.drawable.shap_droptarget);
//    Drawable normalShape;// = getResources().getDrawable(R.drawable.shape);

    public TouchListener(Activity context) {
//        enterShape = context.getResources().getDrawable(R.drawable.shap_droptarget);
//        normalShape = context.getResources().getDrawable(R.drawable.shape);
    }
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            view.setBackgroundDrawable(normalShape);
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorDrawable());
            ((View)view.getParent()).setBackgroundDrawable(null);
            view.startDrag(data, shadowBuilder, view, 0);

            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}

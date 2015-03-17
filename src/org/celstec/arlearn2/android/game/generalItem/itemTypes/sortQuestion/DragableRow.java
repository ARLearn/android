package org.celstec.arlearn2.android.game.generalItem.itemTypes.sortQuestion;


import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
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
public class DragableRow {

    private String text;
    private View row;
    private View dragView;
    private View dropView;

    public DragableRow(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void draw(SortQuestionFeatures sortQuestion, Activity context, LinearLayout linearLayout, boolean first) {
        row = context.getLayoutInflater().inflate(R.layout.game_general_item_sort_question_row, null);
        TextView choiceOption =((TextView)row.findViewById(R.id.text));
        choiceOption.setText(text);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        lp.topMargin = 5;
        if (first) {
            linearLayout.addView(row);
        } else {
            linearLayout.addView(row, lp);
        }

        dragView = row.findViewById(R.id.drag);
        dragView.setOnTouchListener(new TouchListener(context));
        dropView = row.findViewById(R.id.drop);
        dropView.setOnDragListener(new DragListener(sortQuestion));
        dropView.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
    }

    public View getDragView() {
        return dragView;
    }

    public View getDropView() {
        return dropView;
    }
}

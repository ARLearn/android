package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceTest;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.util.Vector;

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
public class MultipleChoiceFeatures extends SingleChoiceFeatures {

    private Vector<String> selectedRows = new Vector<String>();

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.MULTI_CHOICE);
    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    public MultipleChoiceFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
    }

    protected void setRichText(WebView webView){
        MultipleChoiceTest singleChoiceTestBean = (MultipleChoiceTest) generalItemBean;
        setRichText(webView, singleChoiceTestBean.getRichText(), singleChoiceTestBean.getAnswers());
    }

    protected SingleChoiceRow createRow(MultipleChoiceAnswerItem answerItem, LinearLayout linearLayout, boolean first){
        return new MultipleChoiceRow(answerItem, linearLayout, first);
    }

    private void clickHandler(SingleChoiceRow rowClicked){
        if (selectedRows.contains(rowClicked.getId())){
            choiceMap.get(rowClicked.getId()).unSelect();
            selectedRows.remove(rowClicked.getId());
        } else {
            choiceMap.get(rowClicked.getId()).select();
            selectedRows.add(rowClicked.getId());
        }
        if (selectedRows.isEmpty()) {
            activity.findViewById(R.id.button).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        }
    //TODO
    }

    protected class MultipleChoiceRow extends SingleChoiceRow {

        public MultipleChoiceRow(MultipleChoiceAnswerItem answerItem, LinearLayout linearLayout, boolean first){
            super(answerItem, linearLayout, first);
        }

        public void select(){
            icon.setImageResource(R.drawable.ic_ms_checked);
//            icon.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorOval());
            icon.setVisibility(View.VISIBLE);
            row.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorDrawable());
        }

        public void unSelect(){
            icon.setImageResource(R.drawable.ic_ms_unchecked);
//            icon.setBackgroundDrawable(ARL.drawableUtil.getGameMessageIconBackgroundRead());
            row.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
        }

        @Override
        public void onClick(View v) {
            clickHandler(MultipleChoiceRow.this);

            ((GradientDrawable) activity.findViewById(R.id.button).getBackground()).setColor(DrawableUtil.styleUtil.getButtonAlternativeColor());

        }
    }

    protected void submitButtonClick() {
        if (!selectedRows.isEmpty()) {
            for (String rowId : selectedRows) {
                MultipleChoiceAnswerItem selected =  multipleChoiceAnswerItemHashMap.get(rowId);
                ResponseDelegator.getInstance().createMultipleChoiceResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(), selected);

            }
            boolean correct = true;
            for (MultipleChoiceAnswerItem answer: ((MultipleChoiceTest) generalItemBean).getAnswers()){
//                MultipleChoiceAnswerItem answer =  multipleChoiceAnswerItemHashMap.get(rowId);
//            for (MultipleChoiceAnswerItem answer : mct.getAnswers()){
                if (answer.getIsCorrect()) {
                    if (!selectedRows.contains(answer.getId())) correct = false;
                } else {
                    if (selectedRows.contains(answer.getId())) correct = false;
                }
            }
            createAnswerResultAction(correct);
            activity.finish();

        }

    }
}

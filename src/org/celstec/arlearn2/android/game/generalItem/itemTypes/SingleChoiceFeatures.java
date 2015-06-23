package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.FeedbackView;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.SingleChoiceTest;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.util.HashMap;
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
public class SingleChoiceFeatures extends GeneralItemActivityFeatures{
    protected HashMap<String, SingleChoiceRow> choiceMap = new HashMap<String, SingleChoiceRow>();
    protected HashMap<String, MultipleChoiceAnswerItem> multipleChoiceAnswerItemHashMap = new HashMap<String, MultipleChoiceAnswerItem>();
    private String selectedRowId;


    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.SINGLE_CHOICE);
    }
    @Override
    protected boolean showDataCollection() {
        return false;
    }

    public SingleChoiceFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);


    }

    public void setMetadata(){
        super.setMetadata();
        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        setRichText(webView);



    }

    protected void setRichText(WebView webView){
        SingleChoiceTest singleChoiceTestBean = (SingleChoiceTest) generalItemBean;
        setRichText(webView, singleChoiceTestBean.getRichText(), singleChoiceTestBean.getAnswers());
    }

    protected void setRichText(WebView webView, String richText, List<MultipleChoiceAnswerItem> answers){
        webView.loadDataWithBaseURL("file:///android_res/raw/", richText, "text/html", "UTF-8", null);
        LinearLayout linearLayout = (LinearLayout) this.activity.findViewById(R.id.multipleChoice);
        linearLayout.setVisibility(View.VISIBLE);
        boolean first = true;
        for (MultipleChoiceAnswerItem answerItem: answers){
            if (!choiceMap.containsKey(answerItem.getId())) {
                SingleChoiceRow singleChoiceRow = createRow(answerItem, linearLayout, first);
                first = false;
                singleChoiceRow.unSelect();
                choiceMap.put(answerItem.getId(), singleChoiceRow);
                multipleChoiceAnswerItemHashMap.put(answerItem.getId(), answerItem);
            }
        }
        ((TextView) activity.findViewById(R.id.button)).setText(activity.getString(R.string.submit));
        ((TextView) activity.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonClick();
            }
        });
    }

    protected SingleChoiceRow createRow(MultipleChoiceAnswerItem answerItem, LinearLayout linearLayout, boolean first){
        return new SingleChoiceRow(answerItem, linearLayout, first);
    }

    private void clickHandler(SingleChoiceRow rowClicked){
        activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        if (selectedRowId != null) {
            choiceMap.get(selectedRowId).unSelect();
        }
        selectedRowId = rowClicked.getId();
        choiceMap.get(selectedRowId).select();
    }

    protected class SingleChoiceRow implements View.OnClickListener{

        protected View row;
        protected ImageView icon;
        private TextView choiceOption;
        private String id;

        public SingleChoiceRow(MultipleChoiceAnswerItem answerItem, LinearLayout linearLayout, boolean first){
            row = activity.getLayoutInflater().inflate(R.layout.game_general_item_multiplechoice_row, null);
            id = answerItem.getId();
            choiceOption =((TextView)row.findViewById(R.id.choiceOption));
            choiceOption.setText(answerItem.getAnswer());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            lp.topMargin = 5;
            if (first) {
                linearLayout.addView(row);
            } else {
                linearLayout.addView(row, lp);
            }
            icon = (ImageView) row.findViewById(R.id.selectIcon);
            row.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickHandler(SingleChoiceRow.this);

            ((GradientDrawable) activity.findViewById(R.id.button).getBackground()).setColor(DrawableUtil.styleUtil.getButtonAlternativeColor());

        }

        public String getId() {
            return id;
        }

        public void select(){
            icon.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorOval());
            icon.setVisibility(View.VISIBLE);
            row.setBackgroundDrawable(ARL.drawableUtil.getButtonAlternativeColorDrawable());
        }

        public void unSelect(){
            icon.setBackgroundDrawable(ARL.drawableUtil.getGameMessageIconBackgroundRead());
            icon.setVisibility(View.GONE);
            row.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
        }

    }

    protected void createAnswerGivenAction(){
        Action action = new Action();
        action.setAction("answer_given");
        action.setRunId(activity.getGameActivityFeatures().getRunId());
        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
        action.setGeneralItemId(generalItemLocalObject.getId());
        ActionsDelegator.getInstance().createAction(action);
    }

    protected void createAnswerIdAction(String id){
        Action action = new Action();
        action.setAction("answer_" + id);
        action.setRunId(activity.getGameActivityFeatures().getRunId());
        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
        action.setGeneralItemId(generalItemLocalObject.getId());
        ActionsDelegator.getInstance().createAction(action);
    }

    protected void createAnswerResultAction(boolean correct){
        Action action = new Action();
        action.setAction("answer_" + (correct ? "correct" : "wrong"));
        action.setRunId(activity.getGameActivityFeatures().getRunId());
        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
        action.setGeneralItemId(generalItemLocalObject.getId());
        ActionsDelegator.getInstance().createAction(action);
    }

    protected void submitButtonClick() {
        if (selectedRowId != null) {
            MultipleChoiceAnswerItem selected = multipleChoiceAnswerItemHashMap.get(selectedRowId);
            ResponseDelegator.getInstance().createMultipleChoiceResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(), selected);
            createAnswerGivenAction();
            createAnswerIdAction(selectedRowId);
            createAnswerResultAction(selected.getIsCorrect());
//            activity.finish();
            if (((SingleChoiceTest) generalItemBean).getShowFeedback()!= null && ((SingleChoiceTest) generalItemBean).getShowFeedback()){
                Intent feedback = new Intent(activity, FeedbackView.class);
                feedback.putExtra("correct", selected.getIsCorrect());
                feedback.putExtra("feedback", selected.getFeedback());
                activity.startActivity(feedback);
                if (selected.getIsCorrect()) {
                    activity.finish();
                }
            } else {
                activity.finish();
            }

        }
    }
}

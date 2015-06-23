package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.FeedbackView;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.game.generalItem.itemTypes.sortQuestion.DragableRow;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.SingleChoiceTest;
import org.celstec.arlearn2.beans.generalItem.SortQuestion;
import org.celstec.arlearn2.beans.generalItem.SortQuestionItem;
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
public class SortQuestionFeatures extends SingleChoiceFeatures { //GeneralItemActivityFeatures {

    protected HashMap<String, View> rowMap = new HashMap<String, View>();

    protected View dropViews[];
    protected SortQuestionItem sortQuestionItems[];

    public SortQuestionFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
    }

    public void setMetadata(){
        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        webView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {
                    resetView((View) event.getLocalState());
                }
                return true;
            }
        });
        setRichText(webView);

        Drawable iconDrawable = activity.getResources().getDrawable(getImageResource()).mutate();
        TypedArray ta =  activity.obtainStyledAttributes(activity.getGameActivityFeatures().getTheme(), new int[]{R.attr.primaryColor});
        ColorFilter filter = new LightingColorFilter( Color.BLACK, ta.getColor(0, Color.BLACK));
        iconDrawable.setColorFilter(filter);
        ((ImageView)this.activity.findViewById(R.id.generalItemIcon)).setImageDrawable(iconDrawable);
        activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        ((TextView) activity.findViewById(R.id.button)).setText(activity.getString(R.string.submit));
        ((TextView) activity.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonClick();
            }
        });
        ((GradientDrawable) activity.findViewById(R.id.button).getBackground()).setColor(DrawableUtil.styleUtil.getButtonAlternativeColor());

    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.SORT_QUESTION);

    }

    protected void setRichText(WebView webView){
        SortQuestion sortQuestion = (SortQuestion) generalItemBean;
        setRichTextSortQuestions(webView, sortQuestion.getRichText(), sortQuestion.getAnswers());
    }

    protected void setRichTextSortQuestions(WebView webView, String richText, List<SortQuestionItem> answers){
        webView.loadDataWithBaseURL("file:///android_res/raw/", richText, "text/html", "UTF-8", null);
        LinearLayout linearLayout = (LinearLayout) this.activity.findViewById(R.id.multipleChoice);
        linearLayout.setVisibility(View.VISIBLE);
        boolean first = true;
        int i = 0;
        dropViews = new View[answers.size()];
        sortQuestionItems = new SortQuestionItem[answers.size()];
        for (SortQuestionItem answerItem: answers){
            DragableRow row = new DragableRow(answerItem.getText());

            row.draw(this, activity, linearLayout, i == 0);
            row.getDragView();
            dropViews[i] = row.getDropView();
            sortQuestionItems[i] = answerItem;
            rowMap.put(answerItem.getId(), row.getDragView());

            i++;
        }
        ((TextView) activity.findViewById(R.id.button)).setText(activity.getString(R.string.submit));
        ((TextView) activity.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitButtonClick();
            }
        });
    }

    public void resetView(View dragView) {
        dragView.setVisibility(View.VISIBLE);
        dragView.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
        ((View)dragView.getParent()).setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
//        for (String key: rowMap.keySet()) {
//            if (rowMap.get(key) == dragView) {
//                for (int i =0; i< dropViews.length; i++) {
//                    View drop = dropViews[i];
//                    if (drop == dragView.getParent()) {
//
//                        dropViews[i].setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
//                    }
//                }
//            }
//        }
    }

    public void viewDropped(View dragView, View targetView) {
        int droppedAtIndex = 0;
        int droppedFromIndex = 0;
        for (int i =0; i< dropViews.length; i++) {
            View drop = dropViews[i];
            if (drop == targetView) {
                droppedAtIndex = i;
            }
        }
        String selectedKey = null;
        for (String key: rowMap.keySet()) {
            if (rowMap.get(key) == dragView) {
                selectedKey = key;
                for (int i =0; i< dropViews.length; i++) {
                    View drop = dropViews[i];
                    if (drop == dragView.getParent()) {
                        droppedFromIndex = i;
                    }
                }
            }
        }
        SortQuestionItem draggedQuestionItem = sortQuestionItems[droppedFromIndex];
        if (droppedFromIndex == droppedAtIndex) {
//            System.out.println("do nothing");
            dragView.setVisibility(View.VISIBLE);
            dropViews[droppedAtIndex].setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
            dragView.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
        } else {
            ViewGroup owner = (ViewGroup) dragView.getParent();
            owner.removeView(dragView);
            if (droppedFromIndex < droppedAtIndex) {

                //iterate 0 .. 2 at, move 1 to 0, 2 to 1, at the end (2) put the dragged view

                for  (int i = droppedFromIndex; i < droppedAtIndex; i++) {
                    RelativeLayout viewToMove = (RelativeLayout)((LinearLayout)dropViews[i+1]).getChildAt(0);
                    SortQuestionItem questionItemsToMove = sortQuestionItems [i+1];
                    ViewGroup ownerViewToMove = (ViewGroup) viewToMove.getParent();
                    ownerViewToMove.removeView(viewToMove);
                    ((LinearLayout) dropViews[i]).addView(viewToMove);
                    sortQuestionItems[i] =questionItemsToMove;
                }

            } else {
                //iterate 2 .. 0, move 1 to 2 , 0 to 1 and put dragged view in beginning (0)
                for  (int i = droppedFromIndex; i > droppedAtIndex; i--) {
                    RelativeLayout viewToMove = (RelativeLayout)((LinearLayout)dropViews[i-1]).getChildAt(0);
                    SortQuestionItem questionItemsToMove = sortQuestionItems [i-1];
                    ViewGroup ownerViewToMove = (ViewGroup) viewToMove.getParent();
                    ownerViewToMove.removeView(viewToMove);
                    ((LinearLayout) dropViews[i]).addView(viewToMove);
                    sortQuestionItems[i] =questionItemsToMove;
                }
            }
            ((LinearLayout) dropViews[droppedAtIndex]).addView(dragView);
            sortQuestionItems[droppedAtIndex] =draggedQuestionItem;
            dropViews[droppedFromIndex].setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
            dragView.setBackgroundDrawable(ARL.drawableUtil.getGameMessageEntry());
            dragView.setVisibility(View.VISIBLE);
        }
    }

    protected void submitButtonClick() {
        System.out.println("pushed");
        String order = "";
        boolean correct = true;
        String ids[] = new String[sortQuestionItems.length];
        for (int i=0; i<sortQuestionItems.length; i ++) {
            ids[i] = sortQuestionItems[i].getId();
            if (i!=0) {
                order += "_";
                if (sortQuestionItems[i-1].getCorrectPosition() > sortQuestionItems[i].getCorrectPosition()) {
                    correct = false;
                }
            }
            order +=sortQuestionItems[i].getId();
            System.out.println(sortQuestionItems[i].getCorrectPosition());

        }

//        System.out.println(order +" "+correct);
        createAnswerIdAction(order);
        ResponseDelegator.getInstance().createSortQuestionResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(), ids, correct);
        createAnswerResultAction(correct);
        createAnswerGivenAction();
        if (((SortQuestion)generalItemBean).getShowFeedback() != null && ((SortQuestion)generalItemBean).getShowFeedback()){
            Intent feedback = new Intent(activity, FeedbackView.class);
            feedback.putExtra("correct", correct);
            if (correct) {
                feedback.putExtra("feedback", ((SortQuestion)generalItemBean).getFeedbackCorrect());
            } else {
                feedback.putExtra("feedback", ((SortQuestion)generalItemBean).getFeedbackWrong());
            }

            activity.startActivity(feedback);
            if (correct) {
                activity.finish();
            }
        } else {
            activity.finish();
        }


    }

//    protected void createAnswerGivenAction(){
//        Action action = new Action();
//        action.setAction("answer_given");
//        action.setRunId(activity.getGameActivityFeatures().getRunId());
//        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
//        action.setGeneralItemId(generalItemLocalObject.getId());
//        ActionsDelegator.getInstance().createAction(action);
//    }
}

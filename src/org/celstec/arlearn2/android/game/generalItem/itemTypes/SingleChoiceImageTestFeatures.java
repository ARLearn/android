package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.arlearn2.beans.game.GameFile;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.SingleChoiceImageTest;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import android.view.View.OnClickListener;

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
public class SingleChoiceImageTestFeatures extends SingleChoiceFeatures {

    protected int COLUMNS = 3;
    private HashMap<String, GameFileLocalObject> gameFiles = new HashMap<String, GameFileLocalObject>();
    protected HashMap<MultipleChoiceAnswerItem, View> answerViewMapping = new HashMap<MultipleChoiceAnswerItem, View>();

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.SINGLE_CHOICE_IMAGE);
    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    public SingleChoiceImageTestFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
        for (GameFileLocalObject gamefile : generalItemLocalObject.getGeneralItemFiles()) {
            gameFiles.put(gamefile.getPath(), gamefile);
        }
        initUi();
    }

    private void initUi() {
        drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{0xDD000000, 0xAA000000});
        drawable.setCornerRadii(new float[]{5, 5, 5, 5, 5, 5, 5, 5});
        drawable.setStroke(1, 0xFF000000);
    }

    protected List<MultipleChoiceAnswerItem> getMultipleChoiceAnswers() {
        return getBean().getAnswers();
    }

    private SingleChoiceImageTest getBean() {
        return (SingleChoiceImageTest) generalItemBean;
    }

    public void setMetadata() {
        initTableView();
    }

    protected void setRichText(WebView webView){

    }

    protected void setCOLUMNS(){
        COLUMNS = getBean().getColumns();
    }

    private void initTableView() {
        super.setMetadata();
        setCOLUMNS();
        activity.findViewById(R.id.descriptionId).setVisibility(View.GONE);
        activity.findViewById(R.id.multipleChoice).setVisibility(View.GONE);
        submitVoteButton = (TextView) activity.findViewById(R.id.button);
//        activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        submitVoteButton.setText("Submit");


        ((GradientDrawable) submitVoteButton.getBackground()).setColor(DrawableUtil.styleUtil.getPrimaryColor());

        submitVoteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                submitButtonClick();
            }
        });
        TableLayout tableView = (TableLayout) activity.findViewById(R.id.multipleChoiceImageTable);
        tableView.setVisibility(View.VISIBLE);
        TableRow row = null;

        for (int i = 0; i < getMultipleChoiceAnswers().size(); i++) {
            final String answerId = getMultipleChoiceAnswers().get(i).getId();
            if ((i % COLUMNS) == 0) {
                System.out.println("new row");
                if (row != null) {
                    tableView.addView(row);
                }
                row = new TableRow(activity);
                TableLayout.LayoutParams rowLayout = new TableLayout.LayoutParams();
                rowLayout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                row.setLayoutParams(rowLayout);

            }


            View child = activity.getLayoutInflater().inflate(R.layout.game_general_item_mc_tile, null);
            row.addView(child);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
            layoutParams.weight = 1;
            child.setLayoutParams(layoutParams);
            Uri imageUri = gameFiles.get("/generalItems/" + generalItemLocalObject.getId() + "/" + answerId + ":i").getLocalUri();
            if (imageUri != null) {
                ((ImageView) child.findViewById(R.id.tileImage)).setImageURI(imageUri);
            }
            answerViewMapping.put(getMultipleChoiceAnswers().get(i), child);
            child.setOnClickListener(createImageViewClickerListener(answerId, child));
            child.findViewById(R.id.overlay).setVisibility(View.GONE);
            child.findViewById(R.id.overlay).setBackground(DrawableUtil.getPrimaryColorOval());
        }
        tableView.addView(row);
    }

    protected TextView submitVoteButton;
    protected View selectedView;
    protected GradientDrawable drawable;
    private MultipleChoiceAnswerItem selected = null;

    protected void submitButtonClick() {
        if (selected != null) {

            ResponseDelegator.getInstance().createMultipleChoiceResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(), selected);
            createAnswerGivenAction();
            createAnswerIdAction(selected.getId());
            createAnswerResultAction(selected.getIsCorrect());
            activity.finish();

        }
    }


    protected OnClickListener createImageViewClickerListener(final String answerId, final View im) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleSelectedView(im);
//                playSound(answerId + ":a");

//                submitVoteButton.setEnabled(true);
                submitVoteButton.setVisibility(View.VISIBLE);
                for (MultipleChoiceAnswerItem mcai : getMultipleChoiceAnswers()) {
                    if (mcai.getId().equals(answerId)) {
                        selected = mcai;
                    }
                }
            }
        };
    }

    protected void toggleSelectedView(View newSelection) {
        if (selectedView != null) {
            unsetSelection(selectedView);
        }
//            ImageView tileImage = (ImageView) selectedView.findViewById(R.id.tileImage);
//            tileImage.getDrawable().clearColorFilter();
//            selectedView.findViewById(R.id.overlay).setVisibility(View.GONE);

        setSelection(newSelection);
//        ImageView tileImage = (ImageView) im.findViewById(R.id.tileImage);
//        tileImage.getDrawable().setColorFilter(DrawableUtil.adjustAlpha(DrawableUtil.styleUtil.getPrimaryColor(), 0.4f), PorterDuff.Mode.MULTIPLY);
//        im.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
        selectedView = newSelection;
    }

    protected void unsetSelection(View selectedView) {
        ImageView tileImage = (ImageView) selectedView.findViewById(R.id.tileImage);
        tileImage.getDrawable().clearColorFilter();
        selectedView.findViewById(R.id.overlay).setVisibility(View.GONE);
    }

    protected void setSelection(View selectedView) {
        ImageView tileImage = (ImageView) selectedView.findViewById(R.id.tileImage);
        tileImage.getDrawable().setColorFilter(DrawableUtil.adjustAlpha(DrawableUtil.styleUtil.getPrimaryColor(), 0.4f), PorterDuff.Mode.MULTIPLY);
        selectedView.findViewById(R.id.overlay).setVisibility(View.VISIBLE);
    }
    @Override
    public void onPauseActivity() {
        super.onPauseActivity();
        if (selected != null){
            unsetSelection(answerViewMapping.get(selected));

        }
    }
}

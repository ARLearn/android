package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
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
    private HashMap<String, GameFileLocalObject> gameFiles= new HashMap<String, GameFileLocalObject>();

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
        for (GameFileLocalObject gamefile: generalItemLocalObject.getGeneralItemFiles()){
            gameFiles.put(gamefile.getPath(), gamefile);
        }
        initUi();
    }

    private void initUi(){
        drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{ 0xDD000000, 0xAA000000 });
        drawable.setCornerRadii(new float[]{ 5, 5, 5, 5, 5, 5, 5, 5 });
        drawable.setStroke(1, 0xFF000000);

//        submitVoteButton = (Button) findViewById(R.id.mct_submit);
//        submitVoteButton.setEnabled(false);
//        submitVoteButton.setOnClickListener(new OnClickListener() {
//
//            public void onClick(View v) {
//                submitButtonClick();
//            }
//        });
    }

    protected List<MultipleChoiceAnswerItem> getMultipleChoiceAnswers () {
        return getBean().getAnswers();
    }

    protected SingleChoiceImageTest getBean() {
        return (SingleChoiceImageTest) generalItemBean;
    }

    public void setMetadata(){
        initTableView();
    }

    private void initTableView() {
        activity.findViewById(R.id.descriptionId).setVisibility(View.GONE);
        submitVoteButton = (TextView)activity.findViewById(R.id.button);
//        activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
        submitVoteButton.setText("Submit");
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

            }
            final ImageView im = new ImageView(activity);
            im.setPadding(5, 5, 5, 5);
            Uri imageUri = gameFiles.get("/generalItems/"+generalItemLocalObject.getId()+"/"+answerId+":i").getLocalUri();
            if (imageUri != null) {
                im.setImageURI(imageUri);
            }
            row.addView(im);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams();
            layoutParams.width = 0;
            layoutParams.weight = 1;

            im.setLayoutParams(layoutParams);
//            answerViewMapping.put(getMultipleChoiceAnswers().get(i), im);
            im.setOnClickListener(createImageViewClickerListener(answerId, im));

        }
        tableView.addView(row);
    }
    protected TextView submitVoteButton;
    protected ImageView selectedView;
    protected GradientDrawable drawable;
    private MultipleChoiceAnswerItem selected = null;

    protected void submitButtonClick() {
        if (selected != null) {

            ResponseDelegator.getInstance().createMultipleChoiceResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(),selected);
            Action action = new Action();
            action.setAction("answer_given");
            action.setRunId(activity.getGameActivityFeatures().getRunId());
            action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
            action.setGeneralItemId(generalItemLocalObject.getId());

            ActionsDelegator.getInstance().createAction(action);
            action.setAction("answer_" + selected.getId());
            ActionsDelegator.getInstance().createAction(action);

            boolean correct = selected.getIsCorrect();
            action.setAction("answer_" + (correct ? "correct" : "wrong"));
            ActionsDelegator.getInstance().createAction(action);

//            ActionsDelegator.getInstance().issueAction(activity.getGameActivityFeatures().getRunId(), "answer_" + (correct ? "correct" : "wrong"));
//            ResponseDelegator.getInstance().syncResponses();
            activity.finish();

        }
    }

    protected OnClickListener createImageViewClickerListener(final String answerId, final ImageView im) {
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleSelectedView(im);
//                playSound(answerId + ":a");

//                submitVoteButton.setEnabled(true);
                submitVoteButton.setVisibility(View.VISIBLE);
                for (MultipleChoiceAnswerItem mcai :getMultipleChoiceAnswers()) {
                    if (mcai.getId().equals(answerId)) {
                        selected = mcai;
                    }
                }
            }
        };
    }

    protected void toggleSelectedView(ImageView im) {
        if (selectedView != null)
            selectedView.setBackgroundDrawable(null);
        im.setBackgroundDrawable(drawable);
        selectedView = im;
    }
}

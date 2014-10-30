package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceImageTest;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.dao.gen.GeneralItemLocalObject;

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
public class MultipleChoiceImageTestFeatures extends SingleChoiceImageTestFeatures {
    private ArrayList<MultipleChoiceAnswerItem> selected = new ArrayList<MultipleChoiceAnswerItem>();


    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.MULTI_CHOICE_IMAGE);
    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    public MultipleChoiceImageTestFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
    }

    protected List<MultipleChoiceAnswerItem> getMultipleChoiceAnswers() {
        return getBean().getAnswers();
    }

    private MultipleChoiceImageTest getBean() {
        return (MultipleChoiceImageTest) generalItemBean;
    }

    protected void submitButtonClick() {
        createAnswerGivenAction();
        for (MultipleChoiceAnswerItem sel : selected) {
            ResponseDelegator.getInstance().createMultipleChoiceResponse(generalItemLocalObject, activity.getGameActivityFeatures().getRunId(),sel);
            createAnswerIdAction(sel.getId());
        }
        activity.finish();
    }

    protected void setCOLUMNS(){
        COLUMNS = getBean().getColumns();
    }

    protected View.OnClickListener createImageViewClickerListener(final String answerId, final View im) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MultipleChoiceAnswerItem selectedItem = null;
                for (MultipleChoiceAnswerItem mcai : getMultipleChoiceAnswers()) {
                    if (mcai.getId().equals(answerId)) {
                        selectedItem = mcai;
                    }
                }
//                if (!selected.contains(selectedItem)){
//                    new PlayAudioTask().execute(answerId + ":a");
//
//                }
                if (!selected.contains(selectedItem)) {
                    Uri audioUri = gameFiles.get("/generalItems/" + generalItemLocalObject.getId() + "/" + answerId + ":a").getLocalUri();
                    playUri(audioUri);
                }
                toggleSelectedAnswer(selectedItem);
                if (selected.isEmpty()){
                    submitVoteButton.setVisibility(View.GONE);
                } else{
                    submitVoteButton.setVisibility(View.VISIBLE);

                }
            }
        };
    }

    private void toggleSelectedAnswer(MultipleChoiceAnswerItem selectedItem) {

        if (selected.contains(selectedItem)) {
            selected.remove(selectedItem);
            unsetSelection(answerViewMapping.get(selectedItem));
        } else {
            selected.add(selectedItem);
            setSelection(answerViewMapping.get(selectedItem));
        }

    }

    @Override
    public void onPauseActivity() {
        super.onPauseActivity();
        for (MultipleChoiceAnswerItem item: selected) {
            unsetSelection(answerViewMapping.get(item));
        }
    }
}

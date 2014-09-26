package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.SingleChoiceTest;
import org.celstec.dao.gen.GeneralItemLocalObject;

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
        SingleChoiceTest singleChoiceTestBean = (SingleChoiceTest) generalItemBean;
        webView.loadData(singleChoiceTestBean.getRichText(), "text/html", "utf-8");
        LinearLayout linearLayout = (LinearLayout) this.activity.findViewById(R.id.multipleChoice);
        linearLayout.setVisibility(View.VISIBLE);

        for (MultipleChoiceAnswerItem answerItem: singleChoiceTestBean.getAnswers()){
            final View row = activity.getLayoutInflater().inflate(R.layout.game_general_item_multiplechoice_row, null);
            ((TextView)row.findViewById(R.id.choiceOption)).setText(answerItem.getAnswer());
            linearLayout.addView(row);
        }
    }
}

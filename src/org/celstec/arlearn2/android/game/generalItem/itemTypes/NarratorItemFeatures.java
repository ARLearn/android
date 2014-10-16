package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.webkit.WebView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.OpenQuestion;
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
public class NarratorItemFeatures extends GeneralItemActivityFeatures{

    @Override
    protected boolean showDataCollection() {
        return true;
    }

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.NARRATOR_ITEM);
    }

    public NarratorItemFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        this(activity, generalItemLocalObject, true);
//        OpenQuestion openQuestion = ((NarratorItem) generalItemBean).getOpenQuestion();
//        if (openQuestion != null){
//            dataCollectionViewController.setVisibilities(openQuestion.isWithAudio(),openQuestion.isWithPicture(),openQuestion.isWithVideo(),openQuestion.isWithValue(), openQuestion.isWithText());
//        } else {
//            dataCollectionViewController.hideDataCollection();
//        }
    }

    public NarratorItemFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject, boolean dataCollection) {
        super(activity, generalItemLocalObject);
        if (dataCollection) {
            OpenQuestion openQuestion = ((NarratorItem) generalItemBean).getOpenQuestion();
            if (openQuestion != null){
                dataCollectionViewController.setVisibilities(openQuestion.isWithAudio(),openQuestion.isWithPicture(),openQuestion.isWithVideo(),openQuestion.isWithValue(), openQuestion.isWithText());
            } else {
                dataCollectionViewController.hideDataCollection();
            }
        } else {
            dataCollectionViewController.hideDataCollection();
        }
    }

    public void setMetadata(){
        super.setMetadata();
        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
//        webView.loadData(((NarratorItem) generalItemBean).getRichText(), "text/html", "utf-8");
        webView.loadDataWithBaseURL("file:///android_res/raw/", ((NarratorItem) generalItemBean).getRichText(), "text/html", "UTF-8", null);


    }
}

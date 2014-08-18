package org.celstec.arlearn2.android.game.generalItem;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.dataCollection.*;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.DataCollectionResultController;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.DataCollectionViewController;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.LazyListAdapter;
import org.celstec.arlearn2.android.game.generalItem.itemTypes.*;
import org.celstec.arlearn2.beans.generalItem.*;
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
public abstract class GeneralItemActivityFeatures {

    public GeneralItemLocalObject generalItemLocalObject;
    public DataCollectionViewController dataCollectionViewController;
    public DataCollectionResultController dataCollectionResultController;
    private LazyListAdapter lazyListAdapter;

    protected GeneralItemActivity activity;
    protected GeneralItem generalItemBean;

    private PictureManager pictureManager;
    private TextInputManager textInputManager;
    private ValueInputManager valueInputManager;
    private AudioInputManager audioInputManager;


    public static GeneralItemActivityFeatures getGeneralItemActivityFeatures(final GeneralItemActivity activity){
        Long generalItemId = activity.getIntent().getLongExtra(GeneralItemLocalObject.class.getName(), 0l);
        GeneralItemLocalObject generalItemLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId);
        GeneralItemActivityFeatures result = null;
        switch (GeneralItemMapper.mapBeanToConstant(generalItemLocalObject.getGeneralItemBean())){
            case GeneralItemMapper.NARRATOR_ITEM:
                result =new NarratorItemFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.SCAN_TAG:
                result = new ScanTagFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.SINGLE_CHOICE:
                result = new SingleChoiceFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.MULTI_CHOICE:
                result = new MultipleChoiceFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.SINGLE_CHOICE_IMAGE:
                result = new SingleChoiceImageTestFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.MULTI_CHOICE_IMAGE:
                result = new MultipleChoiceImageTestFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.AUDIO_OBJECT:
                result =new AudioItemFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.VIDEO_OBJECT:
                result =new VideoObjectFeatures(activity, generalItemLocalObject);
                break;
        }
        result.setMetadata();
        return result;
    }

    protected abstract boolean showDataCollection();

    protected abstract int  getImageResource();

    public GeneralItemActivityFeatures(final GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        this.activity = activity;
        this.generalItemLocalObject = generalItemLocalObject;
        this.generalItemBean = generalItemLocalObject.getGeneralItemBean();
        dataCollectionResultController = new DataCollectionResultController(this.activity);
        lazyListAdapter = new LazyListAdapter(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId(), generalItemLocalObject.getId());
        dataCollectionResultController.setAdapter(lazyListAdapter);
        dataCollectionResultController.notifyDataSetChanged();


        dataCollectionViewController = new DataCollectionViewController(activity){

            @Override
            public void onAudioClick() {
//                dataCollectionViewController.checkAudio();
//                dataCollectionViewController.checkPicture();
                if (audioInputManager == null) audioInputManager = new AudioInputManager(activity);
                audioInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                audioInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                audioInputManager.takeDataSample();
            }

            @Override
            public void onPictureClick() {
                if (pictureManager == null) pictureManager = new PictureManager(activity);
                pictureManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                pictureManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                pictureManager.takeDataSample();
            }

            @Override
            public void onVideoClick() {
                dataCollectionViewController.checkVideo();
            }

            @Override
            public void onTextClick() {
                if (textInputManager == null) textInputManager = new TextInputManager(activity);
                textInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                textInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                textInputManager.takeDataSample();
            }

            @Override
            public void onNumberClick() {
//                dataCollectionViewController.checkNumbers();
//                DataCollectionResult res = new DataCollectionResult(1, "test");
//                dataCollectionResultController.addResult(res);
                if (valueInputManager == null) valueInputManager = new ValueInputManager(activity);
                valueInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                valueInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                valueInputManager.takeDataSample();
            }

        };
        if (showDataCollection()) {
            dataCollectionViewController.showDataCollection();
        } else {
            dataCollectionViewController.hideDataCollection();
        }
        if (generalItemLocalObject == null) throw new NullPointerException("General Item object is null");

//
    }

    public void onResumeActivity(){
        dataCollectionViewController.showChecks(lazyListAdapter);
    }

    public void setMetadata(){


        ((ImageView)this.activity.findViewById(R.id.generalItemIcon)).setImageResource(getImageResource());
        TextView titleView = (TextView) this.activity.findViewById(R.id.titleId);
        titleView.setText(generalItemLocalObject.getTitle());

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DataCollectionManager.PICTURE_RESULT:
                pictureManager.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.AUDIO_RESULT:
                audioInputManager.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.VIDEO_RESULT:
                break;
            case DataCollectionManager.TEXT_RESULT:
                textInputManager.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.VALUE_RESULT:
                valueInputManager.onActivityResult(requestCode, resultCode, data);
                break;
        }

        ARL.responses.syncResponses(this.activity.getGameActivityFeatures().getRunId());
    }


    public void onPauseActivity(){}
}

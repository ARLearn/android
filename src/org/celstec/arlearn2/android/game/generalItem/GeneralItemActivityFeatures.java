package org.celstec.arlearn2.android.game.generalItem;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.dataCollection.*;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.DataCollectionResultController;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.DataCollectionViewController;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.LazyListAdapter;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.AudioCollectionActivityImpl;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.TextInputCollectionActivityImpl;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.ValueInputCollectionActivityImpl;
import org.celstec.arlearn2.android.game.generalItem.itemTypes.*;
import org.celstec.arlearn2.android.game.messageViews.*;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.*;
import org.celstec.arlearn2.beans.run.Action;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;

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
    private VideoManager videoInputManager;

    private boolean showNavigationBar = true;


    public static GeneralItemActivityFeatures getGeneralItemActivityFeatures(final GeneralItemActivity activity){

        Long generalItemId = activity.getIntent().getLongExtra(GeneralItemLocalObject.class.getName(), 0l);
        boolean showNavigationbar = activity.getIntent().getBooleanExtra("showNavigationbar", true);
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
            case GeneralItemMapper.SORT_QUESTION:
                result = new SortQuestionFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.AUDIO_OBJECT:
                result =new AudioItemFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.PURE_AUDIO:
                result =new PureAudioActivityFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.VIDEO_OBJECT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                result =new VideoObjectFeatures(activity, generalItemLocalObject);
                break;
            case GeneralItemMapper.END_MESSAGE:
                result = new EndMessageFeatures(activity, generalItemLocalObject);
                break;
        }
        result.setMetadata();
        result.setShowNavigationBar(showNavigationbar);
        return result;
    }

    public boolean isShowNavigationBar() {
        return showNavigationBar;
    }

    public void setShowNavigationBar(boolean showNavigationBar) {
        this.showNavigationBar = showNavigationBar;
    }

    public static int getContentView(GeneralItemActivity activity) {
        Long generalItemId = activity.getIntent().getLongExtra(GeneralItemLocalObject.class.getName(), 0l);
        GeneralItemLocalObject generalItemLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId);
        int result = R.layout.game_general_item;
        switch (GeneralItemMapper.mapBeanToConstant(generalItemLocalObject.getGeneralItemBean())){

            case GeneralItemMapper.PURE_AUDIO:
                result = R.layout.game_general_item_pure_audio;
                break;
            case GeneralItemMapper.SCAN_TAG:
                result = R.layout.game_general_item_scan;
                break;
            case GeneralItemMapper.END_MESSAGE:
                result = R.layout.game_general_item_end_message;
                break;
            default:
                result = R.layout.game_general_item;
        }
        return result;
    }

    protected abstract boolean showDataCollection();

    protected abstract int  getImageResource();

    public GeneralItemActivityFeatures(final GeneralItemActivity activity, final GeneralItemLocalObject generalItemLocalObject) {
        this.activity = activity;
        this.generalItemLocalObject = generalItemLocalObject;
        this.generalItemBean = generalItemLocalObject.getGeneralItemBean();
        if (activity.findViewById(R.id.audioLayout) == null) return;

        dataCollectionResultController = new DataCollectionResultController(this.activity);
        lazyListAdapter = new LazyListAdapter(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId(), generalItemLocalObject.getId());
        dataCollectionResultController.setAdapter(lazyListAdapter);
        dataCollectionResultController.notifyDataSetChanged();



        dataCollectionViewController = new DataCollectionViewController(activity){

            @Override
            public void onAudioClick() {

                audioInputManager = new AudioInputManager(activity);
                audioInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                audioInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                audioInputManager.setTheme(activity.getGameActivityFeatures().getTheme());
                String message = ((NarratorItem)generalItemLocalObject.getGeneralItemBean()).getOpenQuestion().getAudioDescription();
                audioInputManager.takeDataSample(AudioCollectionActivityImpl.class, message);
            }

            @Override
            public void onPictureClick() {
                pictureManager = new PictureManager(activity);
                pictureManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                pictureManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                pictureManager.setTheme(activity.getGameActivityFeatures().getTheme());
                pictureManager.takeDataSample(null);
            }

            @Override
            public void onVideoClick() {
                videoInputManager = new VideoManager(activity);
                videoInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                videoInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                videoInputManager.setTheme(activity.getGameActivityFeatures().getTheme());
                videoInputManager.takeDataSample(null);
            }

            @Override
            public void onTextClick() {
                textInputManager = new TextInputManager(activity);
                textInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                textInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                textInputManager.setTheme(activity.getGameActivityFeatures().getTheme());
                String message = ((NarratorItem)generalItemLocalObject.getGeneralItemBean()).getOpenQuestion().getTextDescription();
                textInputManager.takeDataSample(TextInputCollectionActivityImpl.class, message);
            }

            @Override
            public void onNumberClick() {
                valueInputManager = new ValueInputManager(activity);
                valueInputManager.setGeneralItem(GeneralItemActivityFeatures.this.generalItemLocalObject);
                valueInputManager.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
                valueInputManager.setTheme(activity.getGameActivityFeatures().getTheme());
                String message = ((NarratorItem)generalItemLocalObject.getGeneralItemBean()).getOpenQuestion().getValueDescription();
                valueInputManager.takeDataSample(ValueInputCollectionActivityImpl.class, message);
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
        if (dataCollectionViewController !=null) dataCollectionViewController.showChecks(lazyListAdapter);
        checkInvisibility();

    }



    public void setMetadata(){
//        if (DrawableUtil.isInit()) new DrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);
        DrawableUtil drawableUtil = ARL.getDrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);


        //((ImageView)this.activity.findViewById(R.id.generalItemIcon)).setImageResource(getImageResource());

        activity.findViewById(R.id.audioButtonIcon).setBackgroundDrawable(drawableUtil.getPrimaryColorOvalWithState());
        activity.findViewById(R.id.pictureButtonIcon).setBackgroundDrawable(drawableUtil.getPrimaryColorOvalWithState());
        activity.findViewById(R.id.videoButtonIcon).setBackgroundDrawable(drawableUtil.getPrimaryColorOvalWithState());
        activity.findViewById(R.id.textButtonIcon).setBackgroundDrawable(drawableUtil.getPrimaryColorOvalWithState());
        activity.findViewById(R.id.numberButtonIcon).setBackgroundDrawable(drawableUtil.getPrimaryColorOvalWithState());

        activity.findViewById(R.id.audioButtonCheckIcon).setBackgroundDrawable(drawableUtil.getButtonAlternativeColorOval());
        activity.findViewById(R.id.pictureButtonCheckIcon).setBackgroundDrawable(drawableUtil.getButtonAlternativeColorOval());
        activity.findViewById(R.id.videoButtonCheckIcon).setBackgroundDrawable(drawableUtil.getButtonAlternativeColorOval());
        activity.findViewById(R.id.textButtonCheckIcon).setBackgroundDrawable(drawableUtil.getButtonAlternativeColorOval());
        activity.findViewById(R.id.numberButtonCheckIcon).setBackgroundDrawable(drawableUtil.getButtonAlternativeColorOval());

        ((GradientDrawable)activity.findViewById(R.id.button).getBackground()).setColor(drawableUtil.styleUtil.getPrimaryColor());
        TextView titleView = (TextView) this.activity.findViewById(R.id.titleId);
        titleView.setText(generalItemLocalObject.getTitle());


        initiateIcon();

    }

    protected void initiateIcon(){
        TypedArray ta =  activity.obtainStyledAttributes(activity.getGameActivityFeatures().getTheme(), new int[]{R.attr.primaryColor});
        ColorFilter filter = new LightingColorFilter( Color.BLACK, ta.getColor(0, Color.BLACK));
        Bitmap icon = GameFileLocalObject.getBitmap(ARL.ctx, generalItemBean.getGameId(), "/generalItems/" + generalItemBean.getId() + "/icon");
        Drawable iconDrawable = activity.getResources().getDrawable(getImageResource()).mutate();
        if (icon != null) {
            iconDrawable = new BitmapDrawable(icon);
        }

        iconDrawable.setColorFilter(filter);
        ((ImageView)this.activity.findViewById(R.id.generalItemIcon)).setImageDrawable(iconDrawable);
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
                videoInputManager.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.TEXT_RESULT:
                textInputManager.onActivityResult(requestCode, resultCode, data);
                break;
            case DataCollectionManager.VALUE_RESULT:
                valueInputManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
        if (resultCode == activity.RESULT_OK) {
            Action action = new Action();
            action.setAction("answer_given");
            action.setRunId(GeneralItemActivityFeatures.this.activity.getGameActivityFeatures().getRunId());
            action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
            action.setGeneralItemId(generalItemLocalObject.getId());
            ARL.actions.createAction(action);
            ARL.responses.syncResponses(this.activity.getGameActivityFeatures().getRunId());
        }
    }


    public void onPauseActivity(){
        mHandler.removeCallbacks(periodicTimeCheck);
    }

    public void updateResponses() {
        if (dataCollectionResultController != null)
            dataCollectionResultController.notifyDataSetChanged();
    }

    public void updateGeneralItem(){
        generalItemLocalObject =DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemLocalObject.getId());
        generalItemBean = generalItemLocalObject.getGeneralItemBean();
    }

    public boolean showNavigationBar() {
        return true;
    }


    public void setLandscape() {
    }

    public void setPortrait() {
    }

    private void checkInvisibility() {

        String id = GeneralItemVisibilityLocalObject.generateId(generalItemLocalObject.getId(),activity.getGameActivityFeatures().getRunId(),GeneralItemVisibilityLocalObject.INVISIBLE);
        GeneralItemVisibilityLocalObject generalItemVisibilityLocalObject = DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().load(id);
        if (generalItemVisibilityLocalObject != null && generalItemVisibilityLocalObject.getStatus()==GeneralItemVisibilityLocalObject.INVISIBLE) {
            long delta =  generalItemVisibilityLocalObject.getTimeStamp() -ARL.time.getServerTime();
            ARL.time.printTime();
            ARL.time.printTime("will become invisible at ", generalItemVisibilityLocalObject.getTimeStamp());
            long seconds = delta /1000;
            long minutes = seconds /60;
            seconds = seconds %60;
            long hours = minutes /60;
            minutes = minutes %60;
            String countdown = hours+":"+minutes+":"+seconds;
            ARL.time.printTime("will become invisible at "+countdown, generalItemVisibilityLocalObject.getTimeStamp());
//            if (generalItemVisibilityLocalObject.getTimeStamp() < ARL.time.getServerTime()) {
//
//                System.out.println("will become invisible in "+(delta/1000) + " seconds");

//            }
        }
        mHandler.postDelayed(periodicTimeCheck, 1000);
    }

    private Runnable periodicTimeCheck = new Runnable() {
        @Override
        public void run() {

            checkInvisibility();
        }
    };
    private Handler mHandler = new Handler();



}

package org.celstec.arlearn2.android.game.generalItem.dataCollection.impl;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.dataCollection.activities.AudioCollectionActivity;
import org.celstec.arlearn2.android.views.StyleUtil;

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
public class AudioCollectionActivityImpl extends AudioCollectionActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil styleUtil = new StyleUtil(this, R.style.ARLearn_schema1);
        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
        shapeDrawable.setColor(styleUtil.getPrimaryColor());

        ((GradientDrawable) (findViewById(R.id.startRecording)).getBackground()).setColor(styleUtil.getButtonAlternativeColor());
        ((TextView)findViewById(R.id.startRecording)).setTextColor(styleUtil.getTextLight());
    }
    public  int getGameGeneralitemAudioInput() {
        return R.layout.game_general_item_dc_audio_input;
    }

    @Override
    public int getAudioFeedbackView() {
        return R.id.audioFeedbackView;
    }

    @Override
    public int getAudioRecordingLevel0() {
        return R.drawable.game_data_collection_input_norecording;
    }

    @Override
    public int getAudioRecordingLevel1() {
        return R.drawable.game_data_collection_input_recording_level1;
    }

    @Override
    public int getAudioRecordingLevel2() {
        return R.drawable.game_data_collection_input_recording_level2;
    }

    @Override
    public int getAudioRecordingLevel3() {
        return R.drawable.game_data_collection_input_recording_level3;
    }

    @Override
    public int getAudioRecordingLevel4() {
        return R.drawable.game_data_collection_input_recording_level4;
    }

    @Override
    public int getStartRecordingButton() {
        return R.id.startRecording;
    }

    @Override
    public int getStopRecordingButton() {
        return R.id.stopRecording;
    }

    @Override
    public int getSubmitButton() {
        return R.id.dataCollectionSubmit;
    }

    @Override
    public int getCancelButton() {
        return R.id.cancelId;
    }

    @Override
    public int getMediaPlayButton() {
        return R.id.mediaBar;
    }

    @Override
    public int getPlayPauseButton() {
        return R.id.playPauseButton;
    }

    @Override
    public int getDeleteMediaButton() {
        return R.id.deleteRecordingButton;
    }

    @Override
    public int getSeekBar() {
        return R.id.seekbar;
    }


}

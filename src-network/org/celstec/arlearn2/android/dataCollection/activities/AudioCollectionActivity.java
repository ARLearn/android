package org.celstec.arlearn2.android.dataCollection.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.celstec.arlearn2.android.util.MediaFolders;


import java.io.File;
import java.io.IOException;

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
public abstract class AudioCollectionActivity extends Activity {
    static final private double EMA_FILTER = 0.6;

    private File recording = null;
    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    private int mInterval = 100; // 5 seconds by default, can be changed later
    private Handler mHandler = new Handler();

    public  abstract int getGameGeneralitemAudioInput();

    public  abstract int getAudioFeedbackView();
    public  abstract int getStartRecordingButton();
    public  abstract int getStopRecordingButton();
    public  abstract int getSubmitButton();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getGameGeneralitemAudioInput());
        ImageView view = (ImageView) findViewById(getAudioFeedbackView());
        view.setLayoutParams(new LinearLayout.LayoutParams(100,
                100));
        findViewById(getStartRecordingButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startRecording();
                    }
                }
        );
        findViewById(getStopRecordingButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopRecording();
                    }
                }
        );
        findViewById(getSubmitButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        submitAudio();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatusChecker.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRecording();
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void startRecording(){
        if (mRecorder == null) {

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recording = MediaFolders.createOutgoingAmrFile();
            mRecorder.setOutputFile(recording.toString());
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mRecorder.start();
            mEMA = 0.0;
        }
    }

    private void stopRecording(){
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }


    public double getAmplitude() {
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude()/2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }




    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            mHandler.postDelayed(mStatusChecker, mInterval);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((50+(int) (100*getAmplitudeEMA())),
                    100);
            params.gravity= Gravity.CENTER_HORIZONTAL;
            findViewById(getAudioFeedbackView()).setLayoutParams(params);
        }
    };


    public void submitAudio() {
        // TODO

        if (recording!=null) {
            System.out.println(recording.toString());
            Bundle conData = new Bundle();
            conData.putString("filePath", recording.getAbsolutePath());
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }


}

package org.celstec.arlearn2.android.game.generalItem.dataCollection.impl;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.arlearn2.android.views.StyleUtil;
import org.celstec.dao.gen.ResponseLocalObject;
import org.celstec.dao.gen.ResponseLocalObjectDao;

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
public class AudioResultActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    private Drawable playDrawable;
    private Drawable pauseDrawable;
    private ImageView playPauseButton;

    private int status = PAUSE;
    private final static int PAUSE = 0;
    private final static int PLAYING = 1;

    private ResponseLocalObject responseLocalObject;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil styleUtil = DrawableUtil.styleUtil;
        setTheme(styleUtil.getTheme());
        setContentView(R.layout.game_general_item_dc_play_audio);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
        shapeDrawable.setColor(styleUtil.getBackgroundDark());

        pauseDrawable = getResources().getDrawable(R.drawable.btn_pause_black);
        playDrawable = getResources().getDrawable(R.drawable.btn_play_black);
        ColorMatrixColorFilter filter = DrawableUtil.getBlackWhiteFilter(DrawableUtil.styleUtil.getPrimaryColor());
        pauseDrawable.setColorFilter(filter);
        playDrawable.setColorFilter(filter);

        playPauseButton = (ImageView)findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseButton();
                    }
                }
        );
        ((ImageView)findViewById(R.id.playPauseButton)).setImageDrawable(playDrawable);


        ((SeekBar)findViewById(R.id.seekbar)).setThumb(DrawableUtil.getPrimaryColorOvalSeekbar());
        ((SeekBar) findViewById(R.id.seekbar)).setProgressDrawable(DrawableUtil.getSeekBarProgress());

        responseLocalObject = DaoConfiguration.getInstance().getResponseLocalObjectDao().load(getIntent().getLongExtra(ResponseLocalObject.class.getName(), 0l));
        System.out.println(responseLocalObject.getAccount());
        responseLocalObject.getUri();

        findViewById(R.id.cancelId).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (recording!= null && recording.exists()) recording.deleteOnExit();
                        AudioResultActivity.this.finish();
                    }
                }
        );
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preparePlayer();
    }

    private MediaPlayer mediaPlayer;
    public static int oneTimeOnly = 0;
    private double startTime = 0;
    private double finalTime = 0;
    private SeekBar seekbar;
    private Handler playHandler = new Handler();

    public void preparePlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        oneTimeOnly = 0;
        status = PAUSE;
        try {

                Uri uri = responseLocalObject.getUri();
                mediaPlayer.setDataSource(this, uri);
                mediaPlayer.prepare();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void playPauseButton() {
        if (status == PAUSE) {
            playPauseButton.setImageDrawable(pauseDrawable);

            status = PLAYING;
            mediaPlayer.start();

            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            seekbar.setProgress((int)startTime);
            playHandler.postDelayed(UpdateSongTime,100);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    status = PAUSE;
//                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    playPauseButton.setImageDrawable(playDrawable);

                }
            });
        } else {
//            playPauseButton.setIma    geResource(android.R.drawable.ic_media_play);
            playPauseButton.setImageDrawable(playDrawable);
            status = PAUSE;
            mediaPlayer.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            if (status == PAUSE) {
                startTime = 0;
            }
                seekbar.setProgress((int)startTime);
            playHandler.postDelayed(this, 100);

        }
    };

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getProgress() == finalTime){
            status = PAUSE;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        } else
        if (status == PAUSE) {
            mediaPlayer.seekTo(seekBar.getProgress());
        } else {
            mediaPlayer.pause();
            mediaPlayer.seekTo(seekBar.getProgress());
            mediaPlayer.start();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
}

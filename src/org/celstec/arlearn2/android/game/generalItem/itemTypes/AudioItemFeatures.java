package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.io.IOException;
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
public class AudioItemFeatures extends NarratorItemFeatures implements SeekBar.OnSeekBarChangeListener {

    private GameFileLocalObject audioFile;
    private MediaPlayer mediaPlayer;

    private SeekBar seekbar;
    private ImageButton playPauseButton;

    private Handler myHandler = new Handler();

    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;

    private int status = PAUSE;
    private final static int PAUSE = 0;
    private final static int PLAYING = 1;


    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.AUDIO_OBJECT);
    }

    public AudioItemFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
//        GameFileLocalObject.getGameFileLocalObject(generalItemLocalObject.getGameId(), "/generalItems/"+generalItemLocalObject.getId()+"/audio");
        if (DrawableUtil.isInit()) new DrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);

        List<GameFileLocalObject> files = generalItemLocalObject.getGeneralItemFiles();
        if (!generalItemLocalObject.getGeneralItemFiles().isEmpty()) {
            for (GameFileLocalObject gameFileLocalObject: files) {
                if (gameFileLocalObject.getPath().equals("/generalItems/"+generalItemLocalObject.getId()+"/audio")) {
                    audioFile = gameFileLocalObject;
                }
            }

        }

        activity.findViewById(R.id.mediaBar).setVisibility(View.VISIBLE);
        ((SeekBar)activity.findViewById(R.id.seekbar)).setThumb(DrawableUtil.getPrimaryColorOvalSeekbar());

        ((SeekBar) activity.findViewById(R.id.seekbar)).setProgressDrawable(DrawableUtil.getSeekBarProgress());

        Drawable drawable = ((SeekBar) activity.findViewById(R.id.seekbar)).getProgressDrawable();

        playPauseButton  = (ImageButton) activity.findViewById(R.id.playPauseButton);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });

        seekbar = (SeekBar) activity.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
    }

    public void onResumeActivity(){
        super.onResumeActivity();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.reset();
        oneTimeOnly = 0;
        status = PAUSE;
        try {
            if (audioFile != null) {
//                File file = new File(MediaFolders.getIncommingFilesDir().getPath() + "/" + generalItemLocalObject.getGameId() + audioFile.getPath());
                Uri uri = audioFile.getLocalUri();
                mediaPlayer.setDataSource(activity, uri);
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onPauseActivity(){
        super.onPauseActivity();
        mediaPlayer.stop();
    }

    public void playPause(){
        if (status == PAUSE) {
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);

            status = PLAYING;
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
            finalTime = mediaPlayer.getDuration();
            startTime = mediaPlayer.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime,100);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    status = PAUSE;
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);

                }
            });
        } else {
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);

            status = PAUSE;
            mediaPlayer.pause();
        }

    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();

            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

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

}
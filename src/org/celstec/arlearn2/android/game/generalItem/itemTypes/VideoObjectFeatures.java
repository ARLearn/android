package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.views.MyVideoView;
import org.celstec.arlearn2.beans.generalItem.VideoObject;
import org.celstec.dao.gen.GameFileLocalObject;
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
public class VideoObjectFeatures extends NarratorItemFeatures implements SeekBar.OnSeekBarChangeListener {

    private MyVideoView videoView;

    private Drawable playDrawable;
    private Drawable pauseDrawable;
    private ImageView playPauseButton;

    private SeekBar seekbar;

    private Handler myHandler = new Handler();

    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;

    private int status = PAUSE;
    private final static int PAUSE = 0;
    private final static int PLAYING = 1;

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.VIDEO_OBJECT);
    }

    public VideoObjectFeatures(GeneralItemActivity activity, final GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
        videoView = (MyVideoView) activity.findViewById(R.id.videoView);
        videoView.setVisibility(View.VISIBLE);

        activity.findViewById(R.id.mediaBar).setVisibility(View.VISIBLE);
        ((SeekBar)activity.findViewById(R.id.seekbar)).setThumb(DrawableUtil.getPrimaryColorOvalSeekbar());

        ((SeekBar) activity.findViewById(R.id.seekbar)).setProgressDrawable(DrawableUtil.getSeekBarProgress());

        Drawable drawable = ((SeekBar) activity.findViewById(R.id.seekbar)).getProgressDrawable();

        playPauseButton  = (ImageView) activity.findViewById(R.id.playPauseButton);
        pauseDrawable = activity.getResources().getDrawable(R.drawable.btn_pause_black);
        playDrawable = activity.getResources().getDrawable(R.drawable.btn_play_black);
        ColorMatrixColorFilter filter = DrawableUtil.getBlackWhiteFilter(DrawableUtil.styleUtil.getPrimaryColor());
        pauseDrawable.setColorFilter(filter);
        playDrawable.setColorFilter(filter);

        playPauseButton.setImageDrawable(playDrawable);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });

        seekbar = (SeekBar) activity.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(this);
        GameFileLocalObject gameFile = GameFileLocalObject.getGameFileLocalObject(generalItemLocalObject.getGameId(), "/generalItems/" + generalItemLocalObject.getId() + "/video");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(activity, gameFile.getLocalUri());
        videoView.setVideoURI(gameFile.getLocalUri());
        Bitmap frameAtTime = retriever.getFrameAtTime();
        videoView.setVideoSize(frameAtTime.getWidth(),frameAtTime.getHeight());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (((VideoObject)generalItemLocalObject.getGeneralItemBean()).getAutoLaunch()) playPause();

            }
        });

    }

    public void onPauseActivity(){
        super.onPauseActivity();
        videoView.stopPlayback();
    }

    public void playPause(){
        if (status == PAUSE) {
            status = PLAYING;
            playPauseButton.setImageDrawable(pauseDrawable);
            videoView.start();
            finalTime = videoView.getDuration();
            startTime = videoView.getCurrentPosition();
            if(oneTimeOnly == 0){
                seekbar.setMax((int) finalTime);
                oneTimeOnly = 1;
            }
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime,100);
        } else {
            status = PAUSE;
            playPauseButton.setImageDrawable(playDrawable);
            videoView.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = videoView.getCurrentPosition();

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
            videoView.pause();
            videoView.seekTo(0);
        } else
        if (status == PAUSE) {
            videoView.seekTo(seekBar.getProgress());
        } else {
            videoView.pause();
            videoView.seekTo(seekBar.getProgress());
            videoView.start();
        }
    }
}

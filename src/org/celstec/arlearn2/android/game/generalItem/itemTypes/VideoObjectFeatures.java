package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.views.MyVideoView;
import org.celstec.arlearn2.beans.generalItem.VideoObject;
import org.celstec.arlearn2.beans.run.Action;
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
//    public static int oneTimeOnly = 0;

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
//        videoView.setBackgroundColor(DrawableUtil.styleUtil.getBackgroundColor());
        videoView.setZOrderOnTop(true);

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
        long runId = activity.getGameActivityFeatures().getRunId();

        if (!ARL.config.getBooleanProperty("media_player_drag")
                && ActionsDelegator.getInstance().actionDoesNotExist(runId,generalItemLocalObject.getId(), "complete",ARL.accounts.getLoggedInAccount())) {
            seekbar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
        seekbar.setOnSeekBarChangeListener(this);


        GameFileLocalObject gameFile = GameFileLocalObject.getGameFileLocalObject(generalItemLocalObject.getGameId(), "/generalItems/" + generalItemLocalObject.getId() + "/video");
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (gameFile != null) {
            retriever.setDataSource(activity, gameFile.getLocalUri());
            videoView.setVideoURI(gameFile.getLocalUri());
            Bitmap frameAtTime = retriever.getFrameAtTime();
            if (frameAtTime!=null) {
                videoView.setVideoSize(frameAtTime.getWidth(), frameAtTime.getHeight());
                videoView.seekTo(10);

//                videoView.setBackgroundColor(Color.TRANSPARENT);
            }
//            videoView.setBackgroundColor(DrawableUtil.styleUtil.getBackgroundColor());
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
//                    GeneralItem bean = generalItemLocalObject.getGeneralItemBean();
//                    System.out.println(bean.toString());

                    if (((VideoObject) generalItemLocalObject.getGeneralItemBean()).getAutoLaunch()) playPause();

                }
            });

        }
    }

    public void onPauseActivity(){
        super.onPauseActivity();
        videoView.stopPlayback();
        status = PAUSE;




    }

    public void playPause(){

        if (status == PAUSE) {
            status = PLAYING;
            playPauseButton.setImageDrawable(pauseDrawable);
            videoView.start();
            finalTime = videoView.getDuration();
            startTime = videoView.getCurrentPosition();
            seekbar.setMax((int) finalTime);
//            if(oneTimeOnly == 0){
//
//                oneTimeOnly = 1;
//            }
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(UpdateSongTime, 100);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    status = PAUSE;
                    playbackCompleted();
                    playPauseButton.setImageDrawable(playDrawable);
                }
            });
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();

            params.width = metrics.widthPixels;
            params.height = metrics.heightPixels;
            videoView.setLayoutParams(params);
        } else {



            status = PAUSE;
            playPauseButton.setImageDrawable(playDrawable);
            videoView.pause();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = videoView.getCurrentPosition();
            if (!touching)  seekbar.setProgress((int) startTime);
            if (status == PLAYING)
                myHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        touching = true;

    }

    private boolean touching = false;

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        touching = false;
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

    protected void playbackCompleted() {
        Action action = new Action();
        action.setAction("complete");
        action.setRunId(activity.getGameActivityFeatures().getRunId());
        action.setGeneralItemType(generalItemLocalObject.getGeneralItemBean().getType());
        action.setGeneralItemId(generalItemLocalObject.getId());

        ActionsDelegator.getInstance().createAction(action);
    }

    public void setLandscape() {
        activity.findViewById(R.id.titleId).setVisibility(View.GONE);
        activity.findViewById(R.id.generalItemIcon).setVisibility(View.GONE);
        activity.findViewById(R.id.descriptionId).setVisibility(View.GONE);
        activity.getActionBar().hide();
    }

    public void setPortrait() {
        activity.findViewById(R.id.titleId).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.generalItemIcon).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.descriptionId).setVisibility(View.VISIBLE);
        activity.getActionBar().show();
    }

}

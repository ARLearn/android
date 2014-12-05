package org.celstec.arlearn2.android.game.generalItem.dataCollection.impl;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.StyleUtilInterface;
import org.celstec.arlearn2.android.views.MyVideoView;
import org.celstec.arlearn2.beans.generalItem.VideoObject;
import org.celstec.dao.gen.ResponseLocalObject;

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
public class VideoResultActivity extends Activity {

    private ResponseLocalObject responseLocalObject;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtilInterface styleUtil = DrawableUtil.styleUtil;
        setTheme(styleUtil.getTheme());
        getActionBar().hide();
        setContentView(R.layout.game_general_item_result_picture);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
        shapeDrawable.setColor(styleUtil.getBackgroundDark());

        responseLocalObject = DaoConfiguration.getInstance().getResponseLocalObjectDao().load(getIntent().getLongExtra(ResponseLocalObject.class.getName(), 0l));
        ((ImageView)findViewById(R.id.pictureView)).setVisibility(View.GONE);
        ((MyVideoView)findViewById(R.id.videoView)).setVisibility(View.VISIBLE);
        (findViewById(R.id.mediaBar)).setVisibility(View.VISIBLE);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, responseLocalObject.getUri());
        MyVideoView videoView = ((MyVideoView)findViewById(R.id.videoView));
        videoView.setVideoURI(responseLocalObject.getUri());
//        ((ImageView)findViewById(R.id.pictureView)).setImageURI(responseLocalObject.getUri());

        Bitmap frameAtTime = retriever.getFrameAtTime();
        videoView.setVideoSize(frameAtTime.getWidth(),frameAtTime.getHeight());
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
//                if (((VideoObject)generalItemLocalObject.getGeneralItemBean()).getAutoLaunch()) playPause();

            }
        });
        videoView.start();

        findViewById(R.id.cancelId).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        VideoResultActivity.this.finish();
                    }
                }
        );
    }
}

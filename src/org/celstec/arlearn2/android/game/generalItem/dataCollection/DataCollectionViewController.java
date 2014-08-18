package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import android.view.View;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;

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
public abstract class DataCollectionViewController {
    private GeneralItemActivity activity;

    public DataCollectionViewController(GeneralItemActivity activity) {
        this.activity = activity;
        activity.findViewById(R.id.audioButtonIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAudioClick();
            }
        });

        activity.findViewById(R.id.pictureButtonIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPictureClick();
            }
        });

        activity.findViewById(R.id.videoButtonIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onVideoClick();
            }
        });

        activity.findViewById(R.id.textButtonIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTextClick();
            }
        });

        activity.findViewById(R.id.numberButtonIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberClick();
            }
        });
    }

    public void hideDataCollection() {
        activity.findViewById(R.id.dataCollection).setVisibility(View.GONE);
    }

    public void showDataCollection() {
        activity.findViewById(R.id.dataCollection).setVisibility(View.VISIBLE);
    }

    public void setVisibilities(boolean audio, boolean picture, boolean video, boolean numbers, boolean text) {
        if (audio) {
            activity.findViewById(R.id.audioButtonIcon).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.audioButtonCheckIcon).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.audioLayout).setVisibility(View.GONE);
        }
        if (picture) {
            activity.findViewById(R.id.pictureButtonIcon).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.pictureButtonCheckIcon).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.pictureLayout).setVisibility(View.GONE);
        }
        if (video) {
            activity.findViewById(R.id.videoButtonIcon).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.videoButtonCheckIcon).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.videoLayout).setVisibility(View.GONE);
        }
        if (numbers) {
            activity.findViewById(R.id.numberButtonIcon).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.numberButtonCheckIcon).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.numberLayout).setVisibility(View.GONE);
        }
        if (text) {
            activity.findViewById(R.id.textButtonIcon).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.textButtonCheckIcon).setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.textLayout).setVisibility(View.GONE);
        }

    }

    public void checkAudio(){
        activity.findViewById(R.id.audioButtonCheckIcon).setVisibility(View.VISIBLE);
    }

    public void checkPicture(){
        activity.findViewById(R.id.pictureButtonCheckIcon).setVisibility(View.VISIBLE);
    }

    public void checkVideo(){
        activity.findViewById(R.id.videoButtonCheckIcon).setVisibility(View.VISIBLE);
    }

    public void checkText(){
        activity.findViewById(R.id.textButtonCheckIcon).setVisibility(View.VISIBLE);
    }

    public void checkNumbers(){
        activity.findViewById(R.id.numberButtonCheckIcon).setVisibility(View.VISIBLE);
    }

    public abstract void onAudioClick();

    public abstract void onPictureClick();

    public abstract void onVideoClick();

    public abstract void onTextClick();

    public abstract void onNumberClick();

    public void showChecks(LazyListAdapter lazyListAdapter){
        if (lazyListAdapter.hasAudioResult()) {
            activity.findViewById(R.id.audioButtonCheckIcon).setVisibility(View.VISIBLE);
        }
        if (lazyListAdapter.hasPictureResult()) {
            activity.findViewById(R.id.pictureButtonCheckIcon).setVisibility(View.VISIBLE);
        }
        if (lazyListAdapter.hasVideoResult()) {
            activity.findViewById(R.id.videoButtonCheckIcon).setVisibility(View.VISIBLE);
        }
        if (lazyListAdapter.hasTextResult()) {
            activity.findViewById(R.id.textButtonCheckIcon).setVisibility(View.VISIBLE);
        }
        if (lazyListAdapter.hasValueResult()) {
            activity.findViewById(R.id.numberButtonCheckIcon).setVisibility(View.VISIBLE);
        }
    }
}

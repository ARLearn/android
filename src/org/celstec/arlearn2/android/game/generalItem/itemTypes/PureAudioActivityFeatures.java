package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.views.AnimatedGifView;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.io.FileNotFoundException;

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
public class PureAudioActivityFeatures extends AudioItemFeatures {

    public PureAudioActivityFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
//        activity.setContentView(R.layout.game_general_item_pure_audio);
    }

    @Override
    protected boolean showDataCollection() {
        return false;
    }

    @Override
    protected int getImageResource() {
        return 0;
    }

    public void setMetadata(){
        DrawableUtil drawableUtil = ARL.getDrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);
        activity.getActionBar().setBackgroundDrawable(drawableUtil.getBackgroundDarkGradient());

        GameFileLocalObject localObject = GameFileLocalObject.getGameFileLocalObject(generalItemLocalObject.getGameId(), "/generalItems/" + generalItemLocalObject.getId() + "/image");
        Uri uri =localObject.getLocalUri();
        ImageView continueButton = (ImageView) activity.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity == null) System.out.println("activity is null ");
//                if (activity.getInBetweenGeneralItemNavigation() == null) System.out.println("activitygetInBetweenGeneralItemNavigation() is null ");
//                activity.getInBetweenGeneralItemNavigation().navigateNext();
                activity.navigateNext();
            }
        });
        ColorMatrixColorFilter filter = DrawableUtil.getBlackWhiteFilter(DrawableUtil.styleUtil.getPrimaryColor());
        Drawable btnNextLarge = activity.getResources().getDrawable(R.drawable.btn_next_large);
        btnNextLarge.setColorFilter(filter);
        (continueButton).setImageDrawable(btnNextLarge);
//        ((ImageView)activity.findViewById(R.id.imageView)).setImageURI(uri);

    }
    @Override
    public void onResumeActivity(){
        super.onResumeActivity();
        GameFileLocalObject localObject = GameFileLocalObject.getGameFileLocalObject(generalItemLocalObject.getGameId(), "/generalItems/" + generalItemLocalObject.getId() + "/image");
        Uri uri =localObject.getLocalUri();
        try {
            ((AnimatedGifView) activity.findViewById(R.id.imageView)).initializeView(activity.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean showNavigationBar() {
        return false;
    }


    protected void playbackCompleted() {
        super.playbackCompleted();
        activity.findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
    }
}

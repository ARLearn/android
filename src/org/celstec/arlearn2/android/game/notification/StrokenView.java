package org.celstec.arlearn2.android.game.notification;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;

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
public abstract class StrokenView {

    private Activity ctx;
    View strokenView;
    Animation animIn;
    Animation animOut;

    private Handler handler = new Handler();

    boolean open = false;
    public StrokenView(Activity ctx) {
        this.ctx = ctx;
         animIn = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        animOut = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);

        int abContainerViewID = ctx.getResources().getIdentifier("action_bar_container", "id", "android");
        FrameLayout actionBarContainer = (FrameLayout) ctx.findViewById(abContainerViewID);
        LayoutInflater inflater = ctx.getLayoutInflater();
        strokenView = inflater.inflate(R.layout.game_stroken, null);
        strokenView.setVisibility(View.GONE);
        actionBarContainer.addView(strokenView);

        strokenView.findViewById(R.id.viewMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickView();
            }
        });
        strokenView.findViewById(R.id.animStrokenView).setBackgroundColor(ARL.drawableUtil.getStyleUtil().getPrimaryColor());
    }

    public void slideIn(String message, String readMessage) {
        strokenView.setVisibility(View.VISIBLE);
        strokenView.startAnimation(animIn);
        open = true;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (open) slideOut();
            }
        }, 5000);
        if (readMessage !=null){
            ((TextView)strokenView.findViewById(R.id.viewMessageButton)).setText(readMessage);
        }
        if (message != null) {
            ((TextView)strokenView.findViewById(R.id.message)).setText(message);

        }

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ctx, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void slideOut() {
        open = false;
        strokenView.startAnimation(animOut);
        strokenView.setVisibility(View.GONE);
    }

    public abstract void onClickView();

}

package org.celstec.arlearn2.android.game.notification;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.StyleUtilInterface;

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
public abstract class AlertView extends Dialog {
    TextView messageTextView;
    TextView openMessageTextView;
    String message;
    String openMessage;
    public AlertView(Activity ctx) {
        super(ctx, R.style.ARLearn_notificationDialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtilInterface styleUtil = DrawableUtil.styleUtil;
//        getOwnerActivity().setTheme(styleUtil.getTheme());
        setContentView(R.layout.game_alert_window);

        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        findViewById(R.id.closeWindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertView.this.dismiss();
            }
        });

        findViewById(R.id.openMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOpen();
            }
        });
        messageTextView = ((TextView)findViewById(R.id.textViewInformation));
        openMessageTextView = ((TextView)findViewById(R.id.openMessage));
        if (message != null) {
            messageTextView.setText(message);
        }
        if (openMessage != null) {
            openMessageTextView.setText(openMessage);
        }


        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.content)).getBackground());
        shapeDrawable.setColor(styleUtil.getBackgroundDark());
        ((GradientDrawable) findViewById(R.id.openMessage).getBackground()).setColor(DrawableUtil.styleUtil.getPrimaryColor());



    }

    public void show(String message) {
        this.message = message;
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
        playNotification();
        show();
    }

    public void show(String message, String readMessage) {
        this.message = message;
        this.openMessage = readMessage;
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
        if (openMessageTextView != null) {
            openMessageTextView.setText(openMessage);
        }
        playNotification();
        show();
    }

    private void playNotification(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onClickOpen();


}

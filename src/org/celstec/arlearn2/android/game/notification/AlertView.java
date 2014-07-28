package org.celstec.arlearn2.android.game.notification;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;

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
    String message;
    public AlertView(Activity ctx) {
        super(ctx, R.style.ARLearn_notificationDialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (message != null) {
            messageTextView.setText(message);
        }
    }

    public void show(String message) {
        this.message = message;
        if (messageTextView != null) {
            messageTextView.setText(message);
        }
        show();
    }

    public abstract void onClickOpen();


}

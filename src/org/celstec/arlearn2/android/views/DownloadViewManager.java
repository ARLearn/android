package org.celstec.arlearn2.android.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.game.GameDownloadEventInterface;

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
public class DownloadViewManager implements GameDownloadEventInterface {
    ProgressBar messagesProgressBar;
    ProgressBar contentProgressBar;
    private View downloadStatus;
    private int progress = 0;
    private int bytesDownloaded = 0;
    public static final int KBYTE = 1024;

    public DownloadViewManager(View ctx) {
        downloadStatus = ctx.findViewById(R.id.downloadStatus);
//        downloadStatus.setVisibility(View.GONE);
        messagesProgressBar = ((ProgressBar)ctx.findViewById(R.id.messageProgressBar));
        contentProgressBar = ((ProgressBar)ctx.findViewById(R.id.contentProgressBar));
    }

    public void setVisible(){
        downloadStatus.setVisibility(View.VISIBLE);
   }

    public void setGone(){
        downloadStatus.setVisibility(View.GONE);
    }

    @Override
    public void setAmountOfMessages(int messages) {
        if (messagesProgressBar != null) {
            messagesProgressBar.setMax(messages);
        }
    }

    @Override
    public void setAmountOfContentInBytes(long totalContentSizeInBytes) {
        if (contentProgressBar != null) {
            contentProgressBar.setMax((int)(totalContentSizeInBytes/KBYTE));
        }
    }

    @Override
    public void setAmountOfContentDownloadedInBytes(long bytesDownloaded) {
        if (contentProgressBar != null) {
            this.bytesDownloaded = (int)(bytesDownloaded/KBYTE);
            contentProgressBar.setProgress(this.bytesDownloaded );
        }
    }

    @Override
    public void addContentDownloadedInBytes(long bytesDownloaded) {
        if (contentProgressBar != null) {
            this.bytesDownloaded += (int)(bytesDownloaded/KBYTE);
            contentProgressBar.setProgress(this.bytesDownloaded );
        }
//            GameSplashScreen.checkIfDismiss();
    }

    @Override
    public void newMessage() {
        if (messagesProgressBar != null) {
            progress++;
            messagesProgressBar.setProgress(progress);
        }
    }


    public void onDismiss() {
        setGone();
    }
}

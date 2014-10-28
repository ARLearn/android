package org.celstec.arlearn2.android.store;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.RelativeLayout;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager;
import org.celstec.arlearn2.android.views.DownloadViewManager;

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
public class GameDownloadProgressView extends Dialog {

//    ProgressBar messagesProgressBar;
//    ProgressBar contentProgressBar;
//    public static final int KBYTE = 1024;

    GameDownloadManager gameDownloadManager;
//    private int progress = 0;
//    private int bytesDownloaded = 0;
    private DownloadViewManager downloadViewManager;
    private DownloadCompleteInterface gameFragment;

    public GameDownloadProgressView(Context ctx, DownloadCompleteInterface gf, GameDownloadManager gameDownloadManager) {
        super(ctx, R.style.ARLearn_notificationDialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.gameDownloadManager = gameDownloadManager;
    this.gameFragment = gf;
//        this.gameDownloadManager.setDownloadEventListener(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_game_overview_download_status);

        downloadViewManager = new DownloadViewManager(findViewById(R.id.downloadStatus)){

            @Override
            public void onDismiss() {
                GameDownloadProgressView.this.dismiss();
                gameFragment.downloadComplete();
            }
        };
        downloadViewManager.setVisible();
        gameDownloadManager.setDownloadEventListener(downloadViewManager);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
//
//        messagesProgressBar = ((ProgressBar)findViewById(R.id.messageProgressBar));
//
//        if (messagesProgressBar != null) {
//            messagesProgressBar.setMax(50);
//            messagesProgressBar.setProgress(0);
//        }
//
//        contentProgressBar = ((ProgressBar)findViewById(R.id.contentProgressBar));
//
//        if (contentProgressBar != null) {
//            contentProgressBar.setMax(50);
//            contentProgressBar.setProgress(0);
//        }
        ARL.eventBus.post(gameDownloadManager);
    }

    public void register() {
        gameDownloadManager.register();
    }

    public void unregister() {
        gameDownloadManager.unregister();
    }

//    @Override
//    public void setAmountOfMessages(int messages) {
//        if (messagesProgressBar != null) {
//            messagesProgressBar.setMax(messages);
//        }
//    }
//
//    @Override
//    public void setAmountOfContentInBytes(long totalContentSizeInBytes) {
//        if (contentProgressBar != null) {
//            contentProgressBar.setMax((int)(totalContentSizeInBytes/KBYTE));
//        }
//    }
//
//    @Override
//    public void setAmountOfContentDownloadedInBytes(long bytesDownloaded) {
//        if (contentProgressBar != null) {
//            this.bytesDownloaded = (int)(bytesDownloaded/KBYTE);
//            contentProgressBar.setProgress(this.bytesDownloaded );
//        }
//    }
//
//    @Override
//    public void addContentDownloadedInBytes(long bytesDownloaded) {
//        if (contentProgressBar != null) {
//            this.bytesDownloaded += (int)(bytesDownloaded/KBYTE);
//            contentProgressBar.setProgress(this.bytesDownloaded );
//        }
//        checkIfDismiss();
//    }
//
//    @Override
//    public void newMessage() {
//        if (messagesProgressBar != null) {
//            progress++;
//            messagesProgressBar.setProgress(progress);
//        }
//    }
//
//    @Override
//    public void onDismiss() {
//        dismiss();
//    }
//
//    public void checkIfDismiss() {
//        this.onDismiss();
//
//
//    }

    interface DownloadCompleteInterface {
        public void downloadComplete();
    }
}

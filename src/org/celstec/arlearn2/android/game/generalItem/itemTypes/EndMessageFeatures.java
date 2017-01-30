package org.celstec.arlearn2.android.game.generalItem.itemTypes;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.events.EmailSent;
import org.celstec.arlearn2.android.game.NetworkTest;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemMapper;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.arlearn2.android.whitelabel.SplashScreen;
import org.celstec.arlearn2.beans.generalItem.EndMessage;
import org.celstec.dao.gen.AccountLocalObject;
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
public class EndMessageFeatures extends GeneralItemActivityFeatures {
    @Override
    protected boolean showDataCollection() {
        return false;
    }

    @Override
    protected int getImageResource() {
        return GeneralItemMapper.mapConstantToDrawable(GeneralItemMapper.END_MESSAGE);
    }

    public EndMessageFeatures(GeneralItemActivity activity, GeneralItemLocalObject generalItemLocalObject) {
        super(activity, generalItemLocalObject);
    }

    public void setMetadata() {
//        super.setMetadata();
        responsesProgressBar = ((ProgressBar) activity.findViewById(R.id.progressBar));
        DrawableUtil drawableUtil = ARL.getDrawableUtil(activity.getGameActivityFeatures().getTheme(), activity);
        ((GradientDrawable) activity.findViewById(R.id.button).getBackground()).setColor(drawableUtil.styleUtil.getPrimaryColor());
        TextView titleView = (TextView) this.activity.findViewById(R.id.titleId);
        titleView.setText(generalItemLocalObject.getTitle());
        initiateIcon();


    }

    ProgressDialog progressDialog = null;

    private void sendEmail() {
        progressDialog = ProgressDialog.show(activity, "", "Bezig...\n mail versturen");
        ARL.eventBus.post(new NetworkTest());
        ResponseDelegator.getInstance().sendEmail(activity.getGameActivityFeatures().getRunId());
    }

    public void onEventAsync(EmailSent emailSent) {
        if (emailSent.isSuccesfull()) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    EndMessageFeatures.this.activity.findViewById(R.id.button).setVisibility(View.VISIBLE);
                    if (progressDialog != null) {
                        progressDialog.setMessage("Bezig...\n geslaagd");
                        Toast.makeText(activity, "Mail versturen geslaagd.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                }
            });
            AccountLocalObject account = ARL.accounts.getLoggedInAccount();
            account.setFamilyName("sent");
            DaoConfiguration.getInstance().getAccountLocalObjectDao().insertOrReplace(account);
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.setMessage("Bezig...\n fout");
                        Toast.makeText(activity, "Mail versturen mislukt. Controleer je netwerk instellingen...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                }
            });
        }

    }

    public void onEventAsync(NetworkTest networkTest) {
        networkTest.executeTest();
    }

    public void onEventMainThread(NetworkTest.NetworkResult result) {
        if (result.isResult()) {
        } else {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.setMessage("Bezig...\n fout");
                        Toast.makeText(activity, "Mail versturen mislukt. Controleer je netwerk instellingen...", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        progressDialog = null;
                    }

                }
            });
        }
    }

    private boolean syncComplete() {
        return ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId())
                ==
                ResponseDelegator.getInstance().getAmountOfResponses(activity.getGameActivityFeatures().getRunId());
    }

    private void setMetadataSyncReady() {
        TextView buttonView = (TextView) this.activity.findViewById(R.id.button);
        buttonView.setText(((EndMessage) generalItemBean).getResetText());
        this.activity.findViewById(R.id.progressBar).setVisibility(View.GONE);
        responsesProgressBar.setVisibility(View.GONE);
        if (((EndMessage) generalItemBean).getShowReset()) {
            buttonView.setVisibility(View.VISIBLE);
        } else {
            buttonView.setVisibility(View.INVISIBLE);
        }

        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        String baseUrl = "";
        if (ARL.config.getBooleanProperty("white_label") && !ARL.config.getBooleanProperty("white_label_online_sync")) {
            baseUrl = "file:///android_res/raw/";
        } else {
            baseUrl = "file://" + MediaFolders.getIncommingFilesDir().getParent().toString() + "/";
        }
        String prefix = ARL.config.getProperty("message_html_prefix");
        String postfix = ARL.config.getProperty("message_html_postfix");
        if (prefix == null) {
            prefix = "";
        }
        if (postfix == null) {
            postfix = "";
        }
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

//        String addString = "<br><br>"
//                + ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId()) +"/"
//                + ResponseDelegator.getInstance().getAmountOfResponses(activity.getGameActivityFeatures().getRunId());

        webView.loadDataWithBaseURL(baseUrl, prefix + ((EndMessage) generalItemBean).getRichTextSyncReady() + postfix, "text/html", "UTF-8", null);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Deze actie verwijdert al voortgang op je mobieltje. Ben je zeker?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ARL.properties.setAccount(0l);
                                ARL.accounts.setAccount(null);
                                ARL.properties.setAuthToken(null);
                                ARL.accounts.syncMyAccountDetails();
                                activity.finish();
                                Intent anonymousRegistrationScreen = new Intent(activity, SplashScreen.class);
                                activity.startActivity(anonymousRegistrationScreen);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();

//                RunDelegator.getInstance().resetRun(activity.getGameActivityFeatures().getRunId());
            }
        });

    }

    private void setMetadataSyncNotReady() {
        TextView buttonView = (TextView) this.activity.findViewById(R.id.button);
        buttonView.setText(((EndMessage) generalItemBean).getSyncButtonText());
        buttonView.setVisibility(View.VISIBLE);

        WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        String baseUrl = "";
        if (ARL.config.getBooleanProperty("white_label") && !ARL.config.getBooleanProperty("white_label_online_sync")) {
            baseUrl = "file:///android_res/raw/";
        } else {
            baseUrl = "file://" + MediaFolders.getIncommingFilesDir().getParent().toString() + "/";
        }
        String prefix = ARL.config.getProperty("message_html_prefix");
        String postfix = ARL.config.getProperty("message_html_postfix");
        if (prefix == null) {
            prefix = "";
        }
        if (postfix == null) {
            postfix = "";
        }
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);

        webView.loadDataWithBaseURL(baseUrl, prefix + ((EndMessage) generalItemBean).getRichTextSyncNotReady() + postfix, "text/html", "UTF-8", null);

        responsesProgressBar.setVisibility(View.VISIBLE);

        responsesProgressBar.setMax((int) ResponseDelegator.getInstance().getAmountOfResponses(activity.getGameActivityFeatures().getRunId()));
        responsesProgressBar.setProgress((int) ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId()));
        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responsesProgressBar.setProgress((int) ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId()));
                ResponseDelegator.getInstance().syncResponses(activity.getGameActivityFeatures().getRunId());
            }
        });
    }

    ProgressBar responsesProgressBar;

    //    public void onEventMainThread(ResponseEvent event) {
//        if (responsesProgressBar != null){
//            responsesProgressBar.setMax((int) ResponseDelegator.getInstance().getAmountOfResponses(activity.getGameActivityFeatures().getRunId()));
//            responsesProgressBar.setProgress((int) ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId()));
//        }
//        if (syncComplete()){
//            setMetadataSyncReady();
//        }
//    }
    @Override
    public void updateResponses() {
        if (responsesProgressBar != null) {
            responsesProgressBar.setMax((int) ResponseDelegator.getInstance().getAmountOfResponses(activity.getGameActivityFeatures().getRunId()));
            responsesProgressBar.setProgress((int) ResponseDelegator.getInstance().getAmountOfSyncedResponses(activity.getGameActivityFeatures().getRunId()));
        }
        if (syncComplete()) {
            setMetadataSyncReady();
        }
    }

    @Override
    public void onPauseActivity() {
        super.onPauseActivity();
        ARL.eventBus.unregister(this);
    }

    @Override
    public void onResumeActivity() {
        super.onResumeActivity();
        ARL.eventBus.register(this);
        if (ARL.accounts.getLoggedInAccount().getFamilyName() == null){
            sendEmail();
            this.activity.findViewById(R.id.button).setVisibility(View.GONE);
        }
        if (
                syncComplete()) {
            setMetadataSyncReady();
        } else {
            setMetadataSyncNotReady();
        }
    }
}

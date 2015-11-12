package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import authentication.LoginActivity;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MyAccount;
import org.celstec.arlearn2.android.events.RunEvent;
import org.celstec.arlearn2.android.game.InitWhiteLabelDatabase;
import org.celstec.arlearn2.android.game.MyGamesActivity;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.game.messageViews.MessageViewLauncher;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.RunLocalObject;

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
public class AnonymousRegistrationScreen extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARL.init(this);
        GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(InitWhiteLabelDatabase.getGameIds().get(0));
        setTheme(GameActivityFeatures.getTheme(gameLocalObject.getGameBean()));
        setContentView(R.layout.anonymous_registration);
        ARL.getDrawableUtil(SplashScreen.getGameIdToUseForMainSplashScreen(), this);
        ((GradientDrawable) findViewById(R.id.button).getBackground()).setColor(DrawableUtil.styleUtil.getPrimaryColor());

        String prefix = ARL.config.getProperty("message_html_prefix");
        String postfix = ARL.config.getProperty("message_html_postfix");
        if (prefix == null){
            prefix = "";
        }
        if (postfix == null){
            postfix = "";
        }

        WebView webView =
                ((WebView) findViewById(R.id.anonymousRegistrationWebView));
        webView.setBackgroundColor(0x00000000);
        webView.loadData(prefix + "Welkom bij Mijn Stad Heerlen.<br>" +
                "Om de opdrachten in de wandeling te kunnen maken, hebben we je <b>naam</b> of <b>groepsnaam</b> nodig.<br>" +
                "Om je de resultaten te kunnen toesturen, moet je je <b> e-mailadres </b>invullen.<br>" +
                "Veel plezier en leerzame momenten bij de wandeling!<br>" +
                "<br>" +
                "Open Universiteit Nederland" + postfix, "text/html", "utf8");

        ((TextView)findViewById(R.id.title)).setText(gameLocalObject.getTitle());


        SplashScreen.setBackgroundImage(this, "/background");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEditText = (EditText) findViewById(R.id.name);
                EditText emailEditText = (EditText) findViewById(R.id.email);
                System.out.println(nameEditText.getText());
                System.out.println(emailEditText.getText());
                clickButton(emailEditText.getText().toString(), nameEditText.getText().toString());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ARL.eventBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARL.eventBus.register(this);
    }

    ProgressDialog progressDialog = null;

    private void clickButton(String email, String name) {
        if (!email.trim().equals("")&&!name.trim().equals("")) {
            progressDialog = ProgressDialog.show(AnonymousRegistrationScreen.this, "", "Bezig...\n registratie");
            ARL.accounts.createAnonymousAccount(name.trim(), email.trim());
        }
    }

    private void registrationSucceeded() {
        long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.setMessage("Bezig...\n" +
                        " maak run");
            }
        });

        ARL.runs.createRun(gameId);

    }



    public void onEventAsync(MyAccount createAnonymousAccount) {
        if (createAnonymousAccount.getLoggedInAccount() == null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (progressDialog != null) progressDialog.dismiss();
                    Toast.makeText(AnonymousRegistrationScreen.this, "Registratie mislukt. Controleer je netwerk instellingen...", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            registrationSucceeded();
        }

    }

    public void onEventAsync(RunEvent createRunEvent) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (progressDialog != null) progressDialog.dismiss();
            }
        });
        createRunSucceeded();

    }

    private void createRunSucceeded() {
        if (ARL.config.getBooleanProperty("white_label_login") && !ARL.accounts.isAuthenticated()) {
            Intent loginIntent = new Intent(AnonymousRegistrationScreen.this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (SplashScreen.isGroupOfGames()) {
            Intent gameIntent = new Intent(AnonymousRegistrationScreen.this, MyGamesActivity.getMyGamesActivityClass());
            startActivity(gameIntent);
        } else {

            long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
            new MessageViewLauncher(gameId).launchMessageView(AnonymousRegistrationScreen.this);
        }
        AnonymousRegistrationScreen.this.finish();
    }
}

package org.celstec.arlearn2.android.store;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import authentication.LoginFragment;
import com.actionbarsherlock.app.SherlockFragment;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.GameDelegator;
import org.celstec.arlearn2.android.delegators.RunDelegator;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager;
import org.celstec.arlearn2.android.events.GameEvent;
import org.celstec.arlearn2.android.game.GameSplashScreen;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.arlearn2.beans.run.Run;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.StoreGameLocalObject;

import java.text.DateFormat;
import java.util.HashMap;

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
public class GameFragment extends SherlockFragment implements GameDownloadProgressView.DownloadCompleteInterface{

    private long gameId;
    private Run run;
    private View gameView;
    private static HashMap<String, Integer> languageMapping = new HashMap<String, Integer>();


    static {
        languageMapping.put("en", R.string.en);
        languageMapping.put("nl", R.string.nl);
        languageMapping.put("es", R.string.es);
        languageMapping.put("de", R.string.de);
        languageMapping.put("fr", R.string.fr);
        languageMapping.put("it", R.string.it);
        languageMapping.put("pt", R.string.pt);
        languageMapping.put("bg", R.string.bg);
        languageMapping.put("el", R.string.el);
        languageMapping.put("pl", R.string.pl);
        languageMapping.put("ru", R.string.ru);
    }

    private GameDownloadProgressView progressView ;
    private GameDownloadManager gameDownloadManager;
    ProgressDialog pd;

    public GameFragment() {

    }
//    public GameFragment(Game game) {
//        this.gameId = game.getGameId();
//
//    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.gameId = args.getLong("gameId");
        ARL.store.syncStoreGame(gameId);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (progressView == null) {
            gameDownloadManager = new GameDownloadManager(gameId);
            progressView = new GameDownloadProgressView(getActivity(), this, gameDownloadManager);

        }
        ARL.eventBus.register(this);
        progressView.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        ARL.eventBus.unregister(this);
        progressView.unregister();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public Run getRun() {
        return run;
    }

    public void setRun(Run run) {
        this.run = run;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            int rating = ((Integer)data.getExtras().get("rating"));
            GameDelegator.getInstance().rating.submitRating(rating, gameId);
        }
    }

    private void drawGameContent(View v) {
        StoreGameLocalObject localObject = DaoConfiguration.getInstance().getStoreGameLocalObjectDao().load(gameId);
        if (localObject != null) {
            if (DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId)== null) {
                showDownloadButton();
            } else {
                showOpenButton();
            }
            v.findViewById(R.id.gamePane).setVisibility(View.VISIBLE);
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            byte[] data = localObject.getIcon();
            if (localObject.getIcon() != null && data.length != 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                ((ImageView) v.findViewById(R.id.icon)).setImageBitmap(bitmap);
            }
            ((TextView) v.findViewById(R.id.gameTitleId)).setText(localObject.getTitle());

            if (localObject.getDescription() != null){
                WebView mWebView = ((WebView) v.findViewById(R.id.gameStoreDescriptionId));
                mWebView.loadDataWithBaseURL(null, localObject.getDescription(), "text/html", "utf-8", null);
            }


            int resID = 0;
            String licenseCode = localObject.getLicenseCode();
            if (licenseCode != null) {
                if (licenseCode.equals("cc-by")) {
                    resID = R.string.ccby;
                } else if (licenseCode.equals("cc-by-nd")) {
                    resID = R.string.bynd;
                } else if (licenseCode.equals("cc-by-sa")) {
                    resID = R.string.bysa;
                } else if (licenseCode.equals("cc-by-nc")) {
                    resID = R.string.bync;
                } else if (licenseCode.equals("cc-by-nc-sa")) {
                    resID = R.string.byncsa;
                } else if (licenseCode.equals("cc-by-nc-nd")) {
                    resID = R.string.byncnd;
                }
            } else {
                resID = R.string.nolicense;
            }
            ((TextView) v.findViewById(R.id.licenseId)).setText(getString(resID));
            String langKey = localObject.getGameBean().getLanguage();
            if (langKey != null && languageMapping.containsKey(langKey)) {
                Integer language = languageMapping.get(langKey);
                ((TextView) v.findViewById(R.id.languageId)).setText(getString(language));
            }

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(v.getContext());
            ((TextView) v.findViewById(R.id.dateId)).setText(dateFormat.format(localObject.getLastModificationDate()));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gameView = inflater.inflate(R.layout.store_game_overview, container, false);


        gameView.findViewById(R.id.stars).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textInputIntent = new Intent(getActivity(), RateGameActivity.class);
                GameFragment.this.startActivityForResult(textInputIntent, 1);
            }
        });

        TextView downloadButton = (TextView) gameView.findViewById(R.id.downloadId);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ARL.accounts.isAuthenticated()) {
                    progressView.show();
                } else {
                   startLoginActivity();
                }

            }
        });

        TextView openButton = (TextView) gameView.findViewById(R.id.openId);
        openButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (ARL.accounts.isAuthenticated()) {
                    GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
                    if (!gameLocalObject.getRuns().isEmpty())
                        GameSplashScreen.startActivity(getActivity(), gameLocalObject.getId(), gameLocalObject.getRuns().get(0).getId());
                } else {
                    startLoginActivity();
                }
            }
        });

        pd = ProgressDialog.show(getActivity(), getString(R.string.loading), getString(R.string.wait), true);
        gameView.findViewById(R.id.gamePane).setVisibility(View.INVISIBLE);
        drawGameContent(gameView);
        return gameView;
    }

    private void startLoginActivity() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Bundle args = new Bundle();
        Fragment frag = new LoginFragment();

        frag.setArguments(args);
        fm.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.right_pane, frag).addToBackStack(null).commit();
    }

    public void onEventMainThread(GameEvent event) {
        if (event.getGameId() == gameId) {
            gameId = event.getGameId();
            if (gameView != null) drawGameContent(gameView);
        } else {
            if (gameId == 0 && event.getError() == GameEvent.ERROR_SYNCING_FAILED) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
                Toast.makeText(getActivity(), getString(R.string.unabletosyncgame), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void downloadComplete() {
        gameView.findViewById(R.id.downloadId).setVisibility(View.GONE);
        gameView.findViewById(R.id.openId).setVisibility(View.VISIBLE);

        if (run != null) {
           ARL.runs.selfRegister(run.getRunId());
        }

    }

    private void showOpenButton(){
        gameView.findViewById(R.id.downloadId).setVisibility(View.GONE);
        gameView.findViewById(R.id.openId).setVisibility(View.VISIBLE);
    }

    private void showDownloadButton(){
        gameView.findViewById(R.id.downloadId).setVisibility(View.VISIBLE);
        gameView.findViewById(R.id.openId).setVisibility(View.GONE);
    }

}

package org.celstec.arlearn2.android.game;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GamesLazyListAdapter;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GameLocalObjectDao;
import org.celstec.dao.gen.RunLocalObject;

import java.util.Locale;

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
public class MyGamesActivity extends ListActivity implements ListItemClickInterface<GameLocalObject> {

    private GamesLazyListAdapter adapter;


    public static Class getMyGamesActivityClass(){
        if (ARL.config.getContentView() == R.layout.mygames_list_grid) return MyGamesGridActivity.class;
        return MyGamesActivity.class;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ARL.config.getContentView());
        ARL.init(this);
        init();
        if (ARL.config.containsKey("gameIdToUseForMainSplashScreen")) {
            Long gameIdToUseForMainSplashScreen = null;
            try{
                gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen_"+ Locale.getDefault().getLanguage()));
            } catch (NumberFormatException e ) {}

            if (gameIdToUseForMainSplashScreen == null) gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen"));

                Drawable background = GameFileLocalObject.getDrawable(this, gameIdToUseForMainSplashScreen, "/background");
                if (background != null)
                    ((LinearLayout) findViewById(R.id.storeLinearLayout)).setBackground(background);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                getActionBar().setHomeButtonEnabled(true);
                GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameIdToUseForMainSplashScreen);
                int theme = GameActivityFeatures.getTheme(gameLocalObject.getGameBean());

                ARL.getDrawableUtil(theme, this);
                getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));
            }
        }


    }

    public void init(){
        if (ARL.config.containsKey("white_label_title") && ARL.config.getBooleanProperty("white_label")){
            ((TextView)findViewById(R.id.myGamesText)).setText(ARL.config.getProperty("white_label_title"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    public void resume(){
        adapter = new GamesLazyListAdapter(this);
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
    }

    @Override
    public void onListItemClick(View v, int position, GameLocalObject game) {
        if (ARL.config.getBooleanProperty("gameSplashScreen")) {
            GameSplashScreen.startActivity(this, game.getId(), game.getRuns().get(0).getId());
        } else {

            int amountOfRuns = 0;
            RunLocalObject lastRun = null;
            for (RunLocalObject run: game.getRuns()) {
                if (run.getDeleted() == null || !run.getDeleted()) {
                    amountOfRuns++;
                    lastRun = run;
                }
            }
            if (amountOfRuns ==0) {
                //TODO enable user to create a run
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyGamesActivity.this, "Create or join a run first, via the the ARLearn web interface...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (amountOfRuns == 1) {
                GameMessages.startActivity(this, game.getId(),  lastRun.getId());
            } else {
                MyRunsActivity.startActivity(this, game.getId());
            }

        }
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, GameLocalObject object) {
        return false;
    }

}

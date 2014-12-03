package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.InitWhiteLabelDatabase;
import org.celstec.arlearn2.android.game.MyGamesActivity;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.dao.gen.GameFileLocalObject;
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
public class SplashScreen extends Activity implements Runnable{

    private long timeForLaunchNextScreen;
    private Handler handler = new Handler();
    private boolean databaseInitReady = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);

        if (!ARL.isInit()) {
            ARL.init(this);
        }
            Drawable splashDrawable = GameFileLocalObject.getDrawable(this, InitWhiteLabelDatabase.getGameIds().get(0), "/gameSplashScreen");
            if (splashDrawable != null) {
                ((ImageView)findViewById(R.id.main_backgroundImage)).setImageDrawable(splashDrawable);
                findViewById(R.id.downloadStatus).setVisibility(View.GONE);
            }

        timeForLaunchNextScreen = System.currentTimeMillis() + 3000;
        handler.postDelayed(this, 500);
        initDatabase();
        ARL.accounts.syncMyAccountDetails();
        databaseInitReady = true;
    }

    public void run() {
        if (timeForLaunchNextScreen < System.currentTimeMillis() && databaseInitReady) {
            launchNextActivity();
        } else {
            handler.postDelayed(this, 500);
        }
    }

    private void initDatabase() {
        if (databaseNotLoaded()) {
            InitWhiteLabelDatabase initWhiteLabelDatabase = new InitWhiteLabelDatabase(this);
            initWhiteLabelDatabase.init();
        }
    }

    private boolean databaseNotLoaded() {
        return (DaoConfiguration.getInstance().getGameLocalObjectDao().loadAll().isEmpty());
    }

    private void launchNextActivity(){
        if (isGroupOfGames()) {
            Intent gameIntent = new Intent(this, MyGamesActivity.class);
            startActivity(gameIntent);
        } else {

            long gameId = InitWhiteLabelDatabase.getGameIds().get(0);
            GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
            Intent gameIntent = new Intent(this, GameMessages.class);
            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
            gameIntent.putExtra(RunLocalObject.class.getName(), gameLocalObject.getRuns().get(0).getId());
            startActivity(gameIntent);
//
//            ARL.actions.createAction(runLocalObject.getId(), "startRun");
        }
        this.finish();
    }

    private boolean isGroupOfGames(){
        return InitWhiteLabelDatabase.getGameIds().size() > 1;
    }
}

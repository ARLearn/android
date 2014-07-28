package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import daoBase.DaoConfiguration;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GameEvent;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GameLocalObjectDao;
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
public class GameSplashScreen extends Activity {

    GameLocalObject gameLocalObject;
    RunLocalObject runLocalObject;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_splash_screen);
        if (!ARL.isInit()) ARL.init(this);

//        ARL.eventBus.register(this);

        if (ARL.config.getBooleanProperty("white_label")) {
            whiteLabelMetadata();
        } else {
            nativeArlearnMetadata();
        }
    }

    private void whiteLabelMetadata() {
        Long gameId = Long.parseLong(ARL.config.getProperty("white_label_gameId"));
        Long runId = Long.parseLong(ARL.config.getProperty("white_label_runId"));
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        if (ARL.config.getBooleanProperty("white_label_online")) {
            syncGameContent();
        }
    }


    private void nativeArlearnMetadata(){
        Long gameId = getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
        Long runId = getIntent().getLongExtra(RunLocalObject.class.getName(), 0l);
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        syncGameContent();
    }

    private void syncGameContent() {
        ARL.generalItems.syncGeneralItems(gameLocalObject);
        ARL.generalItemVisibility.syncGeneralItemVisibilities(runLocalObject);
        //TODO
        //ARL.responses.syncResponses(runId);
        //ARL.actions.sync

    }


    @Override
    public void onResume() {
        super.onResume();
        new DelayedGameLauncher(gameLocalObject.getId(), runLocalObject.getId(), this, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ARL.eventBus.unregister(this);
    }



}

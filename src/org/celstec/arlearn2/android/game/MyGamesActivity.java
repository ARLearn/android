package org.celstec.arlearn2.android.game;

import android.app.ListActivity;
import android.content.Intent;
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
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GamesLazyListAdapter;
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
public class MyGamesActivity extends ListActivity implements ListItemClickInterface<GameLocalObject> {

    private GamesLazyListAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygames_list);
        if (ARL.config.containsKey("white_label_title") && ARL.config.getBooleanProperty("white_label")){
            ((TextView)findViewById(R.id.myGamesText)).setText(ARL.config.getProperty("white_label_title"));
        }
        if (ARL.config.containsKey("gameIdToUseForMainSplashScreen")) {
            Long gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen"));

                Drawable background = GameFileLocalObject.getDrawable(this, gameIdToUseForMainSplashScreen, "/background");
                if (background != null)
                    ((LinearLayout) findViewById(R.id.storeLinearLayout)).setBackground(background);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ARL.eventBus.register(this);
        adapter = new GamesLazyListAdapter(this);
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
    }

    @Override
    public void onListItemClick(View v, int position, GameLocalObject game) {
        if (ARL.config.getBooleanProperty("gameSplashScreen")) {
            GameSplashScreen.startActivity(this, game.getId(), game.getRuns().get(0).getId());
        } else {
            int amountOfRuns = game.getRuns().size();
            if (amountOfRuns ==0) {
                //TODO enable user to create a run
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MyGamesActivity.this, "Create or join a run first, via the the ARLearn web interface...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (amountOfRuns == 1) {
//                RunLocalObject runLocalObject = new RunLocalObject();
//                runLocalObject.setGameId(game.getId());
//                runLocalObject.setTitle("Default2");
//                runLocalObject.setId(2l);
//                runLocalObject.setDeleted(false);
//                DaoConfiguration.getInstance().getRunLocalObjectDao().insertOrReplace(runLocalObject);

                GameMessages.startActivity(this, game.getId(),  game.getRuns().get(0).getId());
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

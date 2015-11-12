package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.MapView;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.internal.DaoConfig;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.dao.gen.ActionLocalObject;
import org.celstec.dao.gen.ResponseLocalObject;

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
public class ActionBarMenuController {

    private Activity activity;
    private GameActivityFeatures gameActivityFeatures;

    public ActionBarMenuController(Activity activity, GameActivityFeatures gameActivityFeatures) {
        this.activity = activity;
        this.gameActivityFeatures = gameActivityFeatures;
    }

    public void inflateMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        if (gameActivityFeatures.getGameLocalObject().getMapAvailable()== null ||!gameActivityFeatures.getGameLocalObject().getMapAvailable()) {
            inflater.inflate(R.menu.game_no_messages, menu);
        } else {
            if (activity instanceof GameMessages) {
                inflater.inflate(R.menu.game_messages_actions, menu);


            } else {
//                inflater.inflate(R.menu.game_messages_actions, menu);
                                inflater.inflate(R.menu.game_map_actions, menu);
            }

            if (menu.findItem(R.id.score) != null) {
                menu.findItem(R.id.score).setVisible(true);
                menu.findItem(R.id.score).setTitle(""+ARL.properties.getScore(this.gameActivityFeatures.getRunId()));

//                menu.findItem(R.id.score).setVisible(ARL.properties.hasScore(this.gameActivityFeatures.getRunId()));
//                menu.findItem(R.id.score).setTitle(""+ARL.properties.getScore(this.gameActivityFeatures.getRunId()));
            }

        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map_view:
                if (isMessages()) {
                    Intent intent = new Intent(activity, GameMap.class);
                    gameActivityFeatures.addMetadataToIntent(intent);
                    activity.startActivity(intent);
                    activity.finish();
                }
                return true;
            case R.id.action_message_view:
                if (isMap()) {
                    Intent intent = new Intent(activity, GameMessages.class);
                    gameActivityFeatures.addMetadataToIntent(intent);
                    activity.startActivity(intent);
                    activity.finish();
                }
                return true;
            case android.R.id.home:
                activity.finish();
                return true;
            case R.id.score:
//                runOnUiThread(new Runnable() {
//                    public void run() {
                long firstDate = Long.MAX_VALUE;
                long lastDate = Long.MIN_VALUE;

                for (ResponseLocalObject action: DaoConfiguration.getInstance().getResponseLocalObjectDao().loadAll()){
                    if (action.getTimeStamp()<firstDate) firstDate = action.getTimeStamp();
                    if (action.getTimeStamp()>lastDate) lastDate = action.getTimeStamp();
                }
                if (DaoConfiguration.getInstance().getActionLocalObjectDao().loadAll().isEmpty()){
                    Toast.makeText(activity, "no responses given", Toast.LENGTH_SHORT).show();
                } else {
                    long time = lastDate - firstDate;
                    time = time / 1000 / 60;
                    Toast.makeText(activity, "you worked for "+time + " minutes", Toast.LENGTH_SHORT).show();
                }

//                    }
//                });
                return true;
            default:
                return false;
        }
    }

    public boolean isMap(){
        return activity instanceof GameMap;
    }

    public boolean isMessages(){
        return activity instanceof GameMessages;
    }

    public void updateScore(Menu menu) {
        if (ARL.config.getBooleanProperty("show_score")){
            menu.findItem(R.id.score).setTitle(""+ARL.properties.getScore(this.gameActivityFeatures.getRunId()));
        }
    }
}

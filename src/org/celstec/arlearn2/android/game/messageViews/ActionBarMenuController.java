package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.maps.MapView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;

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
        inflater.inflate(R.menu.game_messages_actions, menu);
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
}

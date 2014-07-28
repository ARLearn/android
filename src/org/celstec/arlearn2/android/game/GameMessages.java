package org.celstec.arlearn2.android.game;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.notification.StrokenView;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GeneralItemVisibilityAdapter;
import org.celstec.arlearn2.android.listadapter.impl.GeneralItemsLazyListAdapter;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
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
public class GameMessages extends ListActivity implements ListItemClickInterface<GeneralItemVisibilityLocalObject> {
    GameActivityFeatures gameActivityFeatures;

    private GeneralItemVisibilityAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_list_messages);
        gameActivityFeatures = new GameActivityFeatures(this);
        adapter = new GeneralItemVisibilityAdapter(this, gameActivityFeatures.getRunId());
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
        ARL.generalItems.syncGeneralItems(gameActivityFeatures.getGameLocalObject());
//        Handler handler = new Handler();
//        handler.postDelayed(new AnimationRunnable(), 2000l);

    }

    @Override
    public void onListItemClick(View v, int position, GeneralItemVisibilityLocalObject object) {
        Intent intent = new Intent(this, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent);
        intent.putExtra(GeneralItemLocalObject.class.getName(), object.getGeneralItemLocalObject().getId());
        startActivity(intent);
    }

    @Override
    public boolean setOnLongClickListener(View v, int position, GeneralItemVisibilityLocalObject object) {
        return false;
    }


    class AnimationRunnable implements Runnable {
//        StrokenView view;
        public void run() {
//            gameActivityFeatures.showStrokenNotification();
            gameActivityFeatures.showAlertViewNotification();
        }
    }
}

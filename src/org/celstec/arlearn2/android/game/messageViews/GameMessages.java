package org.celstec.arlearn2.android.game.messageViews;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.notification.NotificationAction;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GeneralItemVisibilityAdapter;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.arlearn2.android.views.StyleUtil;
import org.celstec.dao.gen.GameFileLocalObject;
import org.celstec.dao.gen.GameFileLocalObjectDao;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;

import java.util.List;

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
    ActionBarMenuController actionBarMenuController;

    private GeneralItemVisibilityAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        gameActivityFeatures = new GameActivityFeatures(this);
        setTheme(gameActivityFeatures.getTheme());
        new DrawableUtil(gameActivityFeatures.getTheme(), this);
        setContentView(R.layout.game_list_messages);
        getActionBar().setIcon(R.drawable.ic_ab_back);
        getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));

        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);
        Drawable messagesHeader = GameFileLocalObject.getDrawable(this, gameActivityFeatures.gameLocalObject.getId(), "/gameMessagesHeader");
        if (messagesHeader != null) {
            ((ImageView)findViewById(R.id.gameHeader)).setImageDrawable(messagesHeader);
        }

//        createVisibilityStatement(885015l);
//        createVisibilityStatement(20196003l);
//        createVisibilityStatement(1059001l);
//        createVisibilityStatement(881016l);
//        createVisibilityStatement(881017l);
//        createVisibilityStatement(835208l);
//        createVisibilityStatement(20166030l);
//        createVisibilityStatement(865017l);
//        createVisibilityStatement(1071003l);
//        createVisibilityStatement(1299059l);
//        createVisibilityStatement(866026l);
//        createVisibilityStatement(1105009l);
//        createVisibilityStatement(883021l);
//        createVisibilityStatement(884022l);
//        createVisibilityStatement(870030l);
//        createVisibilityStatement(1313001l);
//        createVisibilityStatement(884021l);
//        createVisibilityStatement(865018l);
//        createVisibilityStatement(886019l);
//        createVisibilityStatement(1279031l);


    }

    private void createVisibilityStatement (long visibility) {
        GeneralItemVisibilityLocalObject visibilityLocalObject = new GeneralItemVisibilityLocalObject();
        visibilityLocalObject.setGeneralItemId(visibility);
        visibilityLocalObject.setTimeStamp(System.currentTimeMillis());
        visibilityLocalObject.setStatus(1);
        visibilityLocalObject.setRunId(gameActivityFeatures.getRunId());
        visibilityLocalObject.setAccount(ARL.accounts.getLoggedInAccount().getFullId());
        String id = GeneralItemVisibilityLocalObject.generateId(gameActivityFeatures.getRunId(), visibilityLocalObject.getGeneralItemId());
        visibilityLocalObject.setId(id);
        DaoConfiguration.getInstance().getGeneralItemVisibilityLocalObjectDao().insertOrReplace(visibilityLocalObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARL.eventBus.register(this);
        adapter = new GeneralItemVisibilityAdapter(this, gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
        ARL.generalItems.syncGeneralItems(gameActivityFeatures.getGameLocalObject());
        ARL.generalItemVisibility.calculateVisibility(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
        GeneralItemEvent event = (GeneralItemEvent) ARL.eventBus.removeStickyEvent(GeneralItemEvent.class);
        if (event !=null) onEventMainThread(event);
    }

    public void onEventMainThread(final GeneralItemEvent event) {
        System.out.println("LOG onEventMainThread "+System.currentTimeMillis());
        if (event.isBecameVisible()) gameActivityFeatures.showStrokenNotification(new NotificationAction() {
            @Override
            public void onOpen() {

                Intent intent = new Intent(GameMessages.this, GeneralItemActivity.class);
                gameActivityFeatures.addMetadataToIntent(intent);
                intent.putExtra(GeneralItemLocalObject.class.getName(), event.getGeneralItemId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBarMenuController.inflateMenu(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarMenuController.onOptionsItemSelected(item);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ARL.eventBus.unregister(this);
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
        public void run() {
//            gameActivityFeatures.showStrokenNotification();
            gameActivityFeatures.showAlertViewNotification();
        }
    }
}

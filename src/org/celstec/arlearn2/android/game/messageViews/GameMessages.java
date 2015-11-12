package org.celstec.arlearn2.android.game.messageViews;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemBecameVisibleEvent;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.events.RunEvent;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.notification.NotificationAction;
import org.celstec.arlearn2.android.listadapter.ListItemClickInterface;
import org.celstec.arlearn2.android.listadapter.impl.GeneralItemVisibilityAdapter;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.*;

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
    private Menu menu;

    private GeneralItemVisibilityAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        ARL.init(this);
        if (ARL.config.isGameMapActionBarTransparent()){
            getWindow().requestFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        }

        super.onCreate(savedInstanceState);
        ARL.accounts.syncMyAccountDetails();
        gameActivityFeatures = new GameActivityFeatures(this);
        ARL.proximityEvents.createEvents(gameActivityFeatures.getRunLocalObject());
        setTheme(gameActivityFeatures.getTheme());
        DrawableUtil drawableUtil = ARL.getDrawableUtil(gameActivityFeatures.getTheme(), this);

        setContentView(R.layout.game_list_messages);

        boolean enabled =ARL.config.getBooleanProperty("game_messages_show_home");
        if (android.os.Build.VERSION.SDK_INT >= 11)  getActionBar().setHomeButtonEnabled(enabled);
                getActionBar().setDisplayShowHomeEnabled(enabled);
                getActionBar().setDisplayShowTitleEnabled(enabled);
                if (ARL.config.isGameMessagesActionBarTransparent()){
                    if (ARL.config.getGameMessagesActionBarTransparency()==ARL.config.TRANSPARENT){
                        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                    if (ARL.config.getGameMessagesActionBarTransparency()==ARL.config.HALF){
                        getActionBar().setBackgroundDrawable(drawableUtil.getBackgroundDarkGradientTransparancy());
                    }
                } else {
                    getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));
                }


        actionBarMenuController = new ActionBarMenuController(this, gameActivityFeatures);
        Drawable messagesHeader = GameFileLocalObject.getDrawable(this, gameActivityFeatures.gameLocalObject.getId(), "/gameMessagesHeader");
        if (messagesHeader != null) {
            ((ImageView)findViewById(R.id.gameHeader)).setImageDrawable(messagesHeader);
        }


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
        if (menu!=null) actionBarMenuController.updateScore(menu);
        adapter = new GeneralItemVisibilityAdapter(this, gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId(),  ARL.config.getProperty("message_view_messages").equals("messages_only"));
        gameActivityFeatures.checkRunDeleted(this);
        setListAdapter(adapter);
        adapter.setOnListItemClickCallback(this);
        ARL.generalItems.syncGeneralItems(gameActivityFeatures.getGameLocalObject());
        ARL.generalItemVisibility.calculateVisibility(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
        ARL.generalItemVisibility.calculateInVisibility(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());
        ARL.generalItemVisibility.syncGeneralItemVisibilities(gameActivityFeatures.getRunLocalObject());

        GeneralItemBecameVisibleEvent event = (GeneralItemBecameVisibleEvent) ARL.eventBus.removeStickyEvent(GeneralItemBecameVisibleEvent.class);
        if (event !=null) onEventMainThread(event);
    }

    public void onEventMainThread(final GeneralItemBecameVisibleEvent event) {
        event.processEvent(gameActivityFeatures, this, null);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void onEventMainThread(RunEvent runEvent) {
        if (runEvent.getRunId() == gameActivityFeatures.getRunId() && runEvent.isDeleted()){
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        actionBarMenuController.inflateMenu(menu);
        this.menu = menu;
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gameActivityFeatures.saveState(outState);
    }

    public static void startActivity(Context ctx, long gameId, long runId){
            Intent gameIntent = new Intent(ctx, GameMessages.class);
            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
            gameIntent.putExtra(RunLocalObject.class.getName(), runId);
            ctx.startActivity(gameIntent);
    }
}

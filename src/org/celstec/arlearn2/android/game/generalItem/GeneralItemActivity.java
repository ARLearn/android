package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.events.ResponseEvent;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.notification.NotificationAction;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.dao.gen.GeneralItemLocalObject;

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
public class GeneralItemActivity extends Activity {

    private GameActivityFeatures gameActivityFeatures;
    GeneralItemActivityFeatures generalItemActivityFeatures;
    InBetweenGeneralItemNavigation inBetweenGeneralItemNavigation;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameActivityFeatures = new GameActivityFeatures(this);
        setTheme(gameActivityFeatures.getTheme());
        setContentView(R.layout.game_general_item);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().setIcon(R.drawable.ic_ab_back);
            getActionBar().setTitle(getString(R.string.messages));
            getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));
        }

        generalItemActivityFeatures = GeneralItemActivityFeatures.getGeneralItemActivityFeatures(this);
        ARL.actions.issueAction(ActionsDelegator.READ,
                gameActivityFeatures.getRunId(),
                generalItemActivityFeatures.generalItemLocalObject.getId(),
                generalItemActivityFeatures.generalItemLocalObject.getGeneralItemBean().getType());
        inBetweenGeneralItemNavigation = new InBetweenGeneralItemNavigation(this, gameActivityFeatures, generalItemActivityFeatures);
    }

    public void onEventMainThread(final GeneralItemEvent event) {
        System.out.println("LOG onEventMainThread "+System.currentTimeMillis());
        generalItemActivityFeatures.updateGeneralItem();
        inBetweenGeneralItemNavigation.updateMessagesHeader();
        if (event.isBecameVisible()) gameActivityFeatures.showStrokenNotification(new NotificationAction() {
            @Override
            public void onOpen() {

                Intent intent = new Intent(GeneralItemActivity.this, GeneralItemActivity.class);
                gameActivityFeatures.addMetadataToIntent(intent);
                intent.putExtra(GeneralItemLocalObject.class.getName(), event.getGeneralItemId());
                startActivity(intent);
                finish();
            }
        });
        if (event.getGeneralItemId() == generalItemActivityFeatures.generalItemLocalObject.getId()){
            Boolean deleted = generalItemActivityFeatures.generalItemLocalObject.getDeleted();
            if (deleted!= null && deleted){
                GeneralItemActivity.this.finish();
            }
            generalItemActivityFeatures.setMetadata();
        }
    }


    public void onEventMainThread(ResponseEvent event) {
        generalItemActivityFeatures.updateResponses();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ARL.eventBus.unregister(this);
        generalItemActivityFeatures.onPauseActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ARL.eventBus.register(this);
        generalItemActivityFeatures.onResumeActivity();
        System.out.println("LOG onResume "+System.currentTimeMillis());
        GeneralItemEvent event = (GeneralItemEvent) ARL.eventBus.removeStickyEvent(GeneralItemEvent.class);
        if (event !=null) onEventMainThread(event);
//        inBetweenGeneralItemNavigation.updateMessagesHeader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (generalItemActivityFeatures != null)
            generalItemActivityFeatures.onActivityResult(requestCode, resultCode, data);
        generalItemActivityFeatures.updateResponses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;

    }

    public GameActivityFeatures getGameActivityFeatures() {
        return gameActivityFeatures;
    }

    public void setGameActivityFeatures(GameActivityFeatures gameActivityFeatures) {
        this.gameActivityFeatures = gameActivityFeatures;
    }

    public InBetweenGeneralItemNavigation getInBetweenGeneralItemNavigation() {
        return inBetweenGeneralItemNavigation;
    }
}

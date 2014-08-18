package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObjectDao;

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
        setTheme(R.style.ARLearn_schema2);
        setContentView(R.layout.game_general_item);
        gameActivityFeatures = new GameActivityFeatures(this);
        generalItemActivityFeatures = GeneralItemActivityFeatures.getGeneralItemActivityFeatures(this);
        ARL.actions.issueAction(ActionsDelegator.READ,
                gameActivityFeatures.getRunId(),
                generalItemActivityFeatures.generalItemLocalObject.getId(),
                generalItemActivityFeatures.generalItemLocalObject.getGeneralItemBean().getType());
        inBetweenGeneralItemNavigation = new InBetweenGeneralItemNavigation(this, gameActivityFeatures, generalItemActivityFeatures);
    }

    public void onEventMainThread(GeneralItemEvent event) {
        if (event.getGeneralItemId() == generalItemActivityFeatures.generalItemLocalObject.getId()){
            Boolean deleted = generalItemActivityFeatures.generalItemLocalObject.getDeleted();
            if (deleted!= null && deleted){
                GeneralItemActivity.this.finish();;
            }
            generalItemActivityFeatures.setMetadata();
        }
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (generalItemActivityFeatures != null)
            generalItemActivityFeatures.onActivityResult(requestCode, resultCode, data);
    }

    public GameActivityFeatures getGameActivityFeatures() {
        return gameActivityFeatures;
    }

    public void setGameActivityFeatures(GameActivityFeatures gameActivityFeatures) {
        this.gameActivityFeatures = gameActivityFeatures;
    }
}

package org.celstec.arlearn2.android.game.messageViews;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GeneralItemEvent;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.notification.AlertView;
import org.celstec.arlearn2.android.game.notification.NotificationAction;
import org.celstec.arlearn2.android.game.notification.StrokenView;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;
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
public class GameActivityFeatures {

    protected GameLocalObject gameLocalObject;
    protected RunLocalObject runLocalObject;
    private Activity activity;

    private StrokenView strokenView;

    private AlertView alertView;

    public GameActivityFeatures(final Activity activity) {
        this.activity = activity;
//        this.activity.setTheme(R.style.ARLearn_schema2);
        Long gameId = activity.getIntent().getLongExtra(GameLocalObject.class.getName(), 0l);
        Long runId = activity.getIntent().getLongExtra(RunLocalObject.class.getName(), 0l);
        gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        runLocalObject = DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId);
        ARL.getDrawableUtil(getTheme(), activity);
        if (gameLocalObject == null) throw new NullPointerException("Game object is null");
        strokenView = new StrokenView(activity) {
            @Override
            public void onClickView() {
                onClickStroken();
            }
        };

        alertView = new AlertView(activity) {
            @Override
            public void onClickOpen() {

            }


        };
    }

    public void saveState(Bundle savedInstanceState) {
        savedInstanceState.putLong(GameLocalObject.class.getName(), getGameId());
        savedInstanceState.putLong(RunLocalObject.class.getName(), getRunId());
    }

    public long getGameId(){
        return gameLocalObject.getId();
    }

    public long getRunId(){
        return runLocalObject.getId();
    }

    public void showStrokenNotification() {
        strokenView.slideIn();
    }

    public void showStrokenNotification(NotificationAction action) {
        this.action = action;
        strokenView.slideIn();
    }

    NotificationAction action;

    private void onClickStroken() {
        if (action != null) {
            action.onOpen();

        }
        strokenView.slideOut();
    }

    public void showAlertViewNotification(){
        alertView.show("Hier komt de bericht tekst...");
    }


    public GameLocalObject getGameLocalObject() {
        return gameLocalObject;
    }

    public RunLocalObject getRunLocalObject() {
        return runLocalObject;
    }

    public void addMetadataToIntent(Intent intent) {
        intent.putExtra(GameLocalObject.class.getName(), gameLocalObject.getId());
        intent.putExtra(RunLocalObject.class.getName(), runLocalObject.getId());
    }

    public int getTheme() {
        if (gameLocalObject == null) return R.style.ARLearn_schema1;
        Game game = gameLocalObject.getGameBean();
        return getTheme(game);

    }

    public static int getTheme(Game game) {
        switch (game.getTheme()){
            case 1:
                return R.style.ARLearn_schema1;
            case 2:
                return R.style.ARLearn_schema2;
            case 3:
                return R.style.ARLearn_schema3;
            case 4:
                return R.style.ARLearn_schema4;
            case 5:
                return R.style.ARLearn_schema5;
        }
        return R.style.ARLearn_schema1;
    }

    public void checkRunDeleted(Activity activity) {
        runLocalObject.refresh();
        if (getRunLocalObject().getDeleted()!= null &&getRunLocalObject().getDeleted()){
            activity.finish();
        }

    }
}

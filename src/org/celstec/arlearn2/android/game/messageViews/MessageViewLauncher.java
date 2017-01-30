package org.celstec.arlearn2.android.game.messageViews;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
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
public class MessageViewLauncher {

    private Long gameId;

    public MessageViewLauncher(Long gameId) {
        this.gameId = gameId;
    }

    public void launchMessageView(Context ctx) {
        GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        long runId = gameLocalObject.getRuns().get(0).getId();
        ARL.actions.createAction(runId, "startRun");
        ARL.generalItems.syncGeneralItems(gameId);
        ARL.generalItemVisibility.calculateVisibility(runId, gameId);
        if (ARL.config.getProperty("message_views").startsWith("messages")) {
            Intent gameIntent = new Intent(ctx, GameMessages.class);
            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
            if (!gameLocalObject.getRuns().isEmpty()) gameIntent.putExtra(RunLocalObject.class.getName(), runId);
            ctx.startActivity(gameIntent);
        } else if (ARL.config.getProperty("message_views").startsWith("map")) {

            Intent gameIntent = new Intent(ctx, GameMap.class);
            gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
            if (!gameLocalObject.getRuns().isEmpty()) gameIntent.putExtra(RunLocalObject.class.getName(), runId);
            ctx.startActivity(gameIntent);
        }
    }
}

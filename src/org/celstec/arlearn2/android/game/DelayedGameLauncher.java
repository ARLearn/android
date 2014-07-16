package org.celstec.arlearn2.android.game;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import org.celstec.dao.gen.GameLocalObject;

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
public class DelayedGameLauncher  implements  Runnable {

    private Long gameId;
    private Context ctx;
//    private int delay;
    private Handler handler = new Handler();


    public DelayedGameLauncher(Long gameId, Context ctx, int delay) {
//        this.delay = delay;
        this.gameId = gameId;
        this.ctx = ctx;
        handler.postDelayed(this, delay);
    }


    public void run() {
        Intent gameIntent = new Intent(ctx, GameMessages.class);
        gameIntent.putExtra(GameLocalObject.class.getName(), gameId);
        ctx.startActivity(gameIntent);
    }

//    private void launchGame() {
//
//    }
}

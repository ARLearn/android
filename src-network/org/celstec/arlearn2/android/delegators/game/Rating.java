package org.celstec.arlearn2.android.delegators.game;

import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AbstractDelegator;
import org.celstec.arlearn2.client.GameClient;
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
public class Rating extends AbstractDelegator{

    public Rating() {
        ARL.eventBus.register(this);
    }

    public void submitRating(int rating, long  gameId) {
        ARL.eventBus.post(new StarRequest(rating, gameId));
    }

    private void onEventAsync(StarRequest starRequest) {
        String token = returnTokenIfOnline();
        if (token != null) {
            GameClient.getGameClient().rateGame(token, starRequest.getGameId(), starRequest.getRating());
        }

    }

    class StarRequest{
        private int rating;
        private long gameId;

        StarRequest(int rating, long gameId) {
            this.rating = rating;
            this.gameId = gameId;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public long getGameId() {
            return gameId;
        }

        public void setGameId(long gameId) {
            this.gameId = gameId;
        }
    }
}

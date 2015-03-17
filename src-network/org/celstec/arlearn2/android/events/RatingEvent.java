package org.celstec.arlearn2.android.events;

/**
 * Created by str on 10/02/15.
 */
public class RatingEvent {

    private int rating;
    private long gameId;

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

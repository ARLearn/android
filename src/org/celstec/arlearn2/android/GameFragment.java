package org.celstec.arlearn2.android;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.GameEvent;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GameLocalObjectDao;

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
public class GameFragment extends SherlockFragment {

    private long gameId;
//    private View gameView;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    public GameFragment(Game game) {
        this.gameId = game.getGameId();
        ARL.games.syncGame(game.getGameId());
        ARL.eventBus.register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ARL.eventBus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void drawGameContent(View v) {
        GameLocalObject localObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId);
        if (localObject != null) {
            ((TextView) v.findViewById(R.id.gameTitleId)).setText(localObject.getTitle());
            ((TextView) v.findViewById(R.id.gameDescriptionId)).setText(localObject.getDescription());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View gameView = inflater.inflate(R.layout.store_game_overview, container, false);
        star1 = (ImageView) gameView.findViewById(R.id.star1);
        star2 = (ImageView) gameView.findViewById(R.id.star2);
        star3 = (ImageView) gameView.findViewById(R.id.star3);
        star4 = (ImageView) gameView.findViewById(R.id.star4);
        star5 = (ImageView) gameView.findViewById(R.id.star5);

        star1.setOnClickListener(new StarOneButton(1));
        star2.setOnClickListener(new StarOneButton(2));
        star3.setOnClickListener(new StarOneButton(3));
        star4.setOnClickListener(new StarOneButton(4));
        star5.setOnClickListener(new StarOneButton(5));
        drawGameContent(gameView);
        return gameView;
    }

    public void onEventMainThread(GameEvent event) {
        if (event.getGameId() == gameId) {
            gameId = event.getGameId();
//            drawGameContent();
        }
    }



    private class StarOneButton implements View.OnClickListener {

        private int starId;

        private StarOneButton(int starId) {
            this.starId = starId;
        }

        @Override
        public void onClick(View view) {
            Log.e("ARLearn", "Click Star");
            for (int i =0 ; i<6; i++) {
                if (i <=starId) {
                    setStar(i);
                } else {
                    unsetStar(i);
                }
            }
        }

        public void setStar(int i) {
            switch (i) {
                case 1:
                    star1.setImageResource(R.drawable.ic_star);
                    break;
                case 2:
                    star2.setImageResource(R.drawable.ic_star);
                    break;
                case 3:
                    star3.setImageResource(R.drawable.ic_star);
                    break;
                case 4:
                    star4.setImageResource(R.drawable.ic_star);
                    break;
                case 5:
                    star5.setImageResource(R.drawable.ic_star);
                    break;
            }

        }

        public void unsetStar(int i) {
            switch (i) {
                case 1:
                    star1.setImageResource(R.drawable.ic_star_grey);
                    break;
                case 2:
                    star2.setImageResource(R.drawable.ic_star_grey);
                    break;
                case 3:
                    star3.setImageResource(R.drawable.ic_star_grey);
                    break;
                case 4:
                    star4.setImageResource(R.drawable.ic_star_grey);
                    break;
                case 5:
                    star5.setImageResource(R.drawable.ic_star_grey);
                    break;
            }

        }
    }

}

package org.celstec.arlearn2.android.store;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.GameDelegator;

/**
 * Created by admin on 03/10/14.
 */
public class RateGameActivity extends Activity {
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private ImageView star4;
    private ImageView star5;

    private int selectedRating = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_game_overview_rating);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);
        star4 = (ImageView) findViewById(R.id.star4);
        star5 = (ImageView) findViewById(R.id.star5);

        star1.setOnClickListener(new StarOneButton(1));
        star2.setOnClickListener(new StarOneButton(2));
        star3.setOnClickListener(new StarOneButton(3));
        star4.setOnClickListener(new StarOneButton(4));
        star5.setOnClickListener(new StarOneButton(5));
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle conData = new Bundle();
                conData.putInt("rating", selectedRating);

                Intent intent = new Intent();
                intent.putExtras(conData);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    private class StarOneButton implements View.OnClickListener {

        private int starId;

        private StarOneButton(int starId) {
            this.starId = starId;
        }

        @Override
        public void onClick(View view) {
            selectedRating = starId;
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
                    star1.setImageResource(R.drawable.store_game_overview_rating_yellow);
                    break;
                case 2:
                    star2.setImageResource(R.drawable.store_game_overview_rating_yellow);
                    break;
                case 3:
                    star3.setImageResource(R.drawable.store_game_overview_rating_yellow);
                    break;
                case 4:
                    star4.setImageResource(R.drawable.store_game_overview_rating_yellow);
                    break;
                case 5:
                    star5.setImageResource(R.drawable.store_game_overview_rating_yellow);
                    break;
            }

        }

        public void unsetStar(int i) {
            switch (i) {
                case 1:
                    star1.setImageResource(R.drawable.store_game_overview_rating_grey);
                    break;
                case 2:
                    star2.setImageResource(R.drawable.store_game_overview_rating_grey);
                    break;
                case 3:
                    star3.setImageResource(R.drawable.store_game_overview_rating_grey);
                    break;
                case 4:
                    star4.setImageResource(R.drawable.store_game_overview_rating_grey);
                    break;
                case 5:
                    star5.setImageResource(R.drawable.store_game_overview_rating_grey);
                    break;
            }

        }
    }
}

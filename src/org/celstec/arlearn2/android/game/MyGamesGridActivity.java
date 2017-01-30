package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.internal.DaoConfig;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.beans.generalItem.Tutorial;
import org.celstec.dao.gen.*;

/**
 * Created by str on 16/06/15.
 */
public class MyGamesGridActivity  extends Activity {

    MyGamesGridImageAdapter myGamesGridImageAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ARL.init(this);
        GameLocalObject game = null;
        Long gameIdToUseForMainSplashScreen = 0l;
//        if (ARL.config.getBooleanProperty("white_label_login")) {
//            //ARL.runs.syncRunsParticipate();
//        }
        if (ARL.config.containsKey("gameIdToUseForMainSplashScreen")) {
            gameIdToUseForMainSplashScreen = Long.parseLong((String) ARL.config.get("gameIdToUseForMainSplashScreen"));
            game = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameIdToUseForMainSplashScreen);
            setTheme(GameActivityFeatures.getTheme(game.getGameBean()));

        }
        setContentView(R.layout.mygames_list_grid);

        if (ARL.config.containsKey("white_label_title") && ARL.config.getBooleanProperty("white_label")){
            ((TextView)findViewById(R.id.myGamesText)).setText(ARL.config.getProperty("white_label_title"));
        }
        if (game !=null) {

            Drawable messagesHeader = GameFileLocalObject.getDrawable(this, gameIdToUseForMainSplashScreen, "/gameMessagesHeader");
            if (messagesHeader != null) {
                ((ImageView) findViewById(R.id.gameHeader)).setImageDrawable(messagesHeader);
            }
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        myGamesGridImageAdapter = new MyGamesGridImageAdapter(this);
        gridview.setAdapter(myGamesGridImageAdapter);

        if (ARL.config.getBooleanProperty("white_label_login")) {
            //ARL.runs.syncRunsParticipate();
            for (GameLocalObject gameLocalObject: myGamesGridImageAdapter.gameLocalObjects){
                ARL.runs.syncRunsParticipate(gameLocalObject.getId());
            }
        }

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                openGame(myGamesGridImageAdapter.getItemId(position));
            }
        });

        ;
    }

    public void openGame(long itemId) {
        GameLocalObject gameLocalObject = DaoConfiguration.getInstance().getGameLocalObjectDao().load(itemId);
        if (ARL.config.getBooleanProperty("white_label_login")) {
            ARL.runs.syncRunsParticipate(gameLocalObject.getId());
        }
        boolean found = false;
        RunLocalObject runLocalObject = null;
        int amount = 0;
        for (RunLocalObject run: gameLocalObject.getRuns()) {
            if (run.getDeleted() == null || !run.getDeleted()){
                runLocalObject = run;
                amount++;
            }
        }
        if (amount > 1){
            Toast.makeText(MyGamesGridActivity.this, "opgepast: meer dan 1 run gevonden", Toast.LENGTH_LONG).show();

        }
        if (runLocalObject != null) {
            for (GeneralItemLocalObject item : gameLocalObject.getGeneralItems()) {

                if (item.getType().equals(Tutorial.class.getName())) {
                    if (!gameLocalObject.getRuns().isEmpty()) {
                        Intent gameIntent = new Intent(MyGamesGridActivity.this, TutorialActivity.class);
                        gameIntent.putExtra(GameLocalObject.class.getName(), gameLocalObject.getId());
                        gameIntent.putExtra(GeneralItemLocalObject.class.getName(), item.getId());
                        gameIntent.putExtra(RunLocalObject.class.getName(), runLocalObject.getId());
                        startActivity(gameIntent);
                        found = true;
                    }
                }
            }

            if (!found) {
                if (!gameLocalObject.getRuns().isEmpty()) {
                    Intent gameIntent = new Intent(MyGamesGridActivity.this, GameMessages.class);
                    gameIntent.putExtra(GameLocalObject.class.getName(), gameLocalObject.getId());
                    gameIntent.putExtra(RunLocalObject.class.getName(), runLocalObject.getId());
                    MyGamesGridActivity.this.startActivity(gameIntent);
                }
            }
        } else {
            Toast.makeText(MyGamesGridActivity.this, "geen run gevonden", Toast.LENGTH_LONG).show();
        }
    }




}

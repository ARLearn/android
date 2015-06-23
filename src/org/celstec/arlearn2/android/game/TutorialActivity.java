package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.ActionsDelegator;
import org.celstec.arlearn2.android.game.generalItem.ActionBarMenuController;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.game.messageViews.GameMessages;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.Tutorial;
import org.celstec.dao.gen.GameFileLocalObject;
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
public class TutorialActivity extends Activity {

    private GameActivityFeatures gameActivityFeatures;
//    GeneralItemActivityFeatures generalItemActivityFeatures;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (gameActivityFeatures != null)
            gameActivityFeatures.saveState(savedInstanceState);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARL.init(this);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY); //TODO make conditional
        gameActivityFeatures = new GameActivityFeatures(this);
        DrawableUtil drawableUtil = ARL.getDrawableUtil(gameActivityFeatures.getTheme(), this);

        setTheme(gameActivityFeatures.getTheme());
        setContentView(R.layout.game_tutorial);
        getActionBar().setBackgroundDrawable(drawableUtil.getBackgroundDarkGradient());
        Drawable messagesHeader = GameFileLocalObject.getDrawable(this, gameActivityFeatures.getGameId(), "/gameMessagesHeader");
        if (messagesHeader != null) {
            ((ImageView) findViewById(R.id.gameHeader)).setImageDrawable(messagesHeader);
        }

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            getActionBar().setHomeButtonEnabled(true);
            getActionBar().setTitle(getString(R.string.messages));
            getActionBar().setBackgroundDrawable(new ColorDrawable(DrawableUtil.styleUtil.getBackgroundDark()));
        }

        byte[] data = gameActivityFeatures.getGameLocalObject().getIcon();
        if (data != null && data.length!=0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ((ImageView)this.findViewById(R.id.generalItemIcon)).setImageBitmap(bitmap);

        }else {
            ((ImageView)this.findViewById(R.id.generalItemIcon)).setImageResource(R.drawable.ic_default_game);
        }

        Long generalItemId = getIntent().getLongExtra(GeneralItemLocalObject.class.getName(), 0l);

        NarratorItem tutorial = (NarratorItem) DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId).getGeneralItemBean();
        WebView webView = (WebView) this.findViewById(R.id.descriptionId);
        webView.setBackgroundColor(0x00000000);
        String baseUrl = "";
        if (ARL.config.getBooleanProperty("white_label") && !ARL.config.getBooleanProperty("white_label_online_sync")) {
            baseUrl = "file:///android_res/raw/";
        } else {
            baseUrl = "file://"+ MediaFolders.getIncommingFilesDir().getParent().toString()+"/";
        }
        webView.loadDataWithBaseURL(baseUrl, tutorial.getRichText(), "text/html", "UTF-8", null);


        TextView titleView = (TextView) this.findViewById(R.id.titleId);


        titleView.setText(tutorial.getName());

        ((GradientDrawable)findViewById(R.id.button).getBackground()).setColor(drawableUtil.styleUtil.getPrimaryColor());
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(TutorialActivity.this, GameMessages.class);
                gameIntent.putExtra(GameLocalObject.class.getName(), gameActivityFeatures.getGameId());
                gameIntent.putExtra(RunLocalObject.class.getName(), gameActivityFeatures.getRunId());
                TutorialActivity.this.startActivity(gameIntent);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;

    }
}

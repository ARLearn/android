package org.celstec.arlearn2.android.game.generalItem.dataCollection.impl;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.views.DrawableUtil;
import org.celstec.arlearn2.android.views.StyleUtil;
import org.celstec.dao.gen.ResponseLocalObject;

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
public class PictureResultActivity extends Activity {

    private ResponseLocalObject responseLocalObject;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil styleUtil = DrawableUtil.styleUtil;
        setTheme(styleUtil.getTheme());
        setContentView(R.layout.game_general_item_result_picture);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        responseLocalObject = DaoConfiguration.getInstance().getResponseLocalObjectDao().load(getIntent().getLongExtra(ResponseLocalObject.class.getName(), 0l));
        ((ImageView)findViewById(R.id.pictureView)).setImageURI(responseLocalObject.getUri());
    }
}

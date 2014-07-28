package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.game.GameActivityFeatures;

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
public class GeneralItemActivity extends Activity {

    GameActivityFeatures gameActivityFeatures;
    GeneralItemActivityFeatures generalItemActivityFeatures;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_general_item);
        setTheme(R.style.ARLearn_schema2);
        gameActivityFeatures = new GameActivityFeatures(this);
        generalItemActivityFeatures = new GeneralItemActivityFeatures(this);
    }
}

package org.celstec.arlearn2.android.dataCollection.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
public abstract class TextInputCollectionActivity extends Activity {

    String textValue = "";

    public abstract int getGameGeneralItemDcTextInput();
    public abstract int getDataCollectionText();
    public abstract int getDataCollectionSubmit();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getGameGeneralItemDcTextInput());
        findViewById(getDataCollectionSubmit()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = ((EditText) findViewById(getDataCollectionText())).getText().toString();
                Bundle conData = new Bundle();
                conData.putString("value", text);
                Intent intent = new Intent();
                intent.putExtras(conData);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }
}

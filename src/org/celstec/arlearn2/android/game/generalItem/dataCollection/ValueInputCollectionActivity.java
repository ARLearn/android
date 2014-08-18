package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;

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
public class ValueInputCollectionActivity extends Activity {

    String textValue = "";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_general_item_dc_number_input);
        findViewById(R.id.dataCollectionSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!textValue.equals("")) {
                    Bundle conData = new Bundle();
                    conData.putDouble("value", Double.parseDouble(textValue));
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
        findViewById(R.id.button0).setOnClickListener(new ButtonListener(0));
        findViewById(R.id.button1).setOnClickListener(new ButtonListener(1));
        findViewById(R.id.button2).setOnClickListener(new ButtonListener(2));
        findViewById(R.id.button3).setOnClickListener(new ButtonListener(3));
        findViewById(R.id.button4).setOnClickListener(new ButtonListener(4));
        findViewById(R.id.button5).setOnClickListener(new ButtonListener(5));
        findViewById(R.id.button6).setOnClickListener(new ButtonListener(6));
        findViewById(R.id.button7).setOnClickListener(new ButtonListener(7));
        findViewById(R.id.button8).setOnClickListener(new ButtonListener(8));
        findViewById(R.id.button9).setOnClickListener(new ButtonListener(9));

        findViewById(R.id.buttonDot).setOnClickListener(new ButtonListener(ButtonListener.DOT));
        findViewById(R.id.buttonBack).setOnClickListener(new ButtonListener(ButtonListener.BACK));
    }

    public class ButtonListener implements View.OnClickListener {
        public static final int BACK = -2;
        public static final int DOT = -1;
        private int value;
        public ButtonListener(int value) {
            super();
            this.value = value;
        }

        @Override
        public void onClick(View view) {
            if (value == BACK) {
                if (textValue.length()>=1) textValue = textValue.substring(0, textValue.length()-1);
            } else if (value == DOT) {
                if (!textValue.contains(".")) textValue += ".";
            } else {
                textValue += value;
            }
            ((TextView)findViewById(R.id.textView1)).setText(textValue);
        }
    }
}

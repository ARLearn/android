package org.celstec.arlearn2.android.dataCollection.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


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
public abstract class ValueInputCollectionActivity extends Activity {

    String textValue = "";
    public abstract  int getTextView();
    public abstract  int getHeaderTextView();
    public abstract  int getGameGeneralItemDcNumberInput();
    public abstract  int getDataCollectionSubmit();
    public  abstract  int getCancelButton();
    public abstract  int getButton0();
    public abstract  int getButton1();
    public abstract  int getButton2();
    public abstract  int getButton3();
    public abstract  int getButton4();
    public abstract  int getButton5();
    public abstract  int getButton6();
    public abstract  int getButton7();
    public abstract  int getButton8();
    public abstract  int getButton9();
    public abstract  int getButtonDot();
    public abstract  int getButtonBack();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getGameGeneralItemDcNumberInput());
        findViewById(getDataCollectionSubmit()).setOnClickListener(new View.OnClickListener() {
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

        findViewById(getCancelButton()).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ValueInputCollectionActivity.this.finish();
                    }
                }
        );

        findViewById(getButton0()).setOnClickListener(new ButtonListener(0));
        findViewById(getButton1()).setOnClickListener(new ButtonListener(1));
        findViewById(getButton2()).setOnClickListener(new ButtonListener(2));
        findViewById(getButton3()).setOnClickListener(new ButtonListener(3));
        findViewById(getButton4()).setOnClickListener(new ButtonListener(4));
        findViewById(getButton5()).setOnClickListener(new ButtonListener(5));
        findViewById(getButton6()).setOnClickListener(new ButtonListener(6));
        findViewById(getButton7()).setOnClickListener(new ButtonListener(7));
        findViewById(getButton8()).setOnClickListener(new ButtonListener(8));
        findViewById(getButton9()).setOnClickListener(new ButtonListener(9));

        findViewById(getButtonDot()).setOnClickListener(new ButtonListener(ButtonListener.DOT));
        findViewById(getButtonBack()).setOnClickListener(new ButtonListener(ButtonListener.BACK));
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
            ((TextView)findViewById(getTextView())).setText(textValue);
            if (textValue.trim().equals("")){
                findViewById(getDataCollectionSubmit()).setVisibility(View.GONE);
            } else{
                findViewById(getDataCollectionSubmit()).setVisibility(View.VISIBLE);
            }
        }
    }
}

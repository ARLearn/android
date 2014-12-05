package org.celstec.arlearn2.android.game.generalItem.dataCollection.impl;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.dataCollection.activities.ValueInputCollectionActivity;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.views.StyleUtil;

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
public class ValueInputCollectionActivityImpl extends ValueInputCollectionActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String message = getIntent().getStringExtra("message");
        if (message != null) ((TextView) findViewById(getHeaderTextView())).setText(message);

        GradientDrawable shapeDrawable = (GradientDrawable) ((findViewById(R.id.functionPad)).getBackground());
        shapeDrawable.setColor(ARL.getDrawableUtil(R.style.ARLearn_schema1, this).getStyleUtil().getPrimaryColor());
        ((GradientDrawable) (findViewById(getDataCollectionSubmit())).getBackground()).setColor(ARL.drawableUtil.styleUtil.getButtonAlternativeColor());


        setButtonBackground(R.id.button0);
        setButtonBackground(R.id.button1);
        setButtonBackground(R.id.button2);
        setButtonBackground(R.id.button3);
        setButtonBackground(R.id.button4);
        setButtonBackground(R.id.button5);
        setButtonBackground(R.id.button6);
        setButtonBackground(R.id.button7);
        setButtonBackground(R.id.button8);
        setButtonBackground(R.id.button9);
        setButtonBackground(R.id.buttonBack);
        setButtonBackground(R.id.buttonDot);
    }

    private void setButtonBackground(int buttonId){
        findViewById(buttonId).setBackgroundDrawable(ARL.getDrawableUtil(R.style.ARLearn_schema1, this).getPrimaryColorLightOvalWithState());
        ((Button)findViewById(buttonId)).setTextColor(ARL.getDrawableUtil(R.style.ARLearn_schema1, this).getTextColorLightWithState());
        View v = findViewById(buttonId);
        int height = ARL.drawableUtil.dipToPixels(50);
//        int height = v.getHeight();
//        v.setLayoutParams(new RelativeLayout.LayoutParams(height, height));
    }

    @Override
    public int getTextView() {
        return R.id.textView1;
    }

    @Override
    public int getGameGeneralItemDcNumberInput() {
        return R.layout.game_general_item_dc_number_input;
    }

    @Override
    public int getDataCollectionSubmit() {
        return R.id.dataCollectionSubmit;
    }

    @Override
    public int getCancelButton() {
        return R.id.cancelId;
    }

    @Override
    public int getButton0() {
        return R.id.button0;
    }

    @Override
    public int getButton1() {
        return R.id.button1;
    }

    @Override
    public int getButton2() {
        return R.id.button2;
    }

    @Override
    public int getButton3() {
        return R.id.button3;
    }

    @Override
    public int getButton4() {
        return R.id.button4;

    }

    @Override
    public int getButton5() {
        return R.id.button5;

    }

    @Override
    public int getButton6() {
        return R.id.button6;
    }

    @Override
    public int getButton7() {
        return R.id.button7;

    }

    @Override
    public int getButton8() {
        return R.id.button8;
    }

    @Override
    public int getButton9() {
        return R.id.button9;
    }

    @Override
    public int getButtonDot() {
        return R.id.buttonDot;
    }

    @Override
    public int getButtonBack() {
        return R.id.buttonBack;
    }

    public  int getHeaderTextView(){
        return R.id.text;
    }

}

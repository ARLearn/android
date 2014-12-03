package org.celstec.arlearn2.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.util.StyleUtilInterface;

import java.io.Serializable;

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
public class StyleUtil implements Serializable, StyleUtilInterface {

    private int primaryColor;
    private int primaryColorLight;

    private int buttonAlternativeColor;
    private int textLight;

    private int backgroundColor;
    private int backgroundDark;

    private int textInactive;
    private int buttonColor;

    private int primaryColorHighlight;

    private int theme;

    public StyleUtil(Context context, Integer schema) {
        this.theme = schema;
        TypedArray ta = context.obtainStyledAttributes(schema, new int[]{
                R.attr.primaryColor,
                R.attr.buttonAlternativeColor,
                R.attr.textLight,
                R.attr.primaryColorLight,
                R.attr.backgroundColor,
                R.attr.backgroundColorDark,
                R.attr.buttonColor,
                R.attr.textInactive,
                R.attr.primaryColorHighlight

        });
        primaryColor = ta.getColor(0, Color.BLACK);
        buttonAlternativeColor = ta.getColor(1, Color.BLACK);

        textLight = ta.getColor(2, Color.BLACK);
        primaryColorLight = ta.getColor(3, Color.BLACK);

        backgroundColor = ta.getColor(4, Color.BLACK);
        backgroundDark = ta.getColor(5, Color.BLACK);

        buttonColor = ta.getColor(6, Color.BLACK);
        textInactive = ta.getColor(7, Color.BLACK);

        primaryColorHighlight = ta.getColor(8, Color.BLACK);

    }

    public int getTheme() {
        return theme;
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryColorLight() {
        return primaryColorLight;
    }

     public int getButtonAlternativeColor() {
        return buttonAlternativeColor;
    }

     public int getTextLight() {
        return textLight;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getBackgroundDark() {
        return backgroundDark;
    }

    public int getTextInactive() {
        return textInactive;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public int getPrimaryColorHighlight() {
        return primaryColorHighlight;
    }
}

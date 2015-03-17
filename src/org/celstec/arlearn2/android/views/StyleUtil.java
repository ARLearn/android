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

        primaryColor = context.obtainStyledAttributes(schema, new int[]{R.attr.primaryColor}).getColor(0, Color.BLACK);
        buttonAlternativeColor = context.obtainStyledAttributes(schema, new int[]{R.attr.buttonAlternativeColor}).getColor(0, Color.BLACK);

        textLight = context.obtainStyledAttributes(schema, new int[]{R.attr.textLight}).getColor(0, Color.BLACK);
        primaryColorLight = context.obtainStyledAttributes(schema, new int[]{R.attr.primaryColorLight}).getColor(0, Color.BLACK);

        backgroundColor = context.obtainStyledAttributes(schema, new int[]{R.attr.backgroundColor}).getColor(0, Color.BLACK);
        backgroundDark = context.obtainStyledAttributes(schema, new int[]{R.attr.backgroundColorDark}).getColor(0, Color.BLACK);

        buttonColor = context.obtainStyledAttributes(schema, new int[]{R.attr.buttonColor}).getColor(0, Color.BLACK);
        textInactive = context.obtainStyledAttributes(schema, new int[]{R.attr.textInactive}).getColor(0, Color.BLACK);

        primaryColorHighlight = context.obtainStyledAttributes(schema, new int[]{R.attr.primaryColorHighlight}).getColor(0, Color.BLACK);

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

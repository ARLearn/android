package org.celstec.arlearn2.android.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.TypedValue;
import android.view.Gravity;
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
public class DrawableUtil {
    public static StyleUtil styleUtil;
    private static Context ctx;

    public static boolean isInit() {
        return styleUtil != null;
    }

    private static int[][] states = new int[][] {
            new int[] { -android.R.attr.state_pressed}, // not pressed
            new int[] { android.R.attr.state_pressed}  // pressed
    };

    public DrawableUtil(int theme, Context ctx) {

        DrawableUtil.ctx = ctx;
        styleUtil = new StyleUtil(ctx, theme);
    }

    public static Drawable getGameMessageEntry() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(styleUtil.getPrimaryColorLight()));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(styleUtil.getPrimaryColor()));
        return stateListDrawable;
    }

    public static Drawable getGameMessageEntryRead() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(styleUtil.getBackgroundColor()));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(styleUtil.getBackgroundDark()));
        return stateListDrawable;
    }

    public static ColorStateList getGameMessageTextRead() {
        int[] colors = new int[] {
                styleUtil.getTextLight(),
                styleUtil.getBackgroundColor()
        };
        return new android.content.res.ColorStateList(states, colors);
    }

    public static ColorStateList getGameMessageText() {
        int[] colors = new int[] {
                styleUtil.getTextLight(),
                styleUtil.getTextLight()
        };
        return new android.content.res.ColorStateList(states, colors);
    }

    public static Drawable getGameMessageIconBackgroundRead() {
        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.getPaint().setColor(styleUtil.getTextInactive());
        oval.setIntrinsicHeight(30);
        oval.setIntrinsicHeight(30);
        return oval;
    }

    public static Drawable getGameMessageIconBackgroundUnRead() {
        ShapeDrawable ovalPressed = new ShapeDrawable(new OvalShape());
        ovalPressed.getPaint().setColor(styleUtil.getPrimaryColorLight());
        ovalPressed.setIntrinsicHeight(30);
        ovalPressed.setIntrinsicHeight(30);

        ShapeDrawable ovalUnPressed = new ShapeDrawable(new OvalShape());
        ovalUnPressed.getPaint().setColor(styleUtil.getPrimaryColor());
        ovalUnPressed.setIntrinsicHeight(30);
        ovalUnPressed.setIntrinsicHeight(30);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  ovalUnPressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, ovalPressed);
        return stateListDrawable;
    }

    public static Drawable getPrimaryColorOvalWithState() {
        ShapeDrawable ovalPressed = new ShapeDrawable(new OvalShape());
        ovalPressed.getPaint().setColor(styleUtil.getPrimaryColorLight());

        ShapeDrawable ovalUnPressed = new ShapeDrawable(new OvalShape());
        ovalUnPressed.getPaint().setColor(styleUtil.getPrimaryColor());
        ovalUnPressed.setPadding(13,13,13,13);


        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  ovalUnPressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, ovalPressed);
        return stateListDrawable;
    }

    public static Drawable getPrimaryColorOvalSeekbar() {
        ShapeDrawable ovalUnPressed = new ShapeDrawable(new OvalShape());
        ovalUnPressed.getPaint().setColor(styleUtil.getPrimaryColor());
        ovalUnPressed.setIntrinsicWidth(dipToPixels(20));
        ovalUnPressed.setIntrinsicHeight(dipToPixels(20));
        return ovalUnPressed;
    }

    public static Drawable getButtonAlternativeColorOval() {
        ShapeDrawable ovalUnPressed = new ShapeDrawable(new OvalShape());
        ovalUnPressed.getPaint().setColor(styleUtil.getButtonAlternativeColor());
        ovalUnPressed.setPadding(13,13,13,13);
        return ovalUnPressed;
    }

    /**
     * Source of this method: http://stackoverflow.com/questions/2020882/how-to-change-progress-bars-progress-color-in-android
     * @return
     */
    public static Drawable getSeekBarProgress(){
        GradientDrawable.Orientation fgGradDirection
                = GradientDrawable.Orientation.TOP_BOTTOM;
        GradientDrawable.Orientation bgGradDirection
                = GradientDrawable.Orientation.TOP_BOTTOM;

        //Background
        GradientDrawable bgGradDrawable = new GradientDrawable(
                bgGradDirection, new int[]{styleUtil.getBackgroundColor(), styleUtil.getBackgroundColor()});
        bgGradDrawable.setShape(GradientDrawable.RECTANGLE);
        bgGradDrawable.setCornerRadius(5);
        ClipDrawable bgclip = new ClipDrawable(
                bgGradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        bgclip.setLevel(10000);

        //Progress
        GradientDrawable fg1GradDrawable = new GradientDrawable(
                fgGradDirection, new int[]{styleUtil.getPrimaryColorLight(), styleUtil.getPrimaryColorLight()});
        fg1GradDrawable.setShape(GradientDrawable.RECTANGLE);
        fg1GradDrawable.setCornerRadius(5);
        ClipDrawable fg1clip = new ClipDrawable(
                fg1GradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgclip, fg1clip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.progress);
        return  progressLayerDrawable;

    }

    public static int dipToPixels(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }
}

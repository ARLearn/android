package org.celstec.arlearn2.android.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.OvalShape;
import android.util.TypedValue;
import android.view.Gravity;

import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;

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
    public static StyleUtilInterface styleUtil;
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
//        ClassLoader cl = new URLClassLoader();

        try {

            Class cls =  Class.forName("org.celstec.arlearn2.android.views.StyleUtil");//cl.loadClass("org.celstec.arlearn2.android.views.StyleUtil");
            styleUtil = (StyleUtilInterface) cls.getDeclaredConstructor(Context.class, Integer.class).newInstance(ctx, theme);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        styleUtil = new StyleUtil(ctx, theme);
    }

    public Drawable getGameMessageEntry() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(styleUtil.getPrimaryColorLight()));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(styleUtil.getPrimaryColor()));
        return stateListDrawable;
    }

    public Drawable getGameMessageEntryRead() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(styleUtil.getBackgroundColor()));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(styleUtil.getBackgroundDark()));
        return stateListDrawable;
    }

    public Drawable getButtonAlternativeColorDrawable() {
        return new ColorDrawable(styleUtil.getButtonAlternativeColor());
//        StateListDrawable stateListDrawable = new StateListDrawable();
//        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  new ColorDrawable(styleUtil.getPrimaryColorLight()));
//        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(styleUtil.getPrimaryColor()));
//        return stateListDrawable;
    }

    public Drawable getBackgroundDarkGradient(){
//        styleUtil.getBackgroundDark()
        int color = styleUtil.getBackgroundDark();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = (color >> 0) & 0xFF;
        int alpha= 150;

        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Color.argb(alpha, r, g, b), styleUtil.getBackgroundDark()});
//        gd.setStroke(10, Color.BLUE);
        return gd;
    }

    public StyleUtilInterface getStyleUtil(){
        return styleUtil;
    }

    public static ColorStateList getTextColorLightWithState() {
        int[] colors = new int[] {
                styleUtil.getTextLight(),
                styleUtil.getPrimaryColor()
        };
        return new android.content.res.ColorStateList(states, colors);
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
//        stateListDrawable.setIntrinsicHeight(30);
//        stateListDrawable.setIntrinsicHeight(30);
//        stateListDrawable.set
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

    public static Drawable getPrimaryColorLightOvalWithState() {
        ShapeDrawable ovalPressed = new ShapeDrawable(new OvalShape());
        ovalPressed.getPaint().setColor(styleUtil.getPrimaryColorHighlight());

        ShapeDrawable ovalUnPressed = new ShapeDrawable(new OvalShape());
        ovalUnPressed.getPaint().setColor(styleUtil.getPrimaryColorLight());
        ovalUnPressed.setPadding(13,13,13,13);


        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed},  ovalUnPressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, ovalPressed);
        return stateListDrawable;
    }

    public static Drawable getPrimaryColorOval() {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(styleUtil.getPrimaryColor());

        return drawable;
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
        progressLayerDrawable.setBounds(0,0,64,0);
        return  progressLayerDrawable;

    }

    public static int dipToPixels(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, ctx.getResources().getDisplayMetrics());
    }

    public static ColorMatrixColorFilter getBlackWhiteFilter(int colorToMapOnBlack) {
        ColorMatrix cm = new ColorMatrix();

        float pr= Color.red(colorToMapOnBlack);
        float pg = Color.green(colorToMapOnBlack);
        float pb = Color.blue(colorToMapOnBlack);


        float[] blackToPrimary = new float[] {
                256f-pr, 0f, 0f, 0f, pr,
                0f, 256f-pg, 0f, 0f, pg,
                0, 0f, 256f-pb, 0f, pb,
                0f, 0f, 0f, 1f, 0f };
        cm.set(blackToPrimary);
        return new ColorMatrixColorFilter(cm);

    }

    public static int adjustAlpha(int color, float factor) {
        return Color.argb(Math.round(Color.alpha(color) * factor), Color.red(color), Color.green(color), Color.blue(color));
    }
}

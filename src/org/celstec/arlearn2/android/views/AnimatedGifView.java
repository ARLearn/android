package org.celstec.arlearn2.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.*;

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
public class AnimatedGifView extends View {

    private Movie movie;

    private long movieStart;

    public AnimatedGifView(Context context) {
        super(context);
    }

    public AnimatedGifView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedGifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initializeView(InputStream is) {
//        try {
//            is = new BufferedInputStream(new FileInputStream(new File(getCacheDir(), "mygif.gif")), 16 * 1024);
            is = new BufferedInputStream(is, 16 * 1024);

            is.mark(16 * 1024);
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        movie = Movie.decodeStream(is);
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        float scaleFactor = ((float)getWidth())/ movie.width();

        canvas.scale(scaleFactor, scaleFactor);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (movie != null) {
            int relTime = 0;
            if (movie.duration()!=0) {
                 relTime = (int) ((now - movieStart) % movie.duration());
            }
            movie.setTime(relTime);
//            movie.draw(canvas, getWidth() - movie.width(), getHeight() - movie.height());
            movie.draw(canvas, 0, 0);
            this.invalidate();
        }
    }

}

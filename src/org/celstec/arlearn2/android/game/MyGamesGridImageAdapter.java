package org.celstec.arlearn2.android.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GameLocalObjectDao;

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
public class MyGamesGridImageAdapter extends BaseAdapter {
    private Context mContext;

    GameLocalObject[] gameLocalObjects;

    public MyGamesGridImageAdapter(Context c) {
        mContext = c;
        GameLocalObjectDao dao = DaoConfiguration.getInstance().getGameLocalObjectDao();
        QueryBuilder<GameLocalObject> qb = dao.queryBuilder().orderAsc(GameLocalObjectDao.Properties.Title);
        qb.where(GameLocalObjectDao.Properties.Deleted.eq(false));
        gameLocalObjects= new GameLocalObject[(int) qb.count()];
        int i =0;
        for (GameLocalObject g: qb.list()) {
            gameLocalObjects[i++] = g;
        }

    }

    public int getCount() {
        return gameLocalObjects.length;
    }

    public GameLocalObject getItem(int position) {
        return gameLocalObjects[position];
    }

    public long getItemId(int position) {
        return getItem(position).getId();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(185, 185));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
//            imageView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View arg0, MotionEvent arg1) {
//                    switch (arg1.getAction()) {
//                        case MotionEvent.ACTION_DOWN: {
//                            imageView.setAlpha(0.5f);
//                            return true;
//
//                        }
//                        case MotionEvent.ACTION_CANCEL: {
//                            imageView.setAlpha(1.0f);
//                            return true;
//
//                        }
//                        case MotionEvent.ACTION_UP: {
//                            imageView.setAlpha(1.0f);
//                            return true;
//                        }
//                    }
//
//                    return false;
//                }
//            });
        } else {
            imageView = (ImageView) convertView;
        }

        if (getItem(position) != null) {
            byte[] data = getItem(position).getIcon();
            if (data != null && data.length!=0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                imageView.setImageBitmap(bitmap);
            }else {
                imageView.setImageResource(R.drawable.ic_default_game);
            }
        }
        return imageView;
    }



}
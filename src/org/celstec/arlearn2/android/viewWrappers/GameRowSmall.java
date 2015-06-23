package org.celstec.arlearn2.android.viewWrappers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.dao.gen.StoreGameLocalObject;

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
public class GameRowSmall {

    private LinearLayout gameRow;

    public GameRowSmall(LinearLayout layout) {
        gameRow = (LinearLayout) layout;
    }

    public GameRowSmall(LayoutInflater inflater, LinearLayout rootLayout) {
        gameRow = (LinearLayout) inflater.inflate(R.layout.store_game_list_entry_small, rootLayout, false);
        rootLayout.addView(gameRow);
    }

    public GameRowSmall(LayoutInflater layoutInflater, LinearLayout layout, StoreGameLocalObject storeGameLocalObject) {
        gameRow = (LinearLayout) layoutInflater.inflate(R.layout.store_game_list_entry_small, layout, false);
        setGameTitle(storeGameLocalObject.getTitle());
        byte[] data = storeGameLocalObject.getIcon();
        if (data != null && data.length != 0) {
            setIcon(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
    }

    public void setGameTitle(String title) {
        TextView view = (TextView) gameRow.findViewById(R.id.gameTitleId);
        view.setText(title);
    }

    public void setIcon(Bitmap icon) {
        ((ImageView) gameRow.findViewById(R.id.icon)).setImageBitmap(icon);

    }

    public View getView(){
        return gameRow;
    }

}

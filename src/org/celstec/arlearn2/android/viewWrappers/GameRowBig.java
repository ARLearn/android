package org.celstec.arlearn2.android.viewWrappers;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.dao.gen.CategoryLocalObject;

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
public abstract class GameRowBig {

    private LinearLayout gameRow;

    public GameRowBig(LinearLayout layout) {

        gameRow = (LinearLayout) layout;
    }

    public GameRowBig(LayoutInflater inflater, LinearLayout rootLayout) {
        gameRow = (LinearLayout) inflater.inflate(R.layout.store_game_list_entry_big, rootLayout, false);
        gameRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameClick();
            }
        });
        rootLayout.addView(gameRow);
    }

    public void setGameTitle(String title) {
        TextView view = (TextView) gameRow.findViewById(R.id.gameTitleId);
        view.setText(title);
    }

    public void setGameCategory(CategoryLocalObject category) {
        TextView view = (TextView) gameRow.findViewById(R.id.gameCategoryId);
        if (category == null){
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(category.getCategory());
        }


    }

    public void setGameDescription(String description) {
        TextView view = (TextView) gameRow.findViewById(R.id.gameDescriptionId);
        view.setText(description);
    }

    public void setIcon(Bitmap icon) {
        ((ImageView) gameRow.findViewById(R.id.icon)).setImageBitmap(icon);

    }

    public abstract void onGameClick();

}

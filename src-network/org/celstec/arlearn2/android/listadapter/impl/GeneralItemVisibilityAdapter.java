package org.celstec.arlearn2.android.listadapter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.listadapter.AbstractGeneralItemsVisibilityAdapter;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;

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
public class GeneralItemVisibilityAdapter extends AbstractGeneralItemsVisibilityAdapter {


    public GeneralItemVisibilityAdapter(Context context, long runId) {
        super(context, runId);
    }

    @Override
    public View newView(Context context, GeneralItemVisibilityLocalObject item, ViewGroup parent) {
        if (item == null) return null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.game_message_entry, parent, false);
    }

    @Override
    public void bindView(View view, Context context, GeneralItemVisibilityLocalObject item) {
        TextView messageText =(TextView) view.findViewById(R.id.messageText);
        if (item.getGeneralItemLocalObject()!=null) {
            messageText.setText(item.getGeneralItemLocalObject().getTitle());
        } else {
            messageText.setText("message not loaded");
        }
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && lazyList != null) {
            GeneralItemVisibilityLocalObject item = lazyList.get(position);
            if (item != null) {
                return item.getGeneralItemId();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}

package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.dao.gen.GeneralItemLocalObject;

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
public class GeneralItemActivityFeatures {

    public GeneralItemLocalObject generalItemLocalObject;
    private GeneralItemActivity activity;

    public GeneralItemActivityFeatures(final GeneralItemActivity activity) {
        this.activity = activity;
        Long generalItemId = activity.getIntent().getLongExtra(GeneralItemLocalObject.class.getName(), 0l);
        generalItemLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId);
        if (generalItemLocalObject == null) throw new NullPointerException("General Item object is null");
//
    }

    public void setMetadata(){
        TextView titleView = (TextView) this.activity.findViewById(R.id.titleId);
    }
}

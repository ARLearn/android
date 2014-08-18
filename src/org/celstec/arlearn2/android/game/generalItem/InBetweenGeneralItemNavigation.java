package org.celstec.arlearn2.android.game.generalItem;

import android.content.Intent;
import android.view.View;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
import org.celstec.arlearn2.android.listadapter.AbstractGeneralItemsVisibilityAdapter;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObject;
import org.celstec.dao.gen.GeneralItemVisibilityLocalObjectDao;

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
public class InBetweenGeneralItemNavigation {
    private GeneralItemActivity activity;
    private GameActivityFeatures gameActivityFeatures;
    private GeneralItemLocalObject previousGeneralItemLocalObject;
    private GeneralItemLocalObject nextGeneralItemLocalObject;
    private GeneralItemActivityFeatures generalItemActivityFeatures;

    public InBetweenGeneralItemNavigation(GeneralItemActivity activity, GameActivityFeatures gameActivityFeatures, GeneralItemActivityFeatures generalItemActivityFeatures) {
        this.activity = activity;
        this.gameActivityFeatures = gameActivityFeatures;
        this.generalItemActivityFeatures = generalItemActivityFeatures;
        setNextPrevButtons();
        setVisibilityQuery();
    }

    private void setVisibilityQuery() {
        QueryBuilder<GeneralItemVisibilityLocalObject> qb = AbstractGeneralItemsVisibilityAdapter.getQueryBuilder(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());

        boolean found = false;
        boolean setNext = false;
        for(GeneralItemVisibilityLocalObject vi:qb.listLazy()){
            if (setNext) {
                nextGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                setNext = false;
            } else
            if (!found) {
                if (vi.getGeneralItemLocalObject().getId().equals(generalItemActivityFeatures.generalItemLocalObject.getId())){
                    setNext = true;
                    found = true;
                } else {
                    previousGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                }
            }

        }
        if (previousGeneralItemLocalObject == null) {
            activity.findViewById(R.id.previousButton).setVisibility(View.GONE);
        }
        if (nextGeneralItemLocalObject == null) {
            activity.findViewById(R.id.nextButton).setVisibility(View.GONE);
        }
    }

    private void setNextPrevButtons(){
        activity.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                launchGeneralItemActivity(nextGeneralItemLocalObject);
            }
        });
        activity.findViewById(R.id.previousButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                launchGeneralItemActivity(previousGeneralItemLocalObject);
            }
        });
    }

    private void launchGeneralItemActivity(GeneralItemLocalObject localObject) {
        Intent intent = new Intent(activity, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent);
        intent.putExtra(GeneralItemLocalObject.class.getName(), localObject.getId());
        activity.startActivity(intent);
    }
}

package org.celstec.arlearn2.android.game.generalItem;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

    public InBetweenGeneralItemNavigation(final GeneralItemActivity activity, GameActivityFeatures gameActivityFeatures, GeneralItemActivityFeatures generalItemActivityFeatures) {
        this.activity = activity;
        this.gameActivityFeatures = gameActivityFeatures;
        this.generalItemActivityFeatures = generalItemActivityFeatures;
        setNextPrevButtons();
        setVisibilityQuery();
        GeneralItemSwipeListener swipeListener = new GeneralItemSwipeListener(activity){
            @Override
            public void onSwipeLeft() {
                navigateNext();
            }

            @Override
            public void onSwipeRight() {
                navigatePrev();
            }
        };
        activity.findViewById(R.id.generalItemActivity).setOnTouchListener(swipeListener);
        activity.findViewById(R.id.descriptionId).setOnTouchListener(swipeListener);


    }

    public void updateMessagesHeader() {
        setVisibilityQuery();
    }

    private void setVisibilityQuery() {
        QueryBuilder<GeneralItemVisibilityLocalObject> qb = AbstractGeneralItemsVisibilityAdapter.getQueryBuilder(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId(), true);

        boolean found = false;
        boolean setNext = false;
        int counter = 0;
        int total = qb.listLazy().size();
        for(GeneralItemVisibilityLocalObject vi:qb.listLazy()){
            if (setNext) {
                nextGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                setNext = false;
            } else
            if (!found) {
                counter++;
                if (vi.getGeneralItemLocalObject().getId().equals(generalItemActivityFeatures.generalItemLocalObject.getId())){
                    setNext = true;
                    found = true;
                } else {
                    previousGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                }
            }

        }
        ((TextView)(activity.findViewById(R.id.messageCounter))).setText(
                "Bericht "+ counter+ " van " + total
        );
        if (previousGeneralItemLocalObject == null) {
            (activity.findViewById(R.id.previousButton)).setBackgroundResource(R.drawable.game_general_item_previous_message_inactive);
        } else {
            (activity.findViewById(R.id.previousButton)).setBackgroundResource(R.drawable.game_general_item_previous_message_upstate);
        }
        if (nextGeneralItemLocalObject == null) {
            (activity.findViewById(R.id.nextButton)).setBackgroundResource(R.drawable.game_general_item_next_message_inactive);
        } else {
            (activity.findViewById(R.id.nextButton)).setBackgroundResource(R.drawable.game_general_item_next_message_upstate);
        }
    }

    private void setNextPrevButtons(){
        activity.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               navigateNext();
            }
        });
        activity.findViewById(R.id.previousButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               navigatePrev();
            }
        });
    }

    private void navigatePrev(){
        if (previousGeneralItemLocalObject != null) {
            activity.finish();
            launchGeneralItemActivity(previousGeneralItemLocalObject);
        }
    }

    private void navigateNext(){
        if (nextGeneralItemLocalObject != null) {
            activity.finish();
            launchGeneralItemActivity(nextGeneralItemLocalObject);
        }
    }

    private void launchGeneralItemActivity(GeneralItemLocalObject localObject) {
        Intent intent = new Intent(activity, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent);
        intent.putExtra(GeneralItemLocalObject.class.getName(), localObject.getId());
        activity.startActivity(intent);
    }
}

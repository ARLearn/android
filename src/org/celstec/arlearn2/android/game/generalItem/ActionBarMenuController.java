package org.celstec.arlearn2.android.game.generalItem;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.messageViews.GameActivityFeatures;
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
public class ActionBarMenuController {

    private Activity activity;

    private GeneralItemLocalObject previousGeneralItemLocalObject;
    private GeneralItemLocalObject nextGeneralItemLocalObject;
    private GameActivityFeatures gameActivityFeatures;
    private GeneralItemActivityFeatures generalItemActivityFeatures;

    MenuItem upItem = null;
    MenuItem downItem = null;

    public ActionBarMenuController(Activity activity, GameActivityFeatures gameActivityFeatures, GeneralItemActivityFeatures generalItemActivityFeatures) {
        this.activity = activity;
        this.gameActivityFeatures = gameActivityFeatures;
        this.generalItemActivityFeatures = generalItemActivityFeatures;

    }
    private void setVisibilityQuery() {
        QueryBuilder<GeneralItemVisibilityLocalObject> qb = AbstractGeneralItemsVisibilityAdapter.getQueryBuilder(gameActivityFeatures.getRunId(), gameActivityFeatures.getGameId());

        boolean found = false;
        boolean setNext = false;
        int counter = 0;
        int total = qb.listLazy().size();
        for (GeneralItemVisibilityLocalObject vi : qb.listLazy()) {
            if (setNext) {
                nextGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                setNext = false;
            } else if (!found) {
                counter++;
                if (vi.getGeneralItemLocalObject().getId().equals(generalItemActivityFeatures.generalItemLocalObject.getId())) {
                    setNext = true;
                    found = true;
                } else {
                    previousGeneralItemLocalObject = vi.getGeneralItemLocalObject();
                }
            }

        }
        if (previousGeneralItemLocalObject == null) {
            upItem.setIcon(R.drawable.ic_next_message_inactive);
        } else {
            upItem.setIcon(R.drawable.ic_next_message_upstate);
        }
        if (nextGeneralItemLocalObject == null) {
            downItem.setIcon(R.drawable.ic_previous_message_inactive);
        } else {
            downItem.setIcon(R.drawable.ic_previous_message_upstate);
        }
    }
    public void inflateMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.general_item_menu, menu);
        upItem = menu.findItem(R.id.action_gi_up);
        downItem = menu.findItem(R.id.action_gi_down);
        setVisibilityQuery();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_gi_up:
                navigateUp();
                return true;
            case R.id.action_gi_down:
                navigateDown();
                return true;
            case android.R.id.home:
                activity.finish();
                return true;
            default:
                return false;
        }
    }
    private void navigateUp(){

            if (previousGeneralItemLocalObject != null) {
                activity.finish();
                activity.overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

                launchGeneralItemActivity(previousGeneralItemLocalObject);
            }
    }

    public void navigateDown(){
        if (nextGeneralItemLocalObject != null) {
            activity.finish();
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            launchGeneralItemActivity(nextGeneralItemLocalObject);
        }
    }

    private void launchGeneralItemActivity(GeneralItemLocalObject localObject) {
        Intent intent = new Intent(activity, GeneralItemActivity.class);
        gameActivityFeatures.addMetadataToIntent(intent);
        intent.putExtra(GeneralItemLocalObject.class.getName(), localObject.getId());
        activity.startActivity(intent);
    }

    public void updateNavigationButtons() {
        setVisibilityQuery();
    }

    public void navigateNext() {
        navigateUp();
    }

    public boolean hasNext() {
        return previousGeneralItemLocalObject != null;
    }

}

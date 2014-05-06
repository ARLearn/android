package org.celstec.arlearn.delegators;

import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.GeneralItemDelegator;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.arlearn2.beans.generalItem.OpenQuestion;

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
public class DataCollectionTaskDelegator  extends GeneralItemDelegator {


    private DataCollectionTaskDelegator() {
    }

    public static DataCollectionTaskDelegator getInstance() {
        if (instance == null || !(instance instanceof DataCollectionTaskDelegator)) {
            instance = new DataCollectionTaskDelegator();
        }
        return (DataCollectionTaskDelegator) instance;
    }


    public void createDataCollectionTask(long gameId, String name, String description) {
        NarratorItem generalItem = new NarratorItem();
        generalItem.setGameId(gameId);
        generalItem.setName(name);
        generalItem.setDescription(description);
        generalItem.setRichText(description);
        generalItem.setOpenQuestion(new OpenQuestion());
        generalItem.getOpenQuestion().setWithPicture(true);
        generalItem.getOpenQuestion().setWithAudio(true);
        generalItem.getOpenQuestion().setWithText(true);
        generalItem.getOpenQuestion().setWithVideo(true);
        generalItem.getOpenQuestion().setWithValue(true);

        ARL.generalItems.createGeneralItem(generalItem);
    }

}

package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.dao.gen.ResponseLocalObject;

import java.util.Vector;

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
public class DataCollectionResultController {

    private GeneralItemActivity activity;
    private Vector<DataCollectionResult> results = new Vector<DataCollectionResult>();
    private LinearLayout resultsLinearLayout;
    private LazyListAdapter adapter;

    public DataCollectionResultController(GeneralItemActivity activity) {
        this.activity = activity;
        resultsLinearLayout = (LinearLayout) activity.findViewById(R.id.dataCollectionResultsLayout);
    }

    public void addResult(DataCollectionResult result) {
        View row = activity.getLayoutInflater().inflate(R.layout.game_general_item_dc_result_entry, null);
        switch (result.getType()) {
            case ResponseLocalObject.PICTURE_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_data_collection_image);
                break;
            case ResponseLocalObject.VIDEO_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_data_collection_video);
                break;
            case ResponseLocalObject.AUDIO_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_data_collection_audio);
                break;
            case ResponseLocalObject.TEXT_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_data_collection_text);
                break;
            case ResponseLocalObject.VALUE_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_data_collection_number);
                break;
        }
        if (result.getDataAsString() != null) {
            ((TextView) row.findViewById(R.id.messageText)).setText(result.getDataAsString());
        } else {
            ((TextView) row.findViewById(R.id.messageText)).setText(result.getTitle());
        }
        resultsLinearLayout.addView(row);
        results.add(result);
        result.setView(row);
    }

    public void setAdapter(LazyListAdapter adapter) {
        this.adapter = adapter;
    }

    public void notifyDataSetChanged(){
        for (ResponseLocalObject responseLocalObject: adapter.lazyList) {
            DataCollectionResult result = new DataCollectionResult(responseLocalObject.getType(), ""+responseLocalObject.getTimeStamp());
            if (responseLocalObject.getType() == ResponseLocalObject.TEXT_TYPE) {
                result.setDataAsString(responseLocalObject.getValue());
            }
            if (responseLocalObject.getType() == ResponseLocalObject.VALUE_TYPE) {
                result.setDataAsString(responseLocalObject.getValue());
            }
            addResult(result);
        }
    }

}

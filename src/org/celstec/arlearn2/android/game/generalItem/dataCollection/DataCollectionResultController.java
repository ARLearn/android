package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.AudioResultActivity;
import org.celstec.arlearn2.android.views.DrawableUtil;
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
//    private HashSet<Long> displayedIds = new HashSet<Long>();

    public DataCollectionResultController(GeneralItemActivity activity) {
        this.activity = activity;
        resultsLinearLayout = (LinearLayout) activity.findViewById(R.id.dataCollectionResultsLayout);
    }

    public void addResult(DataCollectionResult result, final ResponseLocalObject responseLocalObject) {
        final View row = activity.getLayoutInflater().inflate(R.layout.game_general_item_dc_result_entry, null);
        row.findViewById(R.id.dcTypeIcon).setBackground(DrawableUtil.getPrimaryColorOvalWithState());
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
        (row.findViewById(R.id.messageText)).setOnClickListener(createRowClickerListener(result, responseLocalObject));

        row.findViewById(R.id.trashIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (responseLocalObject.getIsSynchronized() == false) {
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(responseLocalObject);
                } else {
                    responseLocalObject.setRevoked(true);
                    responseLocalObject.setNextSynchronisationTime(0l);
                    responseLocalObject.setIsSynchronized(false);
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(responseLocalObject);
                    row.setVisibility(View.GONE);
                    ResponseDelegator.getInstance().syncResponses(responseLocalObject.getRunId());
                }
            }
        });
        resultsLinearLayout.addView(row);
        results.add(result);
        result.setView(row);
    }

    public View.OnClickListener createRowClickerListener(final DataCollectionResult result, final ResponseLocalObject responseLocalObject){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (result.getType()){
                    case ResponseLocalObject.AUDIO_TYPE:
                        Intent audioRecording = new Intent(activity, AudioResultActivity.class);
                        audioRecording.putExtra(ResponseLocalObject.class.getName(), responseLocalObject.getId());
                        activity.startActivity(audioRecording);
                        break;
                }

            }
        };
    }

    public void setAdapter(LazyListAdapter adapter) {
        this.adapter = adapter;
    }

    public void notifyDataSetChanged(){
        adapter.updateList();
        resultsLinearLayout.removeAllViews();
        for (ResponseLocalObject responseLocalObject: adapter.lazyList) {
            //if (responseLocalObject !=null && !displayedIds.contains(responseLocalObject.getId())) {
            if (responseLocalObject !=null ) {
                DataCollectionResult result = new DataCollectionResult(responseLocalObject.getType(), "" + responseLocalObject.getTimeStamp());
                if (responseLocalObject.getType() == ResponseLocalObject.TEXT_TYPE) {
                    result.setDataAsString(responseLocalObject.getValue());
                }
                if (responseLocalObject.getType() == ResponseLocalObject.VALUE_TYPE) {
                    result.setDataAsString(responseLocalObject.getValue());
                }
                addResult(result, responseLocalObject);
//                displayedIds.add(responseLocalObject.getId());
            }
        }
    }

}

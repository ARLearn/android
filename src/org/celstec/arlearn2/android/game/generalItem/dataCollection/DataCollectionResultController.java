package org.celstec.arlearn2.android.game.generalItem.dataCollection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;import org.celstec.arlearn2.android.delegators.ResponseDelegator;
import org.celstec.arlearn2.android.game.generalItem.GeneralItemActivity;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.AudioResultActivity;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.PictureResultActivity;
import org.celstec.arlearn2.android.game.generalItem.dataCollection.impl.VideoResultActivity;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.beans.generalItem.NarratorItem;
import org.celstec.dao.gen.ResponseLocalObject;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        row.findViewById(R.id.dcTypeIcon).setBackgroundDrawable(DrawableUtil.getPrimaryColorOvalWithState());
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
            case ResponseLocalObject.ANSWER_TYPE:
                ((ImageView) row.findViewById(R.id.dcTypeIcon)).setImageResource(R.drawable.game_general_item_type_mc);
                break;
        }
        if (result.getDataAsString() != null) {
//            WebView webView = (WebView) this.activity.findViewById(R.id.descriptionId);
//            webView.setBackgroundColor(0x00000000);
////        webView.loadData(((NarratorItem) generalItemBean).getRichText(), "text/html", "utf-8");
//            webView.loadDataWithBaseURL("file:///android_res/raw/", result.getDataAsString(), "text/html", "UTF-8", null);

            ((TextView) row.findViewById(R.id.messageText)).setText(result.getDataAsString());
        } else {
            if (result.getType() == ResponseLocalObject.ANSWER_TYPE) {
                ((TextView) row.findViewById(R.id.messageText)).setText(responseLocalObject.getValue());

            } else {
                ((TextView) row.findViewById(R.id.messageText)).setText(result.getTitle());
            }
        }
        (row.findViewById(R.id.messageText)).setOnClickListener(createRowClickerListener(result, responseLocalObject));
        if (responseLocalObject.getAccountLocalObject() != ARL.accounts.getLoggedInAccount() || result.getType() ==ResponseLocalObject.ANSWER_TYPE){
            row.findViewById(R.id.trashIcon).setVisibility(View.GONE);
        } else {
            (row.findViewById(R.id.trashIcon)).setOnClickListener(createDeleteRowClickListener(responseLocalObject, row));
        }

        resultsLinearLayout.addView(row);
        results.add(result);
        result.setView(row);
    }

    public View.OnClickListener createDeleteRowClickListener(final ResponseLocalObject responseLocalObject, final View row) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.removeData)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                row.setVisibility(View.GONE);
                                if (responseLocalObject.getIsSynchronized() == false) {
                                    DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(responseLocalObject);
                                } else {
                                    responseLocalObject.setRevoked(true);
                                    responseLocalObject.setNextSynchronisationTime(0l);
                                    responseLocalObject.setIsSynchronized(false);
                                    responseLocalObject.setLastModificationDate(ARL.time.getServerTime());
                                    DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(responseLocalObject);
                                    row.setVisibility(View.GONE);
                                    ResponseDelegator.getInstance().syncResponses(responseLocalObject.getRunId());
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();


            }
        };
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
                    case ResponseLocalObject.PICTURE_TYPE:
                        Intent pictureRecording = new Intent(activity, PictureResultActivity.class);
                        pictureRecording.putExtra(ResponseLocalObject.class.getName(), responseLocalObject.getId());
                        activity.startActivity(pictureRecording);
                        break;
                    case ResponseLocalObject.VIDEO_TYPE:
                        Intent videoRecording = new Intent(activity, VideoResultActivity.class);
                        videoRecording.putExtra(ResponseLocalObject.class.getName(), responseLocalObject.getId());
                        activity.startActivity(videoRecording);
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
        if (resultsLinearLayout == null) return;
        resultsLinearLayout.removeAllViews();
        for (ResponseLocalObject responseLocalObject: adapter.lazyList) {
            //if (responseLocalObject !=null && !displayedIds.contains(responseLocalObject.getId())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            if (responseLocalObject !=null ) {
                if (responseLocalObject.getType() == null) {
                    System.out.println("break here");
                } else {
                    DataCollectionResult result = new DataCollectionResult(responseLocalObject.getType(), "" + responseLocalObject.getTimeStamp()); //TODO apparently gettype can become null
                    String author  = "";
                    if (responseLocalObject.getAccountLocalObject()!=null){
                        author = activity.getString(R.string.author)+": "+responseLocalObject.getAccountLocalObject().getName() +"\n";
                    }

                    if (responseLocalObject.getType() == ResponseLocalObject.TEXT_TYPE) {
                        result.setDataAsString(author+responseLocalObject.getValue());
                    }
                    if (responseLocalObject.getType() == ResponseLocalObject.VALUE_TYPE) {
                        result.setDataAsString(author+responseLocalObject.getValue());
                    }
                    if (responseLocalObject.getType() == ResponseLocalObject.VIDEO_TYPE) {
                        String message = author+ dateFormat.format(new Date(responseLocalObject.getTimeStamp()));
                        result.setDataAsString(message);
                    }
                    if (responseLocalObject.getType() == ResponseLocalObject.AUDIO_TYPE) {
                        String message = author+ dateFormat.format(new Date(responseLocalObject.getTimeStamp()));
                        result.setDataAsString(message);
                    }
                    if (responseLocalObject.getType() == ResponseLocalObject.PICTURE_TYPE) {
                        String message = author+ dateFormat.format(new Date(responseLocalObject.getTimeStamp()));
                        result.setDataAsString(message);
                    }
                    addResult(result, responseLocalObject);
                }
//                displayedIds.add(responseLocalObject.getId());
            }
        }
    }

}

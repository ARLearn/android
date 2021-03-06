package org.celstec.arlearn2.android.delegators;

import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import org.celstec.arlearn2.android.db.Constants;
import org.celstec.arlearn2.android.events.EmailSent;
import org.celstec.arlearn2.android.events.ResponseEvent;
import org.celstec.arlearn2.android.util.AppengineFileUploader;
import org.celstec.arlearn2.android.util.FileDownloader;
import org.celstec.arlearn2.android.util.GPSUtils;
import org.celstec.arlearn2.beans.generalItem.MultipleChoiceAnswerItem;
import org.celstec.arlearn2.beans.run.ResponseList;
import org.celstec.arlearn2.client.ResponseClient;
import org.celstec.arlearn2.client.RunClient;
import org.celstec.dao.gen.*;
import org.celstec.arlearn2.beans.run.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;

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
public class ResponseDelegator extends AbstractDelegator{

    private static ResponseDelegator instance;
    private static HashMap<Long, Long> syncDates = new HashMap<Long, Long>();

    private ResponseDelegator() {
        ARL.eventBus.register(this);
    }

    public void createMultipleChoiceResponse(GeneralItemLocalObject generalItemLocalObject, long runId,  MultipleChoiceAnswerItem answerItem) {
        try {
            JSONObject responseValueJson = new JSONObject();
            responseValueJson.put("isCorrect", answerItem.getIsCorrect());
            responseValueJson.put("answer", answerItem.getAnswer());
            createResponse(generalItemLocalObject,  runId,responseValueJson.toString());
        } catch (JSONException e) {
            Log.e("exception", e.getMessage(), e);
        }

    }

    public void createSortQuestionResponse(GeneralItemLocalObject generalItemLocalObject, long runId,  String[] ids, boolean correct) {
        try {
            JSONArray array = new JSONArray();
            for (String id: ids){
                array.put(id);
            }

            JSONObject responseValueJson = new JSONObject();
            responseValueJson.put("isCorrect", correct);
            responseValueJson.put("answer", array.toString());
            createResponse(generalItemLocalObject, runId, responseValueJson.toString());
        } catch (JSONException e) {
            Log.e("exception", e.getMessage(), e);
        }

    }


    public void createResponse(GeneralItemLocalObject generalItemLocalObject, long runId, String responseValue) {
        ResponseLocalObject response = new ResponseLocalObject();
        long time = ARL.time.getServerTime();
        response.setTimeStamp(time);
        response.setLastModificationDate(time);
        response.setAccountLocalObject(ARL.accounts.getLoggedInAccount());
        response.setIsSynchronized(false);
        response.setNextSynchronisationTime(0l);
        response.setValue(responseValue);
        response.setRunId(runId);
        response.setGeneralItem(generalItemLocalObject.getId());
        response.setMultipleChoiceAnswerType();
        setLocationDetails(response);
        DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
        DaoConfiguration.getInstance().getRunLocalObjectDao().load(response.getRunId()).resetResponses();

        syncResponses(response.getRunId());

    }

    public static ResponseDelegator getInstance() {
        if (instance == null) {
            instance = new ResponseDelegator();
        }
        return instance;
    }

    private void setLocationDetails(ResponseLocalObject response) {
//        LocationManager locationManager = (LocationManager) ARL.ctx.getSystemService(ARL.ctx.LOCATION_SERVICE);
//
//        String locationProviderNetwork = LocationManager.NETWORK_PROVIDER;
//        String locationProviderGPS = LocationManager.GPS_PROVIDER;
//        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProviderGPS);
//        if (lastKnownLocation == null) {
//            lastKnownLocation =locationManager.getLastKnownLocation(locationProviderNetwork);
//        }
        Location lastKnownLocation = GPSUtils.getLastKnownLocation();
        if (lastKnownLocation != null) {
            response.setLat(lastKnownLocation.getLatitude());
            response.setLng(lastKnownLocation.getLongitude());
        }
    }

    public void syncResponses() {
        ARL.eventBus.post(new SyncResponses());
    }

    public void syncResponses(long runId) {
        ARL.eventBus.post(new SyncResponses(runId));
    }

    public void sendEmail(long runId) {
        ARL.eventBus.post(new SendMail(runId));
    }

    public void deleteResponses(Long runId){
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        dao.queryBuilder().where(ResponseLocalObjectDao.Properties.RunId.eq(runId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public synchronized void onEventAsync(SendMail sendMail) {
        try {
            String token = returnTokenIfOnline();
            if (token != null) {
                RunClient.getRunClient().sendMail(token, sendMail.getRunId());
                ARL.eventBus.post(new EmailSent(true));
            } else {
                ARL.eventBus.post(new EmailSent(false));

            }
        } catch (Exception e) {
            ARL.eventBus.post(new EmailSent(false));
        }
    }
    public synchronized void onEventAsync(SyncResponses syncResponses) {
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        long serverTime = ARL.time.getServerTime();
        Log.i(SYNC_TAG, "Syncing responses");

        QueryBuilder<ResponseLocalObject> qb = dao.queryBuilder().orderAsc(ResponseLocalObjectDao.Properties.TimeStamp);
        Query<ResponseLocalObject> notSyncedQuery = qb.where(ResponseLocalObjectDao.Properties.NextSynchronisationTime.lt(serverTime), ResponseLocalObjectDao.Properties.IsSynchronized.eq(false)).build();

        notSyncedQuery.setParameter(0, serverTime);

        for (ResponseLocalObject response : notSyncedQuery.list()) { //uploading responses
            synchronize(response);
        }

        if (syncResponses.getRunId() != null) { //downloading responses
            synchronizeResponsesWithServer(syncResponses.getRunId());
            synchronizeResponseFilesWithServer(syncResponses.getRunId());
        }
    }



    private void synchronizeWithoutFile(ResponseLocalObject response) {
        String token = returnTokenIfOnline();
        if (token != null) {
            Response responseBean = response.getBean();
            ResponseEvent re = new ResponseEvent(response.getRunId());
            if (response.getRevoked()!=null && response.getRevoked()) {
                Response responseResult =  ResponseClient.getResponseClient().deleteResponse(token, response.getId());
                if (responseResult.getError() == null) {
                    re.setDeletion(true);
                    if (responseResult.getRevoked() == null) {
                        System.out.println("test");
                    }
                    if (responseResult.getRevoked()) {
                        response.setIsSynchronized(true);
                    }
                }
            } else {
                Response responseResult = ResponseClient.getResponseClient().publishAction(token, responseBean);
                if (responseResult.getError() == null){
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(response);
                    response.setId(responseResult.getResponseId());
                    response.setIsSynchronized(true);
                }

            }
            DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);

            ARL.eventBus.post(re);
            response.getGeneralItemLocalObject().resetResponses();
        }
    }
    private void synchronize(ResponseLocalObject response) {
        if (!response.hasFile() || response.getRevoked()) {
            synchronizeWithoutFile(response);
            return;
        }
        Response responseBean = response.getBean();

        AppengineFileUploader uploader = new AppengineFileUploader(
                    response.getRunId(),
                    response.getAccountLocalObject().getFullId(),
                    response.getUri().getLastPathSegment());


        String token = returnTokenIfOnline();
        if (token != null) {
            String uploadUrl = uploader.requestUploadUrl(token) ;
            if (uploadUrl == null) {
                Integer amountOfAttempts = response.getAmountOfSynchronisationAttempts();
                if (amountOfAttempts == null) amountOfAttempts = 0;
                response.setAmountOfSynchronisationAttempts(amountOfAttempts + 1);
                response.setNextSynchronisationTime(ARL.time.getServerTime() + (amountOfAttempts * 5000));
                DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
            } else {
                Uri uri = response.getUri();
                InputStream is = null;
                try {
                    is = ARL.getContext().getContentResolver().openInputStream(uri);
//                    File fileInput = new File(uri.toString().replace("file:///storage/emulated/0/arlearn2/outgoing/","/storage/emulated/legacy/arlearn2/outgoing/"));
//                    is = ARL.getContext().getAssets().openFd(uri.toString()).createInputStream();
//                    is = new FileInputStream(fileInput);
                    if (uploader.publishData(uploadUrl, is, response.getContentType(), uri.getLastPathSegment())) {
                        response.setIsSynchronized(true);
                    } else {
                        int amountOfAttempts = response.getAmountOfSynchronisationAttempts();
                        response.setAmountOfSynchronisationAttempts(amountOfAttempts + 1);
                        response.setNextSynchronisationTime(ARL.time.getServerTime() + (amountOfAttempts * 5000));
                    }
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
                    Response responseResult = ResponseClient.getResponseClient().publishAction(token, responseBean);
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().delete(response);
                    response.setId(responseResult.getResponseId());
                    response.setIsSynchronized(true);
                    response.setThumbnailUriAsString(response.getUriAsString());
                    DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
                    ARL.eventBus.post(new ResponseEvent(response.getRunId()));
                    response.getGeneralItemLocalObject().resetResponses();

//                } catch (FileNotFoundException e) {
//                    Log.e("ARLearn", e.getMessage(), e);
                } catch (Exception e) {
                    Log.e("ARLearn", e.getMessage(), e);
                }


            }
        }

//        ResponseClient.getResponseClient().publishAction(token, response.getBean())
    }

    private void synchronizeResponsesWithServer(long runId) {
        String token = returnTokenIfOnline();
        if (token != null) {
            String resumptionToken = null;
            ResponseEvent responseEvent = null;
            do {
                ResponseList rl;

                if (DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId).getGameLocalObject().getGameBean().getConfig().getEnableExchangeResponses()){
                    rl = ResponseClient.getResponseClient().getResponses(token, runId, getLastSyncDate(runId), resumptionToken);
                } else {
                    rl = ResponseClient.getResponseClient().getResponses(token, runId, ARL.accounts.getLoggedInAccount().getFullId());
                }

                resumptionToken = rl.getResumptionToken();
                for (Response response : rl.getResponses()) {
                    ResponseLocalObject responseLocalObject = DaoConfiguration.getInstance().getResponseLocalObjectDao().loadDeep(response.getResponseId());
                    if (responseLocalObject == null) {
//                        responseLocalObject = new ResponseLocalObject(response);
                        AccountLocalObject account = ARL.accounts.createOrRetrieveAccount(Integer.parseInt(response.getUserEmail().split(":")[0]), response.getUserEmail().split(":")[1]);

                        responseLocalObject = new ResponseLocalObject(response);


                        if (account != null){
                            responseLocalObject.setAccountLocalObject(account);
                        }

                        if (responseLocalObject.getType() != null)
                                DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(responseLocalObject);
                        if (responseEvent == null) {
                            responseEvent = new ResponseEvent(runId);
                            if (responseLocalObject.getGeneralItemLocalObject() != null)
                                responseLocalObject.getGeneralItemLocalObject().resetResponses();
                        }

                    } else {
                        if (response.getLastModificationDate() == null) {
                            response.setLastModificationDate(ARL.time.getServerTime());
                        }
                        if (responseLocalObject.getLastModificationDate()== null || response.getLastModificationDate() > responseLocalObject.getLastModificationDate()) {
                            responseLocalObject.setValuesFromBean(response);
                            if (responseLocalObject.getType() != null)
                                DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(responseLocalObject);
                        }
                    }

                }
                syncDates.put(runId, rl.getServerTime());
            } while (resumptionToken!= null);

            if (responseEvent != null) {
                DaoConfiguration.getInstance().getRunLocalObjectDao().load(runId).resetResponses();
                ARL.eventBus.post(responseEvent);
            }
        }
    }

    private void synchronizeResponseFilesWithServer(Long runId) {
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        QueryBuilder<ResponseLocalObject> qb = dao.queryBuilder().orderAsc(ResponseLocalObjectDao.Properties.TimeStamp);
        Query<ResponseLocalObject> notSyncedQuery = qb.where(ResponseLocalObjectDao.Properties.UriAsString.like("http://%"), ResponseLocalObjectDao.Properties.RunId.eq(runId)).build();
        for (ResponseLocalObject response : notSyncedQuery.list()) {
            File targetFile = urlToCacheFile(runId, response.getId(), response.getUri().getLastPathSegment());
            Uri newUri = Uri.fromFile(targetFile);
            try {
                FileDownloader fd = new FileDownloader(response.getUriAsString(), targetFile);
                fd.download();
                boolean insert = false;
                if (fd.getTargetLocation().exists()) {
                    response.setUriAsString(newUri.toString());
                    insert = true;
                }
                if (response.getThumbnailUriAsString() != null && !"".equals(response.getThumbnailUriAsString())) {
                    targetFile = urlToCacheFile(runId, response.getId(), "thumb_"+response.getThumbnailUri().getLastPathSegment());
                    newUri = Uri.fromFile(targetFile);
                    fd = new FileDownloader(response.getThumbnailUriAsString(), targetFile);
                    fd.download();
                    if (fd.getTargetLocation().exists()) {
                        response.setThumbnailUriAsString(newUri.toString());
                    }
                }
                if (insert) DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
            } catch (MalformedURLException e) {
                Log.e("ARLearn", e.getMessage(), e);
            } catch (FileNotFoundException e) {
                Log.e("ARLearn", e.getMessage(), e);
            }
        }
    }

    private long getLastSyncDate(long runId) {
        if (syncDates.containsKey(runId)) {
            return syncDates.get(runId);
        }
        return 0l;
    }



    private class SendMail{

        private long runId;
        public SendMail(long runId) {
            this.runId = runId;
        }

        public long getRunId() {
            return runId;
        }

        public void setRunId(long runId) {
            this.runId = runId;
        }
    }

    private class SyncResponses {
        private Long runId ;

        private SyncResponses(Long runId) {
            this.runId = runId;
        }

        private SyncResponses() {
        }

        public Long getRunId() {
            return runId;
        }

        public void setRunId(Long runId) {
            this.runId = runId;
        }
    }

    private File urlToCacheFile(long runId, long responseId, String fileName) {
        return new File(getCacheDir2(runId), responseId+"-"+fileName);

    }
    private File getCacheDir2(long runId) {
        File sdcard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        File cacheDirFile = new File(sdcard, Constants.CACHE_DIR);
        if (!cacheDirFile.exists())
            cacheDirFile.mkdir();
        File incommingDir = new File(cacheDirFile, Constants.INCOMMING+"_runs");
        if (!incommingDir.exists())
            incommingDir.mkdir();

        File runDir = new File(incommingDir, "" + runId);
        if (!runDir.exists())
            runDir.mkdir();

        return runDir;
    }

    public long getAmountOfResponses(long runId){
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        QueryBuilder<ResponseLocalObject> qb =dao.queryBuilder();
        qb.where(ResponseLocalObjectDao.Properties.RunId.eq(runId));
        return qb.count();
//        qb.where(new WhereCondition.StringCondition("status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)"));
    }

    public long getAmountOfSyncedResponses(long runId){
        ResponseLocalObjectDao dao = DaoConfiguration.getInstance().getResponseLocalObjectDao();
        QueryBuilder<ResponseLocalObject> qb =dao.queryBuilder();
        qb.where(
                qb.and(
                        ResponseLocalObjectDao.Properties.RunId.eq(runId),
                        ResponseLocalObjectDao.Properties.IsSynchronized.eq(true)
                )

        );
        return qb.count();
//        qb.where(new WhereCondition.StringCondition("status = 1 and run_id = "+runId+" and general_item_id in (select _id from general_item_local_object where game_id = "+gameId+" and deleted = 0)"));
    }
}

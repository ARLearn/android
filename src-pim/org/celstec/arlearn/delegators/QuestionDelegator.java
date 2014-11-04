package org.celstec.arlearn.delegators;

import android.util.Log;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AbstractDelegator;
import org.celstec.arlearn2.client.InquiryClient;
import org.celstec.dao.gen.InquiryLocalObject;
import org.celstec.dao.gen.InquiryQuestionLocalObject;
import org.celstec.events.QuestionEvent;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
public class QuestionDelegator extends AbstractDelegator {

    private static QuestionDelegator instance;
    private QuestionDelegator loggedInAccount;

    private QuestionDelegator() {
        ARL.eventBus.register(this);
    }

    public static QuestionDelegator getInstance() {
        if (instance == null) {
            instance = new QuestionDelegator();
        }
        return instance;
    }

    public void syncQuestions(InquiryLocalObject inquiryLocalObject) {
        ARL.eventBus.post(new SyncQuestionsTask(inquiryLocalObject));

    }

    private void onEventAsync(SyncQuestionsTask sge) {
        Log.i(SYNC_TAG, "Syncing questions for inquiry "+sge.inquiryLocalObject.getTitle()+ " "+sge.inquiryLocalObject.getId());
        String token =returnTokenIfOnline();
        if (token != null) {
            String questions = InquiryClient.getInquiryClient().questions(token, sge.getInquiryLocalObject().getId());
            if (questions == null) return;
            JSONObject json = null;
            QuestionEvent event = null;
            try {
                json = new JSONObject(questions);
                JSONArray array = json.getJSONArray("result");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject inqJsonObject = array.getJSONObject(i);
                    String question = inqJsonObject.getString("question");
                    Log.i(SYNC_TAG, "Question found " + question);
                    InquiryQuestionLocalObject inquiryQuestionLocalObject = new InquiryQuestionLocalObject();
                    inquiryQuestionLocalObject.setDescription(inqJsonObject.getString("description"));
                    inquiryQuestionLocalObject.setTitle(inqJsonObject.getString("question"));
                    inquiryQuestionLocalObject.setIdentifier(inqJsonObject.getString("url"));
                    DaoConfiguration.getInstance().getInquiryQuestionLocalObjectDao().insertOrReplace(inquiryQuestionLocalObject);
                    event = new QuestionEvent();
                    event.setInquiryId(sge.getInquiryLocalObject().getId());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (event != null) ARL.eventBus.post(event);
        }
    }

    public class SyncQuestionsTask {
        InquiryLocalObject inquiryLocalObject;
        public SyncQuestionsTask(InquiryLocalObject inquiryLocalObject) {
            this.inquiryLocalObject = inquiryLocalObject;
        }

        public InquiryLocalObject getInquiryLocalObject() {
            return inquiryLocalObject;
        }

        public void setInquiryLocalObject(InquiryLocalObject inquiryLocalObject) {
            this.inquiryLocalObject = inquiryLocalObject;
        }
    }
}

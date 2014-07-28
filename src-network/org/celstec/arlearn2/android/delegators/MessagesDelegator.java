package org.celstec.arlearn2.android.delegators;

import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.events.MessageEvent;
import org.celstec.arlearn2.beans.run.Message;
import org.celstec.arlearn2.beans.run.MessageList;
import org.celstec.arlearn2.client.ThreadsClient;
import org.celstec.dao.gen.MessageLocalObject;
import org.celstec.dao.gen.MessageLocalObjectDao;

import java.util.List;

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
public class MessagesDelegator extends AbstractDelegator {

    private static MessagesDelegator instance;


    private MessagesDelegator() {
        ARL.eventBus.register(this);
    }

    public static MessagesDelegator getInstance() {
        if (instance == null) {
            instance = new MessagesDelegator();
        }
        return instance;
    }
    /*
        Public API
         */


    public void syncMessages(Long threadId) {
        ARL.eventBus.post(new SyncMessages(threadId, null));
    }

    public void syncMessagesForDefaultThread(Long runId) {
        ARL.eventBus.post(new SyncMessages(null, runId));
    }

    public void postMessagesToServer() {
        ARL.eventBus.post(new PostMessagesToServer());
    }

    /*
    Implementation
    */

    private void onEventAsync(SyncMessages syncMessages) {
        String token = returnTokenIfOnline();
        if (token != null) {

            MessageList ml = null;
            if (syncMessages.getThreadId() != null){
                ThreadsClient.getThreadsClient().getMessages(token, syncMessages.getThreadId());
            }
            if (syncMessages.getRunId() != null){
                ThreadsClient.getThreadsClient().getDefaultThreadMessages(token, syncMessages.getRunId());
            }
            if (ml.getError() ==null) {
                process(ml);
            }
        }

    }

    private void process(MessageList ml) {
        for (Message message: ml.getMessages()) {
            MessageLocalObject existingMessage = DaoConfiguration.getInstance().getMessageLocalObject().load(message.getMessageId());
            MessageLocalObject newMessage = toDaoLocalObject(message);
            if (existingMessage == null) {
                DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(newMessage);
                ARL.eventBus.post(new MessageEvent(newMessage.getRunId(), newMessage.getThreadId()));
            }
        }
    }

    private MessageLocalObject toDaoLocalObject(Message message) {
        MessageLocalObject messageLocalObject = new MessageLocalObject();
        messageLocalObject.setId(message.getMessageId());
        messageLocalObject.setBody(message.getBody());
        messageLocalObject.setRunId(message.getRunId());
        messageLocalObject.setSubject(message.getSubject());
        messageLocalObject.setThreadId(message.getThreadId());
        messageLocalObject.setTime(message.getTimestamp());

        return messageLocalObject;
    }



    private void onEventAsync(PostMessagesToServer syncMessages) {
        String token = returnTokenIfOnline();
        if (token != null) {

            List<MessageLocalObject> list= DaoConfiguration.getInstance().getMessageLocalObject().queryBuilder()
                    .where(MessageLocalObjectDao.Properties.Synced.eq(false))
                    .list();
            for (MessageLocalObject messageLocalObject: list) {
                Message returnMessage = ThreadsClient.getThreadsClient().createMessage(token, messageLocalObject.getBean(false));
                DaoConfiguration.getInstance().getMessageLocalObject().delete(messageLocalObject);

                messageLocalObject.setId(returnMessage.getMessageId());
                messageLocalObject.setSynced(true);
                DaoConfiguration.getInstance().getMessageLocalObject().insertOrReplace(messageLocalObject);
                System.out.println(returnMessage);
            }
        }

    }


    private class SyncMessages{
        Long threadId;
        Long runId;

        private SyncMessages(Long threadId, Long runId) {
            this.threadId = threadId;
            this.runId = runId;
        }

        public Long getThreadId() {
            return threadId;
        }

        public void setThreadId(long threadId) {
            this.threadId = threadId;
        }

        public Long getRunId() {
            return runId;
        }

        public void setRunId(Long runId) {
            this.runId = runId;
        }
    }

    private class PostMessagesToServer{

        private PostMessagesToServer() {

        }

    }
}

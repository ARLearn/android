package org.celstec.arlearn.delegators;

import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AbstractDelegator;
import org.celstec.arlearn2.client.FriendsClient;
import org.celstec.arlearn2.client.InquiryClient;
import org.celstec.dao.gen.FriendsLocalObject;
import org.celstec.dao.gen.FriendsLocalObjectDao;
import org.celstec.events.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
public class FriendsDelegator extends AbstractDelegator {

    private static FriendsDelegator instance;

    private FriendsDelegator () {
        ARL.eventBus.register(this);
    }

    public static FriendsDelegator getInstance() {
        if (instance == null) {
            instance = new FriendsDelegator();
        }
        return instance;
    }


    public void syncFriends(){
        if (INQ.accounts.getLoggedInAccount() == null) {
            return;
        }
        String myUserId = INQ.accounts.getLoggedInAccount().getLocalId();
        int myProviderId = INQ.accounts.getLoggedInAccount().getAccountType();
        ARL.eventBus.post(new SyncFriends(myProviderId, myUserId));

    }

    public void inviteFriend(int myProviderId, String myUserId, int friendProviderId, String friendUserId) {
        ARL.eventBus.post(new InviteFriend(myProviderId,myUserId,friendProviderId, friendUserId));
    }

    public void removeFriend(int myProviderId, String myUserId, int friendProviderId, String friendUserId) {
        ARL.eventBus.post(new RemoveFriend(myProviderId,myUserId,friendProviderId, friendUserId));

    }

    public void receivedFriendRequests(int myProviderId, String myUserId) {
        ARL.eventBus.post(new ReceivedFriendRequests(myProviderId,myUserId));

    }

    public void sentFriendRequests(int myProviderId, String myUserId) {
        ARL.eventBus.post(new SentFriendRequests(myProviderId,myUserId));

    }

    public void siteUsersRequests() {
        ARL.eventBus.post(new AllSiteUsers());

    }

    private void onEventAsync(SyncFriends syncFriends){
        String token = returnTokenIfOnline();
        if (token != null) {
            JSONObject json = FriendsClient.getFriendsClient().syncFriends(token, syncFriends.getProviderId(), syncFriends.getUserId());

            if (json.has("result")) {
                JSONArray array = null;
                List<FriendsLocalObject> allFriends = DaoConfiguration.getInstance().getFriendsLocalObjectDao().loadAll();
                try {
                    array = json.getJSONArray("result");
                    FriendEvent event = null;
                    for (int i = 0; i <array.length(); i++){
                        JSONObject user = array.getJSONObject(i);
                        String oauthId = user.getString("oauthId");
                        String oauthProvider = user.getString("oauthProvider");
                        String name = user.getString("name");
                        String icon = user.getString("icon");
                        String fullAccountId = InquiryClient.getProviderIdAsInt(oauthProvider)+":"+oauthId;
                        boolean found = false;
                        for (FriendsLocalObject existingFriend: allFriends) {
                            if (existingFriend.getAccountIdAsString().equals(fullAccountId)){
                                existingFriend.setDirty(true);
                                if (!name.equals(existingFriend.getName())) {
                                    existingFriend.setName(name);
                                    existingFriend.setWasChanged(true);

                                }
                                found = true;
                            }

                        }
                        if (!found) {
                            FriendsLocalObject newFriend = new FriendsLocalObject();
                            newFriend.setName(name);
                            newFriend.setAccountIdAsString(fullAccountId);
                            DaoConfiguration.getInstance().getFriendsLocalObjectDao().insertOrReplace(newFriend);
                            event = new FriendEvent(newFriend.getAccountIdAsString());
                        }
                    }

                    for (FriendsLocalObject existingFriend: allFriends) {
                        if (existingFriend.isDirty()) {
                            if (existingFriend.isWasChanged()) {
                                DaoConfiguration.getInstance().getFriendsLocalObjectDao().insertOrReplace(existingFriend);
                                event = new FriendEvent(existingFriend.getAccountIdAsString());
                            }
                        } else {
                            event = new FriendEvent(existingFriend.getAccountIdAsString());
                            DaoConfiguration.getInstance().getFriendsLocalObjectDao().delete(existingFriend);
                        }
                    }
                    if (event != null) ARL.eventBus.post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            ARL.eventBus.post(result);
        }
    }

    private void onEventAsync(InviteFriend invite){
        String token = returnTokenIfOnline();
        if (token != null) {
            FriendInviteResultEvent result = FriendsClient.getFriendsClient().addFriend(token, invite.getMyProviderId(), invite.getMyUserId(),invite.getFriendProviderId(), invite.getFriendUserId());
            ARL.eventBus.post(result);
        }
    }

    private void onEventAsync(RemoveFriend invite){
        String token = returnTokenIfOnline();
        if (token != null) {
            FriendRemoveResultEvent result = FriendsClient.getFriendsClient().removeFriend(token, invite.getMyProviderId(), invite.getMyUserId(), invite.getFriendProviderId(), invite.getFriendUserId());
            ARL.eventBus.post(result);
        }
    }

    private void onEventAsync(ReceivedFriendRequests invite){
        String token = returnTokenIfOnline();
        if (token != null) {
            ReceivedFriendEvent result = FriendsClient.getFriendsClient().receivedFriendRequests(token, invite.getMyProviderId(), invite.getMyUserId());
            ARL.eventBus.post(result);
        }
    }

    private void onEventAsync(SentFriendRequests invite){
        String token = returnTokenIfOnline();
        if (token != null) {
            SentFriendRequestsEvent result = FriendsClient.getFriendsClient().sentFriendRequests(token, invite.getMyProviderId(), invite.getMyUserId());
            ARL.eventBus.post(result);
        }
    }

    private void onEventAsync(AllSiteUsers invite){
        String token = returnTokenIfOnline();
        if (token != null) {
            ElggUsersEvent result = FriendsClient.getFriendsClient().siteUsers(token);
            ARL.eventBus.post(result);
        }
    }

    class InviteFriend {
        private int myProviderId;
        private String myUserId;
        private int friendProviderId;
        private String friendUserId;


        InviteFriend(int myProviderId, String myUserId, int friendProviderId, String friendUserId) {
            this.myProviderId = myProviderId;
            this.myUserId = myUserId;
            this.friendProviderId = friendProviderId;
            this.friendUserId = friendUserId;
        }

        public int getMyProviderId() {
            return myProviderId;
        }

        public void setMyProviderId(int myProviderId) {
            this.myProviderId = myProviderId;
        }

        public String getMyUserId() {
            return myUserId;
        }

        public void setMyUserId(String myUserId) {
            this.myUserId = myUserId;
        }

        public int getFriendProviderId() {
            return friendProviderId;
        }

        public void setFriendProviderId(int friendProviderId) {
            this.friendProviderId = friendProviderId;
        }

        public String getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(String friendUserId) {
            this.friendUserId = friendUserId;
        }
    }


    class RemoveFriend {
        private int myProviderId;
        private String myUserId;
        private int friendProviderId;
        private String friendUserId;


        RemoveFriend(int myProviderId, String myUserId, int friendProviderId, String friendUserId) {
            this.myProviderId = myProviderId;
            this.myUserId = myUserId;
            this.friendProviderId = friendProviderId;
            this.friendUserId = friendUserId;
        }

        public int getMyProviderId() {
            return myProviderId;
        }

        public void setMyProviderId(int myProviderId) {
            this.myProviderId = myProviderId;
        }

        public String getMyUserId() {
            return myUserId;
        }

        public void setMyUserId(String myUserId) {
            this.myUserId = myUserId;
        }

        public int getFriendProviderId() {
            return friendProviderId;
        }

        public void setFriendProviderId(int friendProviderId) {
            this.friendProviderId = friendProviderId;
        }

        public String getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(String friendUserId) {
            this.friendUserId = friendUserId;
        }
    }

    class ReceivedFriendRequests {
        private int myProviderId;
        private String myUserId;


        ReceivedFriendRequests(int myProviderId, String myUserId) {
            this.myProviderId = myProviderId;
            this.myUserId = myUserId;
        }

        public int getMyProviderId() {
            return myProviderId;
        }

        public void setMyProviderId(int myProviderId) {
            this.myProviderId = myProviderId;
        }

        public String getMyUserId() {
            return myUserId;
        }

        public void setMyUserId(String myUserId) {
            this.myUserId = myUserId;
        }

    }

    class SentFriendRequests {
        private int myProviderId;
        private String myUserId;


        SentFriendRequests(int myProviderId, String myUserId) {
            this.myProviderId = myProviderId;
            this.myUserId = myUserId;
        }

        public int getMyProviderId() {
            return myProviderId;
        }

        public void setMyProviderId(int myProviderId) {
            this.myProviderId = myProviderId;
        }

        public String getMyUserId() {
            return myUserId;
        }

        public void setMyUserId(String myUserId) {
            this.myUserId = myUserId;
        }

    }

    class SyncFriends {
        private int providerId;
        private String userId;

        SyncFriends(int providerId, String userId) {
            this.providerId = providerId;
            this.userId = userId;
        }

        public int getProviderId() {
            return providerId;
        }

        public void setProviderId(int providerId) {
            this.providerId = providerId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    class AllSiteUsers {

    }
}

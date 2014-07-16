package org.celstec.arlearn.delegators;

import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AbstractDelegator;
import org.celstec.arlearn2.client.FriendsClient;
import org.celstec.events.FriendInviteResultEvent;
import org.celstec.events.FriendRemoveResultEvent;
import org.celstec.events.ReceivedFriendEvent;
import org.celstec.events.SentFriendRequestsEvent;

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
}

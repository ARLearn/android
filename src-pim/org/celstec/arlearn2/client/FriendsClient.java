package org.celstec.arlearn2.client;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.client.exception.ARLearnException;
import org.celstec.events.*;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;

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
public class FriendsClient extends InquiryClient{

    public static FriendsClient instance;

    private FriendsClient() {
        super();
    }

    public static FriendsClient getFriendsClient() {
        if (instance == null) {
            instance = new FriendsClient();
        }
        return instance;
    }

    public FriendInviteResultEvent addFriend(String token, int myProviderId, String myUserId, int friendProviderId, String friendUserId){
        if (INQ.accounts.getLoggedInAccount() == null) {
            return null;
        }
        String url = getUrlPrefix();
        String postBody = "method=add.friend&" +
                "provider=" +providerIdToElggName(myProviderId) +
                "&user_uid=" + myUserId +
                "&friend_provider=" + providerIdToElggName(friendProviderId) +
                "&friend_user_uid=" + friendUserId +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

                HttpResponse response = conn.executePOST(url, token, "application/json", postBody, "application/x-www-form-urlencoded");
        try {
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.has("message")){
                return new FriendInviteResultEvent(json.getInt("status"), json.getString("message"));
            }
            return new FriendInviteResultEvent(json.getInt("status"), json.getString("result"));
//            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }
    }


    public FriendRemoveResultEvent removeFriend(String token, int myProviderId, String myUserId, int friendProviderId, String friendUserId){
        if (INQ.accounts.getLoggedInAccount() == null) {
            return null;
        }
        String url = getUrlPrefix();
        String postBody = "method=remove.friend&" +
                "provider=" +providerIdToElggName(myProviderId) +
                "&user_uid=" + myUserId +
                "&friend_provider=" + providerIdToElggName(friendProviderId) +
                "&friend_user_uid=" + friendUserId +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

        HttpResponse response = conn.executePOST(url, token, "application/json", postBody, "application/x-www-form-urlencoded");
        try {
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            if (json.has("message")) {
                return new FriendRemoveResultEvent(json.getInt("status"), json.getString("message"));
            }
            return new FriendRemoveResultEvent(json.getInt("status"), json.getString("result"));
//            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }
    }

    public ReceivedFriendEvent receivedFriendRequests(String token, int myProviderId, String myUserId){
        if (INQ.accounts.getLoggedInAccount() == null) {
            return null;
        }
        String url = getUrlPrefix()+ "method=received.friendrequests&" +
                "provider=" +providerIdToElggName(myProviderId) +
                "&user_uid=" + myUserId +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

        HttpResponse response = conn.executeGET(url, token, "application/json");
        try {
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            ReceivedFriendEvent result =new ReceivedFriendEvent(json.getInt("status"));
            if (json.has("result")) {
                JSONArray array = json.getJSONArray("result");
                for (int i = 0; i <array.length(); i++){
                    JSONObject user = array.getJSONObject(i);
                    String oauthId = user.getString("oauthId");
                    String oauthProvider = user.getString("oauthProvider");
                    String name = user.getString("name");
                    String icon = user.getString("icon");
                    result.addEntry(oauthId, oauthProvider, name, icon);
                }
            }
            return result;
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }
    }

    public SentFriendRequestsEvent sentFriendRequests(String token, int myProviderId, String myUserId){
        if (INQ.accounts.getLoggedInAccount() == null) {
            return null;
        }
        String url = getUrlPrefix() + "method=sent.friendrequests&" +
                "provider=" +providerIdToElggName(myProviderId) +
                "&user_uid=" + myUserId +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

        HttpResponse response = conn.executeGET(url, token, "application/json");
        try {
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            SentFriendRequestsEvent result = new SentFriendRequestsEvent(json.getInt("status"));
            if (json.has("result")) {
                JSONArray array = json.getJSONArray("result");
                for (int i = 0; i <array.length(); i++){
                    JSONObject user = array.getJSONObject(i);
                    String oauthId = user.getString("oauthId");
                    String oauthProvider = user.getString("oauthProvider");
                    String name = user.getString("name");
                    String icon = user.getString("icon");
                    result.addEntry(oauthId, oauthProvider, name, icon);
                }
            }
            return result;
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }
    }

    public JSONObject syncFriends(String token, int providerId, String userId) {
        if (INQ.accounts.getLoggedInAccount() == null) {
            return null;
        }
        String url = getUrlPrefix() + "method=user.friends&" +
                "oauthProvider=" +providerIdToElggName(providerId) +
                "&oauthId=" + userId +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

        HttpResponse response = conn.executeGET(url, token, "application/json");
        try {
            return new JSONObject(EntityUtils.toString(response.getEntity()));

        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }

    }

    public ElggUsersEvent siteUsers(String token) {

        String url = getUrlPrefix() + "method=site.users&" +
                "&api_key="+INQ.config.getProperty("elgg_api_key") ;

        HttpResponse response = conn.executeGET(url, token, "application/json");
        try {
            JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
            ElggUsersEvent result = new ElggUsersEvent(json.getInt("status"));
            if (json.has("result")) {
                JSONArray array = json.getJSONArray("result");
                for (int i = 0; i <array.length(); i++){
                    JSONObject user = array.getJSONObject(i);
                    String oauthId = user.getString("oauthId");
                    String oauthProvider = user.getString("oauthProvider");
                    String name = user.getString("name");
                    String icon = user.getString("icon");
                    result.addEntry(oauthId, oauthProvider, name, icon);
                }
            }
            return result;
        } catch (Exception e) {
            if (e instanceof ARLearnException) throw (ARLearnException) e;
            return null;
        }
    }
}

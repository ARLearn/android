package org.celstec.events;

import java.util.Iterator;
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
public class ReceivedFriendEvent {

    private int resultCode;
    private Vector<Friend> friends = new Vector<Friend>();

    public ReceivedFriendEvent(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void addEntry(String oauthId, String oauthProvider, String name, String icon) {
        friends.add(new Friend(oauthId, oauthProvider, name, icon));
    }

    public int size() {
        return friends.size();
    }

    public Iterator<Friend> getIterator() {
        return friends.iterator();
    }

    public Friend getFriend(int index) {
        return friends.get(index);
    }

    class Friend {
        private String oauthId;
        private String oauthProvider;
        private String name;
        private String icon;

        Friend(String oauthId, String oauthProvider, String name, String icon) {
            this.oauthId = oauthId;
            this.oauthProvider = oauthProvider;
            this.name = name;
            this.icon = icon;
        }

        public String getOauthId() {
            return oauthId;
        }

        public void setOauthId(String oauthId) {
            this.oauthId = oauthId;
        }

        public String getOauthProvider() {
            return oauthProvider;
        }

        public void setOauthProvider(String oauthProvider) {
            this.oauthProvider = oauthProvider;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

}

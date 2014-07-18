package org.celstec.events;

import org.celstec.events.objects.User;

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
public class ElggUsersEvent {

    private int resultCode;
    private Vector<User> friends = new Vector<User>();

    public ElggUsersEvent(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void addEntry(String oauthId, String oauthProvider, String name, String icon) {
        friends.add(new User(oauthId, oauthProvider, name, icon));
    }

    public int size() {
        return friends.size();
    }

    public Iterator<User> getIterator() {
        return friends.iterator();
    }

    public User getFriend(int index) {
        return friends.get(index);
    }
}

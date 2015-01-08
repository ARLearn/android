package org.celstec.arlearn2.android.gcm.handlers;

import android.content.Context;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.GeneralItemDelegator;
import org.celstec.arlearn2.android.gcm.NotificationListenerInterface;
import org.celstec.arlearn2.beans.notification.MessageNotification;
import org.celstec.arlearn2.beans.run.Message;

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
public class GCMMessageHandler  implements NotificationListenerInterface {

    @Override
    public boolean acceptNotificationType(String notificationType) {
        return MessageNotification.class.getName().equals(notificationType);
    }

    @Override
    public void handleNotification(HashMap<String, String> map) {
        ARL.messages.syncMessages(Long.parseLong(map.get("threadId")));

    }
}

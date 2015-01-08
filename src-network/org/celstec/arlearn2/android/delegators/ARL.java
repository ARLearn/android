package org.celstec.arlearn2.android.delegators;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import daoBase.DaoConfiguration;
import de.greenrobot.event.EventBus;
import org.celstec.arlearn2.android.db.ConfigAdapter;
import org.celstec.arlearn2.android.db.PropertiesAdapter;
import org.celstec.arlearn2.android.gcm.GCMRegisterTask;
//import org.celstec.arlearn2.android.views.StyleUtil;
import org.celstec.arlearn2.android.gcm.NotificationListenerInterface;
import org.celstec.arlearn2.android.util.DrawableUtil;
import org.celstec.arlearn2.client.GenericClient;

import java.util.Arrays;
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
public class ARL {

    public static GameDelegator games;
    public static GeneralItemDelegator generalItems;
    public static GeneralItemVisibilityDelegator generalItemVisibility;
    public static GiFileReferenceDelegator fileReferences;
    public static RunDelegator runs;
    public static AccountDelegator accounts;
    public static ResponseDelegator responses;
    public static ActionsDelegator actions;
    public static PropertiesAdapter properties;
    public static ConfigAdapter.PropertiesExt config;
    public static TimeDelegator time;
    public static StoreDelegator store;
    public static ThreadsDelegator threads;
    public static MessagesDelegator messages;
    public static MapContext mapContext;
    public static EventBus eventBus = new EventBus();
    public static DaoConfiguration dao;
    public static Context ctx;
    public static NotificationListenerInterface[] notificationListenerInterfaces;



    public static DrawableUtil drawableUtil;

    public static void init(Context ctx) {
        if (!ARL.isInit()) {
            System.out.println("Im here");
            ARL.ctx = ctx;
            dao = DaoConfiguration.getInstance(ctx);
            mapContext = new MapContext(ctx);
            properties = PropertiesAdapter.getInstance(ctx);
            config = new ConfigAdapter(ctx).getProperties();
            GenericClient.urlPrefix = config.getProperty("arlearn_server");
            games = GameDelegator.getInstance();
            generalItems = GeneralItemDelegator.getInstance();
            runs = RunDelegator.getInstance();
            accounts = AccountDelegator.getInstance();
            time = TimeDelegator.getInstance(ctx);
            fileReferences = GiFileReferenceDelegator.getInstance();
            actions = ActionsDelegator.getInstance();
            responses = ResponseDelegator.getInstance();
            store = StoreDelegator.getInstance();
            threads = ThreadsDelegator.getInstance();
            messages = MessagesDelegator.getInstance();
            generalItemVisibility = GeneralItemVisibilityDelegator.getInstance();
            initNotificationListeners();
        }

    }

    public static void accountSynchronisationComplete(){
        new GCMRegisterTask().execute((Activity) ARL.ctx);
    }

    public static DrawableUtil getDrawableUtil(int defaultSchema, Context context) {
        if (drawableUtil == null) {
            drawableUtil = new DrawableUtil(defaultSchema, context);
        }
        return drawableUtil;
    }

    public static boolean isInit() {
        return !(ARL.ctx == null);
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static Context getContext(){
        return ctx;
    }


    private static void initNotificationListeners(){
        try {
            List<String> classNameList = Arrays.asList(config.getProperty("notificationListeners").split(";"));
            notificationListenerInterfaces = new NotificationListenerInterface[classNameList.size()];
            int i = 0;
            for (String className :classNameList) {
                notificationListenerInterfaces[i++] = (NotificationListenerInterface) Class.forName(className).newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

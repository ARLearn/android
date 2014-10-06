package org.celstec.arlearn2.android.game;

import android.content.Context;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.internal.DaoConfig;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AccountDelegator;
import org.celstec.arlearn2.android.delegators.GameDelegator;
import org.celstec.dao.gen.AccountLocalObject;
import org.celstec.dao.gen.AccountLocalObjectDao;
import org.celstec.dao.gen.DaoSession;
import org.celstec.dao.gen.RunLocalObject;

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
public class InitWhiteLabelDatabase {

    private Long gameId;
    private Long runId;

    private Context context;

    public InitWhiteLabelDatabase(Context context) {
        this.context = context;
    }

    public void init(){
        gameId = Long.parseLong(ARL.config.getProperty("white_label_gameId"));
        runId = Long.parseLong(ARL.config.getProperty("white_label_runId"));
        if (ARL.config.getBooleanProperty("white_label_online")){

        } else {
            loadGameScript();
            loadGameFiles();
            loadRunScript();
            createDefaultAccount();
        }


    }

    private void loadGameScript() {
        GameDelegator.getInstance().loadGameFromFile(context, gameId);
    }

    private void loadGameFiles(){
        GameDelegator.getInstance().retrieveGameFilesFromFile(context, gameId);
    }

    private void loadRunScript() {
            if (DaoConfiguration.getInstance().getRunLocalObjectDao().loadAll().size()==0){
                RunLocalObject runLocalObject = new RunLocalObject();
                runLocalObject.setGameId(gameId);
                runLocalObject.setTitle("Default");
                DaoConfiguration.getInstance().getRunLocalObjectDao().insertOrReplace(runLocalObject);
            }

    }

    private void createDefaultAccount() {
        AccountLocalObject defaultAccount = new AccountLocalObject();
        defaultAccount.setAccountLevel(0);
        defaultAccount.setEmail("default@mail.com");
        defaultAccount.setName("Anonymous");
        defaultAccount.setFamilyName("Anonymous");
        defaultAccount.setGivenName("Anonymous");
        defaultAccount.setFullId("0:0");
        defaultAccount.setAccountType(0);
        defaultAccount.setLocalId("0");
        defaultAccount.setId(1l);
        DaoConfiguration.getInstance().getAccountLocalObjectDao().insertOrReplace(defaultAccount);
        AccountDelegator.getInstance().setAccount(defaultAccount);
        ARL.properties.setAccount(defaultAccount.getId());
    }
}

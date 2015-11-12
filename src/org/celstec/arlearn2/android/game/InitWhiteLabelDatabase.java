package org.celstec.arlearn2.android.game;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.view.View;
import daoBase.DaoConfiguration;
import de.greenrobot.dao.internal.DaoConfig;
import org.celstec.arlearn2.android.R;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.delegators.AccountDelegator;
import org.celstec.arlearn2.android.delegators.GameDelegator;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager;
import org.celstec.arlearn2.android.delegators.game.GameDownloadManager2;
import org.celstec.arlearn2.android.views.DownloadViewManager;
import org.celstec.arlearn2.beans.game.Config;
import org.celstec.dao.gen.*;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

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
    protected List<Long> gameIdsLong;

    protected Context context;

    public static InitWhiteLabelDatabase getWhiteLabelDatabaseIniter(Context ctx) {
        if (ARL.config.getBooleanProperty("white_label_online_sync")) {
            return new InitWhiteLabelDatabaseOnlineSync(ctx);
        }
        return new InitWhiteLabelDatabase(ctx);
    }

    public InitWhiteLabelDatabase(Context context) {
        this.context = context;
    }

    public static List<Long> getGameIds(){
        String localizedGames = "white_label_gameId_"+Locale.getDefault().getLanguage();
        System.out.println("localised string "+localizedGames);
        String gameIds =  null;
        if (ARL.config.getProperty(localizedGames) != null) {

            gameIds = ARL.config.getProperty(localizedGames);
        } else {
            System.out.println("not localised");
            gameIds = ARL.config.getProperty("white_label_gameId");
        }
        List<String> list = new ArrayList<String>(Arrays.asList(gameIds.split(",")));
        List<Long> gameIdsLong= new ArrayList<Long>(list.size());
        for (String gameId: list){
            gameIdsLong.add(Long.parseLong(gameId));
        }
        return gameIdsLong;
    }

    public void init() {
        ARL.eventBus.register(this);
        ARL.eventBus.post(new AsyncStartInitDB());
    }

    public void onEventAsync(AsyncStartInitDB event) {
        try {
            gameIdsLong = getGameIds();
            if (ARL.config.getProperty("white_label_runId") != null) {
                runId = Long.parseLong(ARL.config.getProperty("white_label_runId"));
            } else {
                runId = 0l;
            }
            if (ARL.config.getBooleanProperty("white_label_online")) {

            } else {
                loadGameScript();
                loadGameFiles();
                loadRunScript();
                if (!ARL.config.getBooleanProperty("show_anonymous_registration")) createDefaultAccount();
                createMaps();
                unpackTileSources();
                ARL.eventBus.post(new SyncReady(true));
            }

        } catch (Exception e) {
            e.printStackTrace();
            ARL.eventBus.post(new SyncReady(false));
        }

        ARL.eventBus.unregister(this);
    }

    private void unpackTileSources() {
        if (gameId != null) {
            unpackTileSources(gameId);
        } else {
            for (Long gameIdLong:gameIdsLong) {
                unpackTileSources(gameIdLong);
            }
        }
    }

    private static final int BUFFER_SIZE = 4096;

    private void unpackTileSources(long gameId) {
        Config config = DaoConfiguration.getInstance().getGameLocalObjectDao().load(gameId).getGameBean().getConfig();
        if (config.getTileSource()!= null) {
            System.out.println("unpacking tiles");
            File zipFilePath = GameFileLocalObject.getGameFileLocalObject(gameId, "/"+config.getTileSource()).getLocalFile();
            String destDirectory = Environment.getExternalStorageDirectory()+"/osmdroid";
            if (!new File(destDirectory).exists()) {
                new File(destDirectory).mkdir();
            }
            destDirectory += "/tiles";
            if (!new File(destDirectory).exists()) {
                new File(destDirectory).mkdir();
            }
            try {
                extractFolder(zipFilePath.getAbsolutePath(), destDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            File destDir = new File(Environment.getExternalStorageDirectory()+"/osmdroid");
//            if (!destDir.exists()) {
//                destDir.mkdir();
//            }
//            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
//            ZipEntry entry = zipIn.getNextEntry();
//            // iterates over entries in the zip file
//            while (entry != null) {
//                String filePath = destDirectory + File.separator + entry.getName();
//                if (!entry.isDirectory()) {
//                    // if the entry is a file, extracts it
//                    extractFile(zipIn, filePath);
//                } else {
//                    // if the entry is a directory, make the directory
//                    File dir = new File(filePath);
//                    dir.mkdir();
//                }
//                zipIn.closeEntry();
//                entry = zipIn.getNextEntry();
//            }
//            zipIn.close();
        }
    }


    static public void extractFolder(String zipFile, String newPath) throws ZipException, IOException
    {
        System.out.println(zipFile);
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements())
        {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory())
            {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
        }
    }


        private void createMaps() {
        if (ARL.config.getProperty("white_label_map_file")!= null) {
            String filename = ARL.config.getProperty("white_label_map_file");
            AssetManager assetManager = context.getAssets();
            try {
                InputStream in = assetManager.open(filename);
                if (!new File("/mnt/sdcard/osmdroid/").exists()) {
                    new File("/mnt/sdcard/osmdroid/").mkdir();
                }
                File outFile = new File("/mnt/sdcard/osmdroid/", filename);
                OutputStream out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFileToOSM(Context context, String filename){
        AssetManager assetManager = context.getAssets();
        try {
            File file = new File(filename);
            FileInputStream in = new FileInputStream(file);
            if (!new File("/mnt/sdcard/osmdroid/").exists()) {
                new File("/mnt/sdcard/osmdroid/").mkdir();
            }
            File outFile = new File("/mnt/sdcard/osmdroid/", file.getName());
            OutputStream out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    protected void loadGameScript() {
        if (gameId != null) {
            GameDelegator.getInstance().loadGameFromFile(context, gameId);
        } else {
            for (Long gameIdLong:gameIdsLong) {
                GameDelegator.getInstance().loadGameFromFile(context, gameIdLong);
            }
        }
    }

    protected void loadGameFiles(){
        System.out.println("load game files in superclass");
            for (long gameId: gameIdsLong) {
                GameDelegator.getInstance().retrieveGameFilesFromFile(context, gameId);
            }
    }

    private void loadRunScript() {
        if (!ARL.config.getBooleanProperty("white_label_login")) {
            if (!ARL.config.getBooleanProperty("show_anonymous_registration"))
            for (GameLocalObject gameLocalObject : DaoConfiguration.getInstance().getGameLocalObjectDao().loadAll()) {
                RunLocalObject runLocalObject = new RunLocalObject();
                runLocalObject.setGameId(gameLocalObject.getId());
                runLocalObject.setTitle("Default");
                runLocalObject.setDeleted(false);
                DaoConfiguration.getInstance().getRunLocalObjectDao().insertOrReplace(runLocalObject);
            }
        }
    }

    private void createDefaultAccount() {
        if (!ARL.config.getBooleanProperty("white_label_login")) {
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

    public class AsyncStartInitDB{

    }

    public class SyncReady{
        private boolean success;

        public SyncReady(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}

package org.celstec.arlearn2.android.whitelabel;

import android.app.Activity;
import android.net.Uri;
import daoBase.DaoConfiguration;
import org.celstec.arlearn2.android.util.MediaFolders;
import org.celstec.dao.gen.GameFileLocalObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by str on 23/01/15.
 */
public class HtmlSplashScreenInitiationScript {

    public HtmlSplashScreenInitiationScript(Activity activity) {
        byte[] buffer = new byte[2048];
        for (GameFileLocalObject gameFileLocalObject : DaoConfiguration.getInstance().getGameFileDao().loadAll()) {
            System.out.println(gameFileLocalObject.getPath());
            File baseFolder = MediaFolders.getIncommingFilesDir();

            if (gameFileLocalObject.getPath().equals("/htmlSplash")) {
                baseFolder = new File(baseFolder, ""+gameFileLocalObject.getGameId());
                baseFolder.mkdir();
                baseFolder = new File(baseFolder, "htmlSplash");
                baseFolder.mkdir();
                Uri zipUri = gameFileLocalObject.getLocalUri();

                try {
                    ZipInputStream zis = new ZipInputStream(activity.getContentResolver().openInputStream(zipUri));

                    ZipEntry ze = null;
                    while ((ze = zis.getNextEntry()) != null) {
                        System.out.println("zip nae " + ze.getName());
                        File target = new File(baseFolder, ze.getName());
                        if (ze.isDirectory()) {
                            target.mkdir();
                        } else {
                            FileOutputStream output = null;
                            try {
                                output = new FileOutputStream(target);
                                int len = 0;
                                while ((len = zis.read(buffer)) > 0) {
                                    output.write(buffer, 0, len);
                                }
                            } finally {
                                // we must always close the output file
                                if (output != null) output.close();
                            }
                        }

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

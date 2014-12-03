package org.celstec.arlearn2.android.whitelabel;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.celstec.arlearn2.beans.GamePackage;
import org.celstec.arlearn2.beans.deserializer.json.JsonBeanDeserializer;
import org.celstec.arlearn2.beans.game.GameFile;
import org.celstec.arlearn2.beans.game.GameFileList;
import org.celstec.arlearn2.beans.generalItem.AudioObject;
import org.celstec.arlearn2.beans.generalItem.FileReference;
import org.celstec.arlearn2.beans.generalItem.GeneralItem;
import org.celstec.arlearn2.client.GenericClient;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

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
public class WhiteLabelDownloadScript {

    public static void main(String[] args) throws Exception {
        String authToken = args[0];
        Long gameId = Long.parseLong(args[1]);
        String baseDir = args[2];
        String rawDir = args[3];

        String game = downloadUrl("download/game?gameId=" + gameId + "&auth=" + authToken + "&type=game", baseDir+"/game."+gameId+".json", null);
        System.out.println("saving game package");
        String contentJsonPath = baseDir+"/game."+gameId+".Content.json";
        String gameFile = downloadUrl("rest/myGames/gameContent/gameId/"+gameId, contentJsonPath, authToken);
        JsonBeanDeserializer jsonBeanDeserializer = new JsonBeanDeserializer(gameFile);
        GameFileList gameFileList = (GameFileList) jsonBeanDeserializer.deserialize(gameFile);
        for (GameFile gf : gameFileList.getGameFiles()) {
            String fullPath = "http://streetlearn.appspot.com/game/"+gameId+gf.getPath();
            String fileName = mapPathToFileName(gameId, gf.getPath());
            System.out.println("downloading "+fullPath+"  ..  "+ fileName);
            downloadFile(fullPath, rawDir+"/"+fileName);
            gf.setLocalRawRef(fileName);
        }
        File f = new File(contentJsonPath);
        FileWriter fr = new FileWriter(f);
        BufferedWriter br  = new BufferedWriter(fr);
        br.write(gameFileList.toString());
        br.flush();
        br.close();

    }

    public static String mapPathToFileName(long gameId, String partialPath) {
        String fullPath = "http://streetlearn.appspot.com/game/"+gameId+partialPath;
        String fileName = null;
        if (fullPath.contains(gameId+"/generalItems")) {
            String path = fullPath.substring(fullPath.indexOf("generalItems")+13);
            String id = path.substring(0,path.indexOf("/"));
            String key = path.substring(path.indexOf("/")+1);
            fileName = key + "_"+id;

        } else {
            fileName = fullPath.substring(fullPath.indexOf(""+gameId));
            fileName = fileName.substring(fileName.indexOf("/")+1);
        }
        return fileName.toLowerCase();
    }


    public static void main2(String[] args) throws Exception {
        String authToken = args[0];
        Long gameId = Long.parseLong(args[1]);
        String baseDir = args[2];
        String rawDir = args[3];

        String game = downloadUrl("download/game?gameId=" + gameId + "&auth=" + authToken + "&type=game", baseDir+"/game."+gameId+".json", null);
        System.out.println(game);
        JsonBeanDeserializer jsonBeanDeserializer = new JsonBeanDeserializer(game);
        GamePackage gamePackage = (GamePackage) jsonBeanDeserializer.deserialize(GamePackage.class);
        System.out.println(gamePackage);

        GameFileList gameFileList = new GameFileList();
        long index = 1l;
        for (GeneralItem item : gamePackage.getGeneralItems()) {

            if (item.getIconUrl() != null) {
                System.out.println("icon  "+item.getIconUrl());
                downloadFile(item.getIconUrl(), rawDir+"/icon_"+item.getId());
                GameFile gameFile = new GameFile();
                gameFile.setPath(item.getIconUrl().substring(item.getIconUrl().indexOf("generalItems")-1));
                gameFile.setLocalRawRef("icon_"+item.getId());
                gameFile.setId(index++);
                gameFileList.addGameFile(gameFile);
            }

            if (item.getFileReferences() != null) {

                for (FileReference fileReference:item.getFileReferences()) {
                    System.out.println("ref "+fileReference.getFileReference()+ " " + fileReference.getKey());
                    downloadFile(fileReference.getFileReference(), rawDir+"/"+fileReference.getKey());
                    GameFile gameFile = new GameFile();
                    gameFile.setPath(fileReference.getFileReference().substring(fileReference.getFileReference().indexOf("generalItems")-1));

                    gameFile.setLocalRawRef(fileReference.getKey());
                    gameFile.setId(index++);
                    gameFileList.addGameFile(gameFile);

                }
            }
            if (item instanceof AudioObject) {
                AudioObject audioObject = (AudioObject) item;
                String audioFeed = audioObject.getAudioFeed();
                downloadFile(audioFeed, rawDir+"/audio_"+audioObject.getId());
                GameFile gameFile = new GameFile();
                gameFile.setPath(audioFeed.substring(audioFeed.indexOf("generalItems")-1));
                gameFile.setLocalRawRef("audio_"+audioObject.getId());
                gameFile.setId(index++);
                gameFileList.addGameFile(gameFile);

            }
        }
        File f = new File(baseDir+"/game."+gameId+".Content.json");
        FileWriter fr = new FileWriter(f);
        BufferedWriter br  = new BufferedWriter(fr);
        br.write(gameFileList.toString());
        br.close();


    }



//    {
//        "type": "org.celstec.arlearn2.beans.game.GameFileList",
//            "gameFiles": [
//        {
//            "type": "org.celstec.arlearn2.beans.game.GameFile",
//                "path": "/gameMessagesHeader",
//                "localRawRef": "header_florence",
//                "id": 1
//        },
//        {
//            "type": "org.celstec.arlearn2.beans.game.GameFile",
//                "path": "/gameSplashScreen",
//                "localRawRef": "splash_florence",
//                "id": 2
//        }
//        ]
//    }

//    public static void downloadFile(String url, String fileName) {
//        try {
//            URL obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            // optional default is GET
//            con.setRequestMethod("GET");
//
//            //add request header
//
//            int responseCode = con.getResponseCode();
//            System.out.println("\nSending 'GET' request to URL : " + url);
//            System.out.println("Response Code : " + responseCode);
//
////            BufferedReader in = new BufferedReader(
////                    new InputStreamReader(con.getInputStream()));
//
//            InputStream is = con.getInputStream();
//            StringBuffer response = new StringBuffer();
//            File f = new File(fileName);
//            FileOutputStream fo = new FileOutputStream(f);
////            BufferedWriter br  = new BufferedWriter(fr);
//
//            while (is.available()>0) {
//
//                fo.write(is.read());
//
//            }
//
//            is.close();
//            fo.close();
//
//            //print result
////            System.out.println(response.toString());
////            JSONObject object = new JSONObject(response.toString());
////            String returnString = object.toString(5);
////
////            br.write(returnString);
////            br.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    public static void downloadFile(String url, String fileName) {
        url = url.replace("http://dl.dropboxusercontent.com","https://dl.dropboxusercontent.com");
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            int responseCode = con.getResponseCode();
            if (responseCode == 302) {
                String newUrl = con.getHeaderField("Location");
                System.out.println(newUrl);
                downloadFile(newUrl, fileName);
            } else {
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);


                InputStream is = con.getInputStream();
                StringBuffer response = new StringBuffer();
                File f = new File(fileName);
                FileOutputStream fo = new FileOutputStream(f);

//            while (is.available()>0) {
//
//                fo.write(is.read());
//
//            }
                byte[] b = new byte[2048];

                int length;
                while ((length = is.read(b)) != -1) {
                    fo.write(b, 0, length);
                }

                is.close();
                fo.close();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String downloadUrl(String path, String filePath, String authToken) {
        try {
            String url = "http://streetlearn.appspot.com/"+path;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            if (authToken != null)  con.setRequestProperty("Authorization", "GoogleLogin auth="+authToken);
                //add request header

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            File f = new File(filePath);
            FileWriter fr = new FileWriter(f);
            BufferedWriter br  = new BufferedWriter(fr);

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);


            }

            in.close();

            //print result
//            System.out.println(response.toString());
            JSONObject object = new JSONObject(response.toString());
            String returnString = object.toString(5);

            br.write(returnString);
            br.close();
            return returnString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

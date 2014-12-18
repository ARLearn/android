package org.celstec.arlearn2.android;

import android.net.Uri;
import android.os.StrictMode;import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.beans.game.Game;
import org.celstec.arlearn2.beans.run.Run;

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
public abstract class UserGameIntentAnalyser {

    private long gameToLoad = 0l;
    private long runToLoad = 0l;

    public boolean analyze(Uri uri){
        if (uri == null) return false;
        return analyze(uri.toString());
    }

    public boolean analyze(String data) {


            if (data.startsWith("http://streetlearn.appspot.com/game/")) {
                data =data.replace("http://streetlearn.appspot.com/game/", "gameId/");
            }
            if (data.contains("gameId/")) {
                try {
                    data = data.substring(data.lastIndexOf("gameId/") + 7);
                    String remainder = data.substring(data.lastIndexOf("gameId/") + 7);
                    boolean runFound=false;
                    if (data.contains("/")) {
                        data = data.substring(0, data.indexOf("/"));
                        if (remainder.contains("/")) {
                            remainder = remainder.substring(remainder.indexOf("/"));
                            runFound = analyseForRun(remainder);
                        }
                    }
                    long gameId = Long.parseLong(data);
//                    System.out.println(gameId);
                    Game gameBean = ARL.games.asyncGameBean(gameId);
                    if (gameBean !=null && !runFound) scannedGame(gameBean);
                    return true;
                } catch (NumberFormatException e) {

                }
            }
            if (data.startsWith("http://streetlearn.appspot.com/oai/resolve/")) {
                String gameIdAsString = data.substring(data.lastIndexOf("/") + 1);
                if (gameIdAsString.contains("?")) {
                    gameIdAsString = gameIdAsString.substring(0, gameIdAsString.indexOf("?"));
                }
                gameToLoad = Long.parseLong(gameIdAsString);
            }

        return false;
    }

    private boolean analyseForRun(String data) {
        data = data.replace("run/", "runId/");
        if (data.contains("runId/")) {
            try {
                data = data.substring(data.lastIndexOf("runId/") + 6);
                if (data.contains("/")) {
                    data = data.substring(0, data.indexOf("/"));
                }
                long runId = Long.parseLong(data);
//                    System.out.println(gameId);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
                Run runBean = ARL.runs.asyncRunBean(runId);
                if (runBean !=null) {
                    scannedRun(runBean);
                    return true;
                }
            } catch (NumberFormatException e) {

            }
        }
        return false;
    }

    public UserGameIntentAnalyser(){

    }

    public boolean hasGameToLoad(){
        return gameToLoad != 0;
    }

    public long getGameId() {
        return gameToLoad;
    }

    public boolean hasRunToLoad(){
        return runToLoad!=0;
    }

    public long getRunId(){
        return runToLoad;
    }

    public abstract void scannedGame(Game game);

    public abstract void scannedRun(Run runId);

    public abstract void scannedLoginToken(String loginToken);


}

package org.celstec.arlearn2.android.db;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.celstec.arlearn2.android.R;

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
public class ConfigAdapter {
    private Context context;
    private PropertiesExt properties;

    public ConfigAdapter(Context context) {
        this.context = context;
        properties = new PropertiesExt();
    }

    public PropertiesExt getProperties() {
        try {
            AssetManager assetManager = context.getAssets();

            InputStream inputStream = assetManager.open("config.properties");
            properties.load(inputStream);

        } catch (IOException e) {
            Log.e("ConfigAdapter",e.toString());
        }
        return properties;

    }

    public class PropertiesExt extends Properties {

        public static final int NONE = 0;
        public static final int TRANSPARENT = 1;
        public static final int HALF = 2;


        public boolean getBooleanProperty(String key) {
            String value = getProperty(key);
            if (value == null) return false;
            if ("yes".equals(value.toLowerCase().trim())) return true;
            return "true".equals(value.toLowerCase().trim());
        }

        public int getContentView() {
            if (getProperty("games_screen") == null) return R.layout.mygames_list;
            if (getProperty("games_screen").equals("grid")) return R.layout.mygames_list_grid;
            return R.layout.mygames_list;
        }

        public int getGameMapActionBarTransparency(){
            if (getProperty("game_map_action_bar_transparency").equalsIgnoreCase("transparent")) return 1;
            if (getProperty("game_map_action_bar_transparency").equalsIgnoreCase("half")) return 2;
            return 0;
        }

        public boolean isGameMapActionBarTransparent(){
            if (getGameMapActionBarTransparency()==0) return false;
            return true;
        }

        public int getGameMessagesActionBarTransparency(){
            if (getProperty("game_messages_action_bar_transparency").equalsIgnoreCase("transparent")) return 1;
            if (getProperty("game_messages_action_bar_transparency").equalsIgnoreCase("half")) return 2;
            return 0;
        }

        public boolean isGameMessagesActionBarTransparent(){
            if (getGameMapActionBarTransparency()==0) return false;
            return true;
        }
    }

}
package authentication;

import org.celstec.arlearn2.android.delegators.ARL;

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
public class WebViewLinkedin extends WebViewFragment{
    @Override
    protected String getUrl() {
        return "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&" +
                "client_id="
                + ARL.config.getProperty("linkedin_clientId")
                + "&scope=r_fullprofile%20r_emailaddress%20r_network" +
                "&state=BdhOU9fFb6JcK5BmoDeOZbaY58" +
                "&redirect_uri="+ARL.config.getProperty("linkedin_redirecturi");
    }
}

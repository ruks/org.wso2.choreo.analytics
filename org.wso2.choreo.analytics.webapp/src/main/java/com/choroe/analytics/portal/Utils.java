package com.choroe.analytics.portal;
/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.Cookie;

public class Utils {
    public static String getAuthorizeUrl() {
        Configuration conf = Configuration.getInstance();
        String authEndpoint = conf.getAuthorizeEndpoint();
        String ck = conf.getConsumerKey();
        String callback = conf.getLoginCallback();

        return authEndpoint + "?response_type=code&client_id=" + ck + "&scope=openid&redirect_uri=" + callback;
    }

    public static String getLogOutUrl(String idToken) {
        Configuration conf = Configuration.getInstance();
        String logoutEndpoint = conf.getOidcLogoutURL();
        String callback = conf.getOidcLogoutcallBack();

        return logoutEndpoint + "?id_token_hint=" + idToken + "&post_logout_redirect_uri=" + callback;
    }

    public static String getIdToken(Cookie[] cookies) {
        Configuration conf = Configuration.getInstance();
        String idPart1 = "";
        String idPart2 = "";
        for (Cookie c : cookies) {
            if (conf.getIdTokenCookieKey1().equals(c.getName())) {
                idPart1 = c.getValue();
            }
            if (conf.getIdTokenCookieKey2().equals(c.getName())) {
                idPart2 = c.getValue();
            }
        }
        return idPart1 + idPart2;
    }

    public static List<Cookie> getLogoutCookies() {
        Configuration conf = Configuration.getInstance();
        Cookie cp1 = new Cookie(conf.getTokenCookieKey1(), "");
        cp1.setDomain(conf.getCookieHostName());
        cp1.setHttpOnly(false);
        cp1.setPath("/");
        cp1.setMaxAge(3600);

        Cookie cp2 = new Cookie(conf.getTokenCookieKey2(), "");
        cp2.setDomain(conf.getCookieHostName());
        cp2.setHttpOnly(true);
        cp2.setPath("/");
        cp2.setMaxAge(3600);

        Cookie cp3 = new Cookie(conf.getIdTokenCookieKey1(), "");
        cp3.setDomain(conf.getCookieHostName());
        cp3.setHttpOnly(false);
        cp3.setPath("/");
        cp3.setMaxAge(3600);

        Cookie cp4 = new Cookie(conf.getIdTokenCookieKey2(), "");
        cp4.setDomain(conf.getCookieHostName());
        cp4.setHttpOnly(true);
        cp4.setPath("/");
        cp4.setMaxAge(3600);

        return Arrays.asList(cp1, cp2, cp3, cp4);
    }

    public static List<Cookie> getLoginCookies(String token, String idToken) {
        int tokenLen = token.length();
        String part1 = token.substring(0, tokenLen/2);
        String part2 = token.substring(tokenLen/2, tokenLen);
        String idPart1 = idToken.substring(0, idToken.length()/2);
        String idPart2 = idToken.substring(idToken.length()/2, idToken.length());

        Configuration conf = Configuration.getInstance();
        Cookie cp1 = new Cookie(conf.getTokenCookieKey1(), part1);
        cp1.setDomain(conf.getCookieHostName());
        cp1.setHttpOnly(false);
        cp1.setPath("/");
        cp1.setMaxAge(3600);

        Cookie cp2 = new Cookie(conf.getTokenCookieKey2(), part2);
        cp2.setDomain(conf.getCookieHostName());
        cp2.setHttpOnly(true);
        cp2.setPath("/");
        cp2.setMaxAge(3600);

        Cookie cp3 = new Cookie(conf.getIdTokenCookieKey1(), idPart1);
        cp3.setDomain(conf.getCookieHostName());
        cp3.setHttpOnly(false);
        cp3.setPath("/");
        cp3.setMaxAge(3600);

        Cookie cp4 = new Cookie(conf.getIdTokenCookieKey2(), idPart2);
        cp4.setDomain(conf.getCookieHostName());
        cp4.setHttpOnly(true);
        cp4.setPath("/");
        cp4.setMaxAge(3600);

        return Arrays.asList(cp1, cp2, cp3, cp4);
    }
}

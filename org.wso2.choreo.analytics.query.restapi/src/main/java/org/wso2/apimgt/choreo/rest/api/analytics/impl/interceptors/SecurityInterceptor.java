/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Configuration;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.security.JWTValidator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(SecurityInterceptor.class);
    private JWTValidator jwtValidator;

    public SecurityInterceptor() {
        this.jwtValidator = new JWTValidator();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = getToken(request);
        if (token == null) {
            log.error("token is missing");
            response.setStatus(401);
            return false;
        }
        return setAuthContext(request, token);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception exception) throws Exception {
    }

    private boolean setAuthContext(HttpServletRequest request, String token) {

        AuthenticationContext context = null;
        try {
            context = this.jwtValidator.validate(token);
            context.setUsername("admin");
            context.setTenant("foo.com");
            context.setCustomerId("azure");
            context.setDeploymentId(request.getHeader("Environment"));
            request.setAttribute("authContext", context);
            return true;
        } catch (Exception e) {
            log.error("Error occurred while validating token");
            return false;
        }

    }

    public static String getToken(HttpServletRequest request) {
        Cookie[] cookieVal = request.getCookies();
        if (cookieVal == null) {
            log.error("Cookie not found in the request.");
            return null;
        }
        String part1Name = "capp1";
        String part2Name = "capp2";
        String part1Value = null;
        String part2Value = null;
        for (Cookie aCookie : cookieVal) {
            if (part1Name.equalsIgnoreCase(aCookie.getName())) {
                part1Value = aCookie.getValue();
            }
            if (part2Name.equalsIgnoreCase(aCookie.getName())) {
                part2Value = aCookie.getValue();
            }
        }
        if (part1Value == null || part2Value == null) {
            log.error("Cookie '" + part1Name + "' and '" + part2Name + " 'not found in the cookies.");
            return null;
        }
        return part1Value + part2Value;
    }
}

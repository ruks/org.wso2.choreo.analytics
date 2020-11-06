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

package org.wso2.apimgt.choreo.rest.api.analytics.impl.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.APIConfiguration;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.ConfigHolder;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.config.Security;
import org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptor.AuthenticationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public class JWTValidator {
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public JWTValidator() {
        Security sec = ConfigHolder.getInstance().getConfiguration().getSecurity();
        this.jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = null;
        try {
            keySource = new RemoteJWKSet<>(new URL(sec.getJwks()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
        this.jwtProcessor.setJWSKeySelector(keySelector);
        this.jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(
                new JWTClaimsSet.Builder().issuer(sec.getJwksIssuer()).build(),
                new HashSet<>(Arrays.asList("sub", "iat", "exp", "jti"))));
    }

    public AuthenticationContext validate(String token) throws Exception {
        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = this.jwtProcessor.process(token, ctx);

        AuthenticationContext context = new AuthenticationContext();
        context.setUsername(claimsSet.getSubject());
        System.out.println("Subject: " + context.getUsername());
        return context;
    }
}

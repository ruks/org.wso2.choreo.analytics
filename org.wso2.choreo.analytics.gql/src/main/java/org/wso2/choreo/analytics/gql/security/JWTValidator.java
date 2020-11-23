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

package org.wso2.choreo.analytics.gql.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.wso2.choreo.analytics.gql.config.ConfigHolder;
import org.wso2.choreo.analytics.gql.config.Security;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

public class JWTValidator {
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private static JWTValidator validator;

    private JWTValidator() {
    }

    private void init() {
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

    public synchronized static JWTValidator get() {
        if(validator == null) {
            validator = new JWTValidator();
            validator.init();
        }
        return validator;
    }

    public JWTClaimsSet validate(String token) throws ParseException, JOSEException, BadJOSEException {
        SecurityContext ctx = null;
        JWTClaimsSet claimsSet = this.jwtProcessor.process(token, ctx);
        return claimsSet;
    }
}

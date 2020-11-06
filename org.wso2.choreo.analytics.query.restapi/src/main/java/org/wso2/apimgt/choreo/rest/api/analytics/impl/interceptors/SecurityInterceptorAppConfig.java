package org.wso2.apimgt.choreo.rest.api.analytics.impl.interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class SecurityInterceptorAppConfig extends WebMvcConfigurerAdapter {
   @Autowired
   SecurityInterceptor securityInterceptor;

   @Override
   public void addInterceptors(InterceptorRegistry registry) {
      registry.addInterceptor(securityInterceptor);
   }
}

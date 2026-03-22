package org.example.bookstore_app.config;

import org.springframework.lang.NonNull;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { SpringConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[] { WebConfig.class };
    }

    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[]{ "/" };
    }
    public static class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    }
}
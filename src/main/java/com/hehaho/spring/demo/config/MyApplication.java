/**
 * 
 */
package com.hehaho.spring.demo.config;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.hehaho.spring.demo.resources.JerseyTestResource;
import com.hehaho.spring.demo.resources.MultiPartResource;


public class MyApplication extends Application {

    /**
     * Register JAX-RS application components.
     */
    public MyApplication () {
    }
    
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> resources = new HashSet<Class<?>>();

        // Add your resources.
        resources.add(JerseyTestResource.class);
        resources.add(MultiPartResource.class);

        // Add additional features such as support for Multipart.
        resources.add(MultiPartFeature.class);

        return resources;
    }
}
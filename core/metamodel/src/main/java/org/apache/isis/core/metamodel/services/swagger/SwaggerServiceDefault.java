/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.core.metamodel.services.swagger;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.internal.base._NullSafe;
import org.apache.isis.applib.internal.resources._Resource;
import org.apache.isis.applib.services.swagger.SwaggerService;
import org.apache.isis.core.metamodel.services.swagger.internal.SwaggerSpecGenerator;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "" + Integer.MAX_VALUE
)
public class SwaggerServiceDefault implements SwaggerService {

    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(SwaggerServiceDefault.class);

    public static final String KEY_RESTFUL_BASE_PATH = "isis.services.swagger.restfulBasePath";
    public static final String KEY_RESTFUL_BASE_PATH_DEFAULT = "/restful";

    private String basePath;

    @PostConstruct
    public void init(final Map<String,String> properties) {
    	
    	// ----------------------------------------------------------------------------------------------------------
    	// TODO [ahuber] this initialization must be done once before accessing _Resource.prependContextPathIfPresent
    	// could be done anywhere during bootstrapping
    	final String restfulPath = 
    			_NullSafe.getOrDefault(properties, KEY_RESTFUL_BASE_PATH, KEY_RESTFUL_BASE_PATH_DEFAULT);
    	_Resource.putRestfulPath(restfulPath); 
    	// ----------------------------------------------------------------------------------------------------------
    	
    	this.basePath = _Resource.prependContextPathIfPresent(restfulPath);
    }

    @Programmatic
    @Override
    public String generateSwaggerSpec(
            final Visibility visibility,
            final Format format) {

        final SwaggerSpecGenerator swaggerSpecGenerator = new SwaggerSpecGenerator(specificationLoader);
        final String swaggerSpec = swaggerSpecGenerator.generate(basePath, visibility, format);
        return swaggerSpec;
    }

    @javax.inject.Inject
    SpecificationLoader specificationLoader;

}

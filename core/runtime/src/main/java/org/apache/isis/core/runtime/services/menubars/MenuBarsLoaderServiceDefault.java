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
package org.apache.isis.core.runtime.services.menubars;

import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.layout.menubars.bootstrap3.BS3MenuBars;
import org.apache.isis.applib.services.jaxb.JaxbService;
import org.apache.isis.applib.services.menu.MenuBarsLoaderService;
import org.apache.isis.core.metamodel.deployment.DeploymentCategoryProvider;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;

@DomainService(nature = NatureOfService.DOMAIN)
public class MenuBarsLoaderServiceDefault implements MenuBarsLoaderService {

    @Override
    public boolean supportsReloading() {
        return !deploymentCategoryProvider.getDeploymentCategory().isProduction();
    }

    @Override
    public BS3MenuBars menuBars() {
        final AppManifest appManifest = isisSessionFactory.getAppManifest();
        try {
            final URL resource = Resources.getResource(appManifest.getClass(), "menubars.layout.xml");
            String xml = Resources.toString(resource, Charsets.UTF_8);

            return jaxbService.fromXml(BS3MenuBars.class, xml);
        } catch (Exception e) {
            return null;
        }
    }

    @javax.inject.Inject
    DeploymentCategoryProvider deploymentCategoryProvider;

    @javax.inject.Inject
    JaxbService jaxbService;

    @javax.inject.Inject
    IsisSessionFactory isisSessionFactory;

}


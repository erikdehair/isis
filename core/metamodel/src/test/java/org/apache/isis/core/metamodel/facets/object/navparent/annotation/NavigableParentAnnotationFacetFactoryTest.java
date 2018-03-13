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
package org.apache.isis.core.metamodel.facets.object.navparent.annotation;

import java.lang.reflect.Method;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.authentication.AuthenticationSessionProvider;
import org.apache.isis.core.commons.reflection.Reflect;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.deployment.DeploymentCategory;
import org.apache.isis.core.metamodel.deployment.DeploymentCategoryProvider;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryJUnit4TestCase;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessClassContext;
import org.apache.isis.core.metamodel.facets.object.navparent.NavigableParentFacet;
import org.apache.isis.core.metamodel.facets.object.navparent.annotation.NavigableParentTestSamples.DomainObjectA;
import org.apache.isis.core.metamodel.facets.object.navparent.method.NavigableParentFacetMethod;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NavigableParentAnnotationFacetFactoryTest extends AbstractFacetFactoryJUnit4TestCase {

    private NavigableParentAnnotationFacetFactory facetFactory;

    @Mock
    private ObjectAdapter mockObjectAdapter;
    @Mock
    private AuthenticationSession mockAuthenticationSession;

    @Before
    public void setUp() throws Exception {

        context.allowing(mockSpecificationLoader);

        facetFactory = new NavigableParentAnnotationFacetFactory();
        facetFactory.setServicesInjector(mockServicesInjector);

        context.checking(new Expectations() {
            {
                allowing(mockServicesInjector).lookupService(AuthenticationSessionProvider.class);
                will(returnValue(mockAuthenticationSessionProvider));

                allowing(mockServicesInjector).lookupService(DeploymentCategoryProvider.class);
                will(returnValue(mockDeploymentCategoryProvider));

                allowing(mockDeploymentCategoryProvider).getDeploymentCategory();
                will(returnValue(DeploymentCategory.PRODUCTION));

                allowing(mockAuthenticationSessionProvider).getAuthenticationSession();
                will(returnValue(mockAuthenticationSession));

                allowing(mockServicesInjector).getSpecificationLoader();
                will(returnValue(mockSpecificationLoader));

                allowing(mockServicesInjector).getPersistenceSessionServiceInternal();
                will(returnValue(mockPersistenceSessionServiceInternal));
            }
        });

        facetFactory.setServicesInjector(mockServicesInjector);

    }

    @After
    @Override
    public void tearDown() throws Exception {
        facetFactory = null;
        super.tearDown();
    }

    @Test
    public void testParentAnnotatedMethod() throws Exception {
    	testParentMethod(new DomainObjectA(), "root");
    }

    // -- HELPER
    
    private void testParentMethod(Object domainObject, String parentMethodName) throws Exception {
    	
    	final Class<?> domainClass = domainObject.getClass();
    	
        facetFactory.process(new ProcessClassContext(domainClass, mockMethodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(NavigableParentFacet.class);
        Assert.assertNotNull(facet);
        Assert.assertTrue(facet instanceof NavigableParentFacetMethod);
        
        final NavigableParentFacetMethod navigableParentFacetMethod = (NavigableParentFacetMethod) facet;
        final Method parentMethod = domainClass.getMethod(parentMethodName);
        
        Assert.assertEquals(
        		parentMethod.invoke(domainObject, Reflect.emptyObjects), 
        		navigableParentFacetMethod.navigableParent(domainObject)	);
        
    }
    
    
    
}

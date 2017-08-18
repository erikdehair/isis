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

package org.apache.isis.core.metamodel.facets.properties.propertylayout;

import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.isis.core.metamodel.facets.all.named.NamedFacet;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class NamedFacetForPropertyLayoutAnnotationFactoryTest extends AbstractFacetFactoryTest {

    public void testPropertyLayoutAnnotationNamed() {
        final PropertyLayoutFacetFactory facetFactory = new PropertyLayoutFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @PropertyLayout(named = "1st name")
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        // when
        final ProcessMethodContext processMethodContext = new ProcessMethodContext(Customer.class, null, null, method,
                methodRemover, facetedMethod);

        final FacetHolder holder = facetFactory.facetHolderFrom(processMethodContext);
        final Properties properties = facetFactory.metadataPropertiesFrom(processMethodContext);
        final PropertyLayout propertyLayout = facetFactory.propertyLayoutAnnotationFrom(processMethodContext);

        facetFactory.processNamed(holder, properties, propertyLayout);

        // then
        final NamedFacet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(notNullValue()));
        assertThat(facet, is(instanceOf(NamedFacetForPropertyLayoutAnnotation.class)));
        assertThat(facet.value(), is(equalTo("1st name")));
        assertThat(facet.escaped(), is(true));
    }

    public void testPropertyLayoutAnnotationNamedEscapedFalse() {
        final PropertyLayoutFacetFactory facetFactory = new PropertyLayoutFacetFactory();

        class Customer {
            @SuppressWarnings("unused")
            @PropertyLayout(named = "1st name", namedEscaped = false)
            public String getFirstName() {
                return null;
            }
        }
        final Method method = findMethod(Customer.class, "getFirstName");

        // when
        final ProcessMethodContext processMethodContext = new ProcessMethodContext(Customer.class, null, null, method,
                methodRemover, facetedMethod);

        final FacetHolder holder = facetFactory.facetHolderFrom(processMethodContext);
        final Properties properties = facetFactory.metadataPropertiesFrom(processMethodContext);
        final PropertyLayout propertyLayout = facetFactory.propertyLayoutAnnotationFrom(processMethodContext);

        facetFactory.processNamed(holder, properties, propertyLayout);

        // then
        final NamedFacet facet = facetedMethod.getFacet(NamedFacet.class);
        assertThat(facet, is(notNullValue()));
        assertThat(facet, is(instanceOf(NamedFacetForPropertyLayoutAnnotation.class)));
        assertThat(facet.value(), is(equalTo("1st name")));
        assertThat(facet.escaped(), is(false));
    }

}

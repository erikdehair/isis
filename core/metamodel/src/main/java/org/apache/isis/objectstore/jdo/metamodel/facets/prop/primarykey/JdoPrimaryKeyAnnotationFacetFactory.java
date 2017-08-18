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
package org.apache.isis.objectstore.jdo.metamodel.facets.prop.primarykey;

import javax.jdo.annotations.PrimaryKey;

import org.apache.isis.core.metamodel.facetapi.FacetUtil;
import org.apache.isis.core.metamodel.facetapi.FeatureType;
import org.apache.isis.core.metamodel.facets.Annotations;
import org.apache.isis.core.metamodel.facets.FacetFactoryAbstract;
import org.apache.isis.core.metamodel.facets.FacetedMethod;


public class JdoPrimaryKeyAnnotationFacetFactory extends FacetFactoryAbstract {

    public JdoPrimaryKeyAnnotationFacetFactory() {
        super(FeatureType.PROPERTIES_ONLY);
    }

    @Override
    public void process(ProcessMethodContext processMethodContext) {

        // ignore any view models
        final Class<?> cls = processMethodContext.getCls();
        if(!org.datanucleus.enhancement.Persistable.class.isAssignableFrom(cls)) {
            return;
        }

        final PrimaryKey annotation = Annotations.getAnnotation(processMethodContext.getMethod(), PrimaryKey.class);
        if (annotation == null) {
            return;
        }

        final FacetedMethod holder = processMethodContext.getFacetHolder();
        FacetUtil.addFacet(new JdoPrimaryKeyFacetAnnotation(holder));
        FacetUtil.addFacet(new OptionalFacetDerivedFromJdoPrimaryKeyAnnotation(holder));
        FacetUtil.addFacet(new DisabledFacetDerivedFromJdoPrimaryKeyAnnotation(holder));
    }
}

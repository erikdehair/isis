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
package org.apache.isis.core.progmodel.facets.properties.javaxvaldigits;

import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.progmodel.facets.value.bigdecimal.BigDecimalValueFacet;
import org.apache.isis.core.progmodel.facets.value.bigdecimal.BigDecimalValueFacetAbstract;


public class BigDecimalFacetForPropertyFromJavaxValidationDigitsAnnotation extends BigDecimalValueFacetAbstract {

    private final int precision;
    private final int scale;

    public static Class<? extends Facet> type() {
        return BigDecimalValueFacet.class;
    }

    public BigDecimalFacetForPropertyFromJavaxValidationDigitsAnnotation(final FacetHolder holder, final Integer precision, final Integer scale) {
        super(BigDecimalFacetForPropertyFromJavaxValidationDigitsAnnotation.type(), holder, Derivation.NOT_DERIVED);
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public Integer getPrecision() {
        return precision;
    }

    @Override
    public Integer getScale() {
        return scale;
    }
}

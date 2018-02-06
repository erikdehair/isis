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

package org.apache.isis.core.metamodel.facets.actions.notcontributed;

import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facetapi.FacetAbstract;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;

public abstract class NotContributedFacetAbstract extends FacetAbstract implements NotContributedFacet {

    private final NotContributedAs as;
    private final Contributed contributed;

    public static Class<? extends Facet> type() {
        return NotContributedFacet.class;
    }

    public NotContributedFacetAbstract(
            final NotContributedAs as,
            final Contributed contributed,
            final FacetHolder holder) {
        this(as, contributed, holder, Derivation.NOT_DERIVED);
    }

    public NotContributedFacetAbstract(
            final NotContributedAs as,
            final Contributed contributed,
            final FacetHolder holder,
            final Derivation derivation) {
        super(type(), holder, derivation);
        this.as = as;
        this.contributed = contributed;
    }

    @Override
    public NotContributedAs notContributed() {
        return as;
    }

    @Override
    public Contributed contributed() {
        return contributed;
    }

    @Override
    public boolean toActions() {
        // not contributed to actions if...
        return contributed() == Contributed.AS_NEITHER || contributed() == Contributed.AS_ASSOCIATION;
    }

    @Override
    public boolean toAssociations() {
        // not contributed to associations if...
        return contributed() == Contributed.AS_NEITHER || contributed() == Contributed.AS_ACTION;
    }

}

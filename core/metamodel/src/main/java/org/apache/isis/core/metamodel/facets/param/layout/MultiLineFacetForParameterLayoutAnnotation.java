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

package org.apache.isis.core.metamodel.facets.param.layout;

import java.util.List;

import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.objectvalue.multiline.MultiLineFacet;
import org.apache.isis.core.metamodel.facets.objectvalue.multiline.MultiLineFacetAbstract;

public class MultiLineFacetForParameterLayoutAnnotation extends MultiLineFacetAbstract {

    public static MultiLineFacet create(
            final List<ParameterLayout> parameterLayouts,
            final FacetHolder holder) {

        return parameterLayouts.stream()
                .map(ParameterLayout::multiLine)
                .filter(multiLine -> multiLine != -1)
                .findFirst()
                .map(multiLine -> new MultiLineFacetForParameterLayoutAnnotation(multiLine, false, holder))
                .orElse(null);
    }

    private MultiLineFacetForParameterLayoutAnnotation(int numberOfLines, boolean preventWrapping, FacetHolder holder) {
        super(numberOfLines, preventWrapping, holder);
    }
}

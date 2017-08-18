/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.isis.applib.services.tablecol;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

public interface TableColumnOrderService {

    @Programmatic
    List<String> orderParented(
            final Object parent,
            final String collectionId,
            final Class<?> collectionType,
            final List<String> propertyIds);

    @Programmatic
    List<String> orderStandalone(
            final Class<?> collectionType,
            final List<String> propertyIds);

    /**
     * Used as a fallback.
     */
    @DomainService(
            nature = NatureOfService.DOMAIN,
            menuOrder = "" + Integer.MAX_VALUE
    )
    public static class Default implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {
            return propertyIds;
        }

        @Override
        public List<String> orderStandalone(
                final Class<?> collectionType,
                final List<String> propertyIds) {
            return propertyIds;
        }
    }
}

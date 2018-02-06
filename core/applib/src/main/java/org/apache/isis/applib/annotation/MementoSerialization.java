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
package org.apache.isis.applib.annotation;

/**
 * Whether the property or collection is included if the domain object is serialized into a memento.
 */
public enum MementoSerialization {
    /**
     * Property or collection is included in any mementos.
     * This is the fallback/default if not explicitly excluded.
     */
    INCLUDED,
    /**
     * Property or collection's state is excluded from any mementos.
     *
     * <p>
     *     Corresponds to <tt>@Property(notPersisted=true)</tt> or <tt>@Collection(notPersisted=true)</tt> prior to Isis 2.x
     * </p>
     */
    EXCLUDED,
    /**
     * Ignore the value provided by this annotation (meaning that the framework will keep searching, in meta
     * annotations or superclasses/interfaces).
     */
    NOT_SPECIFIED
}

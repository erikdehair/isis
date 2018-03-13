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

package org.apache.isis.core.metamodel.postprocessors.param;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facetapi.FacetHolder;
import org.apache.isis.core.metamodel.facets.param.defaults.ActionParameterDefaultsFacetAbstract;

public class ActionParameterDefaultsFacetFromAssociatedCollection extends ActionParameterDefaultsFacetAbstract {

    private static ThreadLocal<List<Object>> selectedPojos = new ThreadLocal<List<Object>>() {
        @Override protected List<Object> initialValue() {
            return Collections.emptyList();
        }
    };

    public interface SerializableRunnable<T> extends Callable<T>, Serializable {}

    public static <T> T withSelected(final List<Object> objects, final SerializableRunnable<T> callable) {
        try {
            selectedPojos.set(objects);
            return callable.call();
        } catch (Exception e) {
            throw new ApplicationException(e);
        } finally {
            selectedPojos.set(Collections.emptyList());
        }
    }

    public ActionParameterDefaultsFacetFromAssociatedCollection(final FacetHolder holder) {
        super(holder);
    }

    @Override
    public Object getDefault(final ObjectAdapter target, List<ObjectAdapter> argumentsIfAvailable) {
        return selectedPojos.get();
    }


}

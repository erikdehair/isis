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
package org.apache.isis.viewer.wicket.ui.components.widgets.select2;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.wicketstuff.select2.Select2MultiChoice;

import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;
import org.apache.isis.viewer.wicket.ui.components.widgets.select2.providers.EmptyChoiceProvider;

public class Select2MultiChoiceExt
        extends Select2MultiChoice<ObjectAdapterMemento>
        implements ChoiceExt {

    public static Select2MultiChoiceExt create(
            final String id,
            final IModel<ArrayList<ObjectAdapterMemento>> modelObject,
            final ScalarModel scalarModel) {

        // TODO: naughty..
        final IModel<Collection<ObjectAdapterMemento>> modelObjectColl = (IModel) modelObject;

        return new Select2MultiChoiceExt(id, modelObjectColl, scalarModel);
    }

    private final ObjectSpecId specId;

    Select2MultiChoiceExt(
            final String id,
            final IModel<Collection<ObjectAdapterMemento>> model,
            final ScalarModel scalarModel) {
        super(id, model, EmptyChoiceProvider.INSTANCE);
        specId = scalarModel.getTypeOfSpecification().getSpecId();

        getSettings().setCloseOnSelect(true);

        setOutputMarkupPlaceholderTag(true);
    }

    @Override
    public ObjectSpecId getSpecId() {
        return specId;
    }
}

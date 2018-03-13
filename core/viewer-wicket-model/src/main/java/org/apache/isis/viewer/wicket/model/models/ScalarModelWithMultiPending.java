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
package org.apache.isis.viewer.wicket.model.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;

/**
 * For widgets that use a <tt>org.wicketstuff.select2.Select2MultiChoice</tt>;
 * synchronizes the {@link Model} of the <tt>Select2MultiChoice</tt>
 * with the parent {@link ScalarModel}, allowing also for pending values.
 */
public interface ScalarModelWithMultiPending extends Serializable {

    public ArrayList<ObjectAdapterMemento> getMultiPending();
    public void setMultiPending(ArrayList<ObjectAdapterMemento> pending);

    public ScalarModel getScalarModel();

    static class Util {

        private static final Logger LOG = LoggerFactory.getLogger(ScalarModelWithMultiPending.Util.class);

        public static IModel<ArrayList<ObjectAdapterMemento>> createModel(final ScalarModel model) {
            return createModel(model.asScalarModelWithMultiPending());
        }

        public static Model<ArrayList<ObjectAdapterMemento>> createModel(final ScalarModelWithMultiPending owner) {
            return new Model<ArrayList<ObjectAdapterMemento>>() {

                private static final long serialVersionUID = 1L;

                @Override
                public ArrayList<ObjectAdapterMemento> getObject() {
                    final ArrayList<ObjectAdapterMemento> pending = owner.getMultiPending();
                    if (pending != null) {
                        LOG.debug("pending not null: {}", pending.toString());
                        return pending;
                    }
                    LOG.debug("pending is null");

                    final ScalarModel scalarModel = owner.getScalarModel();
                    final ObjectAdapterMemento objectAdapterMemento = scalarModel.getObjectAdapterMemento();
                    final ArrayList<ObjectAdapterMemento> mementos =
                            objectAdapterMemento != null ? objectAdapterMemento.getList() : null;
                    return mementos;
                }

                @Override
                public void setObject(final ArrayList<ObjectAdapterMemento> adapterMemento) {
                    LOG.debug("setting to: {}", (adapterMemento != null ? adapterMemento.toString() : null));
                    owner.setMultiPending(adapterMemento);

                    final ScalarModel ownerScalarModel = owner.getScalarModel();
                    final PersistenceSession persistenceSession = ownerScalarModel.getPersistenceSession();
                    final SpecificationLoader specificationLoader = ownerScalarModel.getSpecificationLoader();

                    if(adapterMemento == null) {
                        ownerScalarModel.setObject(null);
                    } else {
                        final ArrayList<ObjectAdapterMemento> ownerPending = owner.getMultiPending();
                        if (ownerPending != null) {
                            LOG.debug("setting to pending: {}", ownerPending.toString());
                            final ObjectSpecId objectSpecId = ownerScalarModel.getTypeOfSpecification().getSpecId();
                            ownerScalarModel.setObjectMemento(
                                    ObjectAdapterMemento.createForList(adapterMemento, objectSpecId),
                                    persistenceSession, specificationLoader);
                        }
                    }
                }
            };
        }

    }
}
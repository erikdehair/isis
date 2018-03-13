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
package org.apache.isis.objectstore.jdo.datanucleus.persistence.commands;

import javax.jdo.PersistenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.runtime.persistence.objectstore.transaction.DestroyObjectCommand;
import org.apache.isis.core.runtime.persistence.objectstore.transaction.PersistenceCommandContext;

public class DataNucleusDeleteObjectCommand extends AbstractDataNucleusObjectCommand implements DestroyObjectCommand {

    private static final Logger LOG = LoggerFactory.getLogger(DataNucleusDeleteObjectCommand.class);

    public DataNucleusDeleteObjectCommand(ObjectAdapter adapter, PersistenceManager persistenceManager) {
        super(adapter, persistenceManager);
    }

    @Override
    public void execute(final PersistenceCommandContext context) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("destroy object - executing command for {}", onAdapter());
        }
        getPersistenceManager().deletePersistent(onAdapter().getObject());
    }

    @Override
    public String toString() {
        return "DestroyObjectCommand [adapter=" + onAdapter() + "]";
    }

}

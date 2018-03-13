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
package org.apache.isis.core.metamodel.services.jdosupport;

import java.io.IOException;

import javax.jdo.PersistenceManagerFactory;
import javax.jdo.metadata.TypeMetadata;
import javax.xml.bind.JAXBException;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport0;
import org.apache.isis.applib.value.Clob;
import org.datanucleus.enhancement.Persistable;

@Mixin(method = "act")
public class Persistable_downloadJdoMetadata {

    private final Persistable persistable;

    public Persistable_downloadJdoMetadata(final Persistable persistable) {
        this.persistable = persistable;
    }

    public static class ActionDomainEvent extends org.apache.isis.applib.IsisApplibModule.ActionDomainEvent<Persistable_downloadJdoMetadata> {}

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.SAFE,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-download",
            position = ActionLayout.Position.PANEL_DROPDOWN
    )
    @MemberOrder(name = "datanucleusIdLong", sequence = "710.1")
    public Clob act(
            @ParameterLayout(named = "File name")
            final String fileName) throws JAXBException, IOException {

        final Class<? extends Persistable> objClass = persistable.getClass();
        final String objClassName = objClass.getName();

        final TypeMetadata metadata = getPersistenceManagerFactory().getMetadata(objClassName);
        final String xml = metadata.toString();

        return new Clob(Util.withSuffix(fileName, "jdo"), "text/xml", xml);
    }

    public String default0Act() {
        return Util.withSuffix(persistable.getClass().getName(), "jdo");
    }

    PersistenceManagerFactory getPersistenceManagerFactory() {
        return jdoSupport.getJdoPersistenceManager().getPersistenceManagerFactory();
    }

    @javax.inject.Inject
    IsisJdoSupport0 jdoSupport;

}

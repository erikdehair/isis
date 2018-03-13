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

package org.apache.isis.core.runtime.services.publish;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PublishingChangeKind;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.command.Command;
import org.apache.isis.applib.services.command.CommandContext;
import org.apache.isis.applib.services.iactn.Interaction;
import org.apache.isis.applib.services.iactn.InteractionContext;
import org.apache.isis.applib.services.metrics.MetricsService;
import org.apache.isis.applib.services.publish.PublishedObjects;
import org.apache.isis.applib.services.publish.PublisherService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.facets.object.publishedobject.PublishedObjectFacet;
import org.apache.isis.core.metamodel.services.publishing.PublishingServiceInternal;
import org.apache.isis.core.runtime.services.changes.ChangedObjectsServiceInternal;

/**
 * Wrapper around {@link PublisherService}.  Is a no-op if there is no injected service.
 */
@DomainService(
        nature = NatureOfService.DOMAIN,
        menuOrder = "" + Integer.MAX_VALUE
)
@RequestScoped
public class PublishingServiceInternalDefault implements PublishingServiceInternal {


    @Override
    @Programmatic
    public void publishObjects() {

        if(suppress) {
            return;
        }

        // take a copy of enlisted adapters ... the JDO implementation of the PublishingService
        // creates further entities which would be enlisted; taking copy of the map avoids ConcurrentModificationException

        final Map<ObjectAdapter, PublishingChangeKind> changeKindByEnlistedAdapter = Maps.newHashMap();
        changeKindByEnlistedAdapter.putAll(changedObjectsServiceInternal.getChangeKindByEnlistedAdapter());

        final Map<ObjectAdapter, PublishingChangeKind> changeKindByPublishedAdapter =
                Maps.filterKeys(
                        changeKindByEnlistedAdapter,
                        isPublished());

        if(changeKindByPublishedAdapter.isEmpty()) {
            return;
        }

        final int numberLoaded = metricsService.numberObjectsLoaded();
        final int numberObjectPropertiesModified = changedObjectsServiceInternal.numberObjectPropertiesModified();
        final PublishedObjects publishedObjects = newPublishedObjects(numberLoaded, numberObjectPropertiesModified,
                changeKindByPublishedAdapter);

        for (PublisherService publisherService : publisherServices) {
            publisherService.publish(publishedObjects);
        }
    }

    private PublishedObjects newPublishedObjects(
            final int numberLoaded,
            final int numberObjectPropertiesModified,
            final Map<ObjectAdapter, PublishingChangeKind> changeKindByPublishedAdapter) {

        final Command command = commandContext.getCommand();
        final UUID transactionUuid = command.getTransactionId();

        final String userName = userService.getUser().getName();
        final Timestamp timestamp = clockService.nowAsJavaSqlTimestamp();

        final Interaction interaction = interactionContext.getInteraction();

        final int nextEventSequence = interaction.next(Interaction.Sequence.INTERACTION.id());

        return new PublishedObjectsDefault(transactionUuid, nextEventSequence, userName, timestamp, numberLoaded, numberObjectPropertiesModified, changeKindByPublishedAdapter);
    }



    @Programmatic
    public void publishAction(
            final Interaction.Execution execution) {

        if(suppress) {
            return;
        }

        publishToPublisherServices(execution);
    }


    @Override
    public void publishProperty(
            final Interaction.Execution execution) {

        if(suppress) {
            return;
        }

        publishToPublisherServices(execution);
    }


    private void publishToPublisherServices(final Interaction.Execution<?,?> execution) {

        if(publisherServices == null || publisherServices.isEmpty()) {
            return;
        }

        for (final PublisherService publisherService : publisherServices) {
            publisherService.publish(execution);
        }
    }


    boolean suppress;

    @Programmatic
    @Override
    public <T> T withPublishingSuppressed(final Block<T> block) {
        try {
            suppress = true;
            return block.exec();
        } finally {
            suppress = false;
        }
    }


    private static Predicate<ObjectAdapter> isPublished() {
        return new Predicate<ObjectAdapter>() {
            @Override
            public boolean apply(final ObjectAdapter objectAdapter) {
                final PublishedObjectFacet publishedObjectFacet =
                        objectAdapter.getSpecification().getFacet(PublishedObjectFacet.class);
                return publishedObjectFacet != null;
            }
        };
    }

    // -- injected services
    @javax.inject.Inject
    List<PublisherService> publisherServices;

    @javax.inject.Inject
    ChangedObjectsServiceInternal changedObjectsServiceInternal;

    @javax.inject.Inject
    CommandContext commandContext;

    @javax.inject.Inject
    InteractionContext interactionContext;

    @javax.inject.Inject
    ClockService clockService;

    @javax.inject.Inject
    UserService userService;

    @javax.inject.Inject
    MetricsService metricsService;

    

}

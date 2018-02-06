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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.isis.applib.services.eventbus.ObjectCreatedEvent;
import org.apache.isis.applib.services.eventbus.ObjectLoadedEvent;
import org.apache.isis.applib.services.eventbus.ObjectPersistedEvent;
import org.apache.isis.applib.services.eventbus.ObjectPersistingEvent;
import org.apache.isis.applib.services.eventbus.ObjectRemovingEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatingEvent;

/**
 * Domain semantics for domain objects (entities and view models; for services see {@link org.apache.isis.applib.annotation.DomainService}).
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainObject {

    /**
     * Whether the entity should be audited (note: does not apply to view models or other recreatable objects.
     *
     * <p>
     * Requires that an implementation of the {@link org.apache.isis.applib.services.audit.AuditerService} is
     * registered with the framework.
     * </p>
     */
    Auditing auditing() default Auditing.NOT_SPECIFIED;


    // //////////////////////////////////////


    /**
     * Whether changes to the object should be published.
     *
     * <p>
     * Requires that an implementation of the {@link org.apache.isis.applib.services.publish.PublisherService} is
     * registered with the framework.
     * </p>
     */
    Publishing publishing() default Publishing.NOT_SPECIFIED;


    // //////////////////////////////////////


    /**
     * The class of the domain service that provides an <code>autoComplete(String)</code> method.
     *
     * <p>
     * It is sufficient to specify an interface rather than a concrete type.
     */
    Class<?> autoCompleteRepository() default Object.class;


    /**
     * The method (despite its name, not necessarily an action) to use in order to perform the auto-complete search
     * (defaults to &quot;autoComplete&quot;).
     *
     * <p>
     * The method is required to accept a single string parameter, and must return a list of the domain type.
     */
    String autoCompleteAction() default "autoComplete";


    // //////////////////////////////////////


    /**
     * Indicates that the class has a bounded, or finite, set of instances.
     * 
     * <p>
     *     Takes precedence over auto-complete.
     * </p>
     *
     * <p>
     *     Note: this replaces bounded=true|false prior to v2.x
     * </p>
     *
     */
    Bounding bounding() default Bounding.NOT_SPECIFIED;


    // //////////////////////////////////////


    /**
     * Whether the properties of this domain object can be edited, or collections of this object be added to/removed from.
     *
     * <p>
     *     Note that non-editable objects can nevertheless have actions invoked upon them.
     * </p>
     */
    Editing editing() default Editing.NOT_SPECIFIED;


    /**
     * If {@link #editing()} is set to {@link Editing#DISABLED},
     * then the reason to provide to the user as to why the object's properties cannot be edited/collections modified.
     */
    String editingDisabledReason() default "Disabled";


    // //////////////////////////////////////


    /**
     * Provides a unique abbreviation for the object type, eg &quot;customer.Customer&quot; for Customer.
     *
     * <p>
     * This value, if specified, is used in the serialized form of the object's OID.  An OID is
     * used by the framework to unique identify an object over time (same concept as a URN).
     * </p>
     */
    String objectType() default "";


    // //////////////////////////////////////


    /**
     * The nature of this domain object.
     */
    Nature nature() default Nature.NOT_SPECIFIED;



    /**
     * Equivalent to {@link Mixin#method()}.
     *
     * <p>
     *     Applicable only if {@link #nature()} is {@link Nature#MIXIN}.
     * </p>
     */
    String mixinMethod() default Mixin.DEFAULT_METHOD_NAME;

    // //////////////////////////////////////


    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectCreatedEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectCreatedEvent<?>> createdLifecycleEvent() default ObjectCreatedEvent.Default.class;

    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectPersistingEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectPersistingEvent<?>> persistingLifecycleEvent() default ObjectPersistingEvent.Default.class;

    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectPersistedEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectPersistedEvent<?>> persistedLifecycleEvent() default ObjectPersistedEvent.Default.class;

    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectLoadedEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectLoadedEvent<?>> loadedLifecycleEvent() default ObjectLoadedEvent.Default.class;

    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectUpdatingEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectUpdatingEvent<?>> updatingLifecycleEvent() default ObjectUpdatingEvent.Default.class;

    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectUpdatedEvent<?>> updatedLifecycleEvent() default ObjectUpdatedEvent.Default.class;


    /**
     * Indicates that the loading of the domain object should be posted to the
     * {@link org.apache.isis.applib.services.eventbus.EventBusService event bus} using a custom (subclass of)
     * {@link org.apache.isis.applib.services.eventbus.ObjectRemovingEvent}.
     *
     * <p>
     * This subclass must provide a no-arg constructor; the fields are set reflectively.
     * </p>
     */
    Class<? extends ObjectRemovingEvent<?>> removingLifecycleEvent() default ObjectRemovingEvent.Default.class;
}
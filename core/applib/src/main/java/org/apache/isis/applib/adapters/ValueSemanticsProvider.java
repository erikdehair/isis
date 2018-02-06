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

package org.apache.isis.applib.adapters;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.Defaulted;
import org.apache.isis.applib.annotation.Encodable;
import org.apache.isis.applib.annotation.EqualByContent;
import org.apache.isis.applib.annotation.Parseable;
import org.apache.isis.applib.annotation.Value;

/**
 * Provides a mechanism for providing a set of value semantics.
 * 
 * <p>
 * As explained in the Javadoc of the {@link Value} annotation, value semantics
 * only actually implies that the type is {@link Aggregated aggregated}.
 * However, values are very often also {@link Parseable}, {@link Encodable},
 * {@link Immutable} and implement {@link EqualByContent} semantics. In
 * addition, there may be a {@link Defaulted default value}.
 * 
 * <p>
 * This interface is used by {@link Value} to allow these semantics to be
 * provided through a single point. Alternatively, {@link Value} supports this
 * information being provided via the configuration files.
 * 
 * <p>
 * Whatever the class that implements this interface, it must also expose either
 * a <tt>public</tt> no-arg constructor, or (for implementations that also are
 * <tt>Facet</tt>s) a <tt>public</tt> constructor that accepts a
 * <tt>FacetHolder</tt>, and <tt>IsisConfiguration</tt> and a
 * <tt>ValueSemanticsProviderContext</tt>. This constructor is then used by the
 * framework to instantiate the object reflectively.
 * 
 * @see Parser
 * @see EncoderDecoder
 * @see DefaultsProvider
 */
public interface ValueSemanticsProvider<T> {

    /**
     * The {@link Parser}, if any.
     * 
     * <p>
     * If not <tt>null</tt>, implies that the value is {@link Parseable}.
     */
    Parser<T> getParser();

    /**
     * The {@link EncoderDecoder}, if any.
     * 
     * <p>
     * If not <tt>null</tt>, implies that the value is {@link Encodable}.
     */
    EncoderDecoder<T> getEncoderDecoder();

    /**
     * The {@link DefaultsProvider}, if any.
     * 
     * <p>
     * If not <tt>null</tt>, implies that the value has (or may have) a default.
     */
    DefaultsProvider<T> getDefaultsProvider();

    /**
     * Whether the value is {@link Immutable}.
     */
    boolean isImmutable();

    /**
     * Whether the value has {@link EqualByContent equal by content} semantics.
     * 
     * <p>
     * If so, then it must implement <tt>equals(Object)</tt> and
     * <tt>hashCode()</tt> consistently. Examples in the Java language that do
     * this are {@link String} and {@link BigDecimal}, for example.
     */
    boolean isEqualByContent();

}

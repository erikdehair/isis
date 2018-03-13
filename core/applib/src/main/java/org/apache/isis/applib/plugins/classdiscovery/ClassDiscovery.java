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

package org.apache.isis.applib.plugins.classdiscovery;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

/**
 * Provides search-methods on class hierarchies.
 * 
 * @since 2.0.0
 */
public interface ClassDiscovery {

	//TODO missing java-doc
	public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation);
	
	//TODO missing java-doc
	public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type);
	
	public static ClassDiscovery empty() {
		
		return new ClassDiscovery() {
			
			@Override
			public Set<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> annotation) {
				return Collections.emptySet();
			}
			
			@Override
			public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
				return Collections.emptySet();
			}
		};
	}
	
}

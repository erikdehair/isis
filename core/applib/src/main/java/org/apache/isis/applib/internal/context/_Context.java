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

package org.apache.isis.applib.internal.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

import org.apache.isis.applib.internal.base._Casts;
import org.apache.isis.applib.internal.base._NullSafe;
import org.apache.isis.applib.internal.collections._Lists;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Provides a context for storing and retrieving singletons (usually application scoped). 
 * Writes to the store are implemented thread-safe. 
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/> 
 * These may be changed or removed without notice!
 * </p>
 * @since 2.0.0
 */
public final class _Context {

	private _Context(){}
	
	/**
	 * Thread-safety note: We let threads synchronize on writes to the singletonMap, 
	 * NOT on reads.<br/>
	 * If there is a race-condition between a writing and a reading thread, by design 
	 * the first one wins.<br/> 
	 * If synchronization is required it should happen elsewhere, not here!<br/>
	 */
	private final static Map<String, Object> singletonMap = new HashMap<>(); 

	/**
	 * Puts a singleton instance onto the current context.
	 * @param type non-null
	 * @param singleton non-null
	 * @throws IllegalStateException if there is already an instance of same {@code type}
	 *  on the current context.
	 */
	public static <T> void putSingleton(Class<? super T> type, T singleton) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(singleton);
		
		// let writes to the map be atomic
		synchronized (singletonMap) {   
			if(singletonMap.containsKey(toKey(type)))
				throw new IllegalStateException(
						"there is already a singleton of type '"+type+"' on this context.");
			singletonMap.put(toKey(type), singleton);	
		}
	}
	
	/**
	 * Puts a singleton instance onto the current context, that 
	 * either overrides any already present or ignores the call depending on {@code override}.
	 * @param type non-null
	 * @param singleton non-null
	 * @param override whether to overrides any already present singleton or not
	 * @return whether the {@code singleton} was put on the context or ignored because there is already one present 
	 */
	public static <T> boolean put(Class<? super T> type, T singleton, boolean override) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(singleton);
		
		// let writes to the map be atomic
		synchronized (singletonMap) {   
			if(singletonMap.containsKey(toKey(type))) {
				if(!override)
					return false;
			}
			singletonMap.put(toKey(type), singleton);
			return true;
		}
	}
	

	/**
	 * Gets a singleton instance of {@code type} if there is any, null otherwise.
	 * @param type non-null
	 * @return null, if there is no such instance
	 */
	public static <T> T getIfAny(Class<? super T> type) {
		return _Casts.uncheckedCast(singletonMap.get(toKey(type)));
	}
	
	/**
	 * If the specified key is not already associated with a value (or is mapped to null), 
	 * attempts to compute its value using the given factory function and enters it into this map unless null. 
	 * @param type
	 * @param factory
	 * @return null, if there is no such instance
	 */
	public static <T> T computeIfAbsent(Class<? super T> type, Function<Class<? super T>, T> factory) {
		Objects.requireNonNull(type);
		Objects.requireNonNull(factory);
		
		// let writes to the map be atomic
		synchronized (singletonMap) { 
			return _Casts.uncheckedCast(singletonMap.computeIfAbsent(toKey(type), __->factory.apply(type)));
		}
	}
	
	/**
	 * Gets a singleton instance of {@code type} if there is any, 
	 * otherwise returns the {@code fallback}'s result,
	 * which could be null.
	 * @param type non-null
	 * @param fallback non-null
	 * @return
	 */
	public static <T> T getOrElse(Class<? super T> type, Supplier<T> fallback) {
		Objects.requireNonNull(fallback);
		final T singleton = getIfAny(type);
		if(singleton!=null) {
			return singleton;
		}
		return fallback.get(); 
	}
	
	/**
	 * Gets a singleton instance of {@code type} if there is any, 
	 * otherwise throws the {@code onNotFound}'s result.
	 * @param type non-null
	 * @param onNotFound non-null
	 * @return
	 * @throws Exception 
	 */
	public static <T, E extends Exception> T getOrThrow(
			Class<? super T> type, 
			Supplier<E> onNotFound) 
			throws E {
		Objects.requireNonNull(onNotFound);
		final T singleton = getIfAny(type);
		if(singleton!=null) {
			return singleton;
		}
		throw onNotFound.get();
	}
	
	/**
	 * Removes any singleton references from the current context. <br/> 
	 * Any singletons that implement the AutoClosable interface are being closed.
	 */
	public static void clear() {
		
		// let writes to the map be atomic
		synchronized (singletonMap) {
			
			closeAnyClosables(_Lists.newArrayList(singletonMap.values()));
			
			singletonMap.clear();
		}
	}
	
	private static void closeAnyClosables(List<Object> objects) {
		_NullSafe.stream(objects)
		.filter(singleton->singleton instanceof AutoCloseable)
		.map(singleton->(AutoCloseable)singleton)
		.forEach(autoCloseable->{
			try {
				autoCloseable.close();	
			} catch (Exception e) {
				// [ahuber] nothing we can do here, so ignore
			}
		});
	}
	
	// -- DEFAULT CLASSLOADER
	
	private final static Supplier<ClassLoader> FALLBACK_CLASSLOADER = 
			Thread.currentThread()::getContextClassLoader;
	
	/**
	 * Will be set by the framework's bootstrapping mechanism if required.
	 * @return the default class loader
	 */
	public static @NotNull ClassLoader getDefaultClassLoader() {
		return getOrElse(ClassLoader.class, FALLBACK_CLASSLOADER);
	}
	
	// -- CLASS LOADING SHORTCUTS
	
	/**
	 * Uses the frameworks default-ClassLoader to load a class by name.
	 * @param className
	 * @return class by name
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException{
		return getDefaultClassLoader().loadClass(className);
	}

	/**
	 * Uses the frameworks default-ClassLoader to load and initialize a class by name.<br/>
	 * <b>Initialize</b> the class, that is, all static initializers will be run. <br/>
	 * (For details on initialize see Section 12.4 of The Java Language Specification)
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClassAndInitialize(String className) throws ClassNotFoundException{
		return Class.forName(className, true, getDefaultClassLoader());
	}
	
	
	// -- HELPER
	
	private static String toKey(Class<?> type) {
		return type.getName();
	}

	
}

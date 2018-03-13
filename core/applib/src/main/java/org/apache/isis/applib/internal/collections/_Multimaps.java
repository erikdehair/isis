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

package org.apache.isis.applib.internal.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * <h1>- internal use only -</h1>
 * <p>
 * Provides Map-of-List and Map-of-Set implementations
 * </p>
 * <p>
 * <b>WARNING</b>: Do <b>NOT</b> use any of the classes provided by this package! <br/> 
 * These may be changed or removed without notice!
 * </p>
 * 
 * @since 2.0.0
 */
public class _Multimaps {
	
	/**
	 * Represents a Map of Lists.
	 * @param <K>
	 * @param <V>
	 */
	public static interface ListMultimap<K, V> extends Map<K, List<V>> {
		/**
		 * Adds {@code value} to the List stored under {@code key}.
		 * (If no such List exists, a new List is created.) 
		 * @param key
		 * @param value
		 */
		public void putElement(K key, V value);
	}
	
	/**
	 * Represents a Map of Sets.
	 * @param <K>
	 * @param <V>
	 */
	public static interface SetMultimap<K, V> extends Map<K, Set<V>> {
		/**
		 * Adds {@code value} to the Set stored under {@code key}.
		 * (If no such Set exists, a new Set is created.) 
		 * @param key
		 * @param value
		 */
		public void putElement(K key, V value);
	}
	
	/**
	 * Represents a Map of Maps. 
	 * @param <K1>
	 * @param <K2>
	 * @param <V>
	 */
	public static interface MapMultimap<K1, K2, V> extends Map<K1, Map<K2, V>> {
		
		/**
		 * Puts {@code value} into the element-map stored under {@code key}.
		 * (If no such Map exists, a new Map is created.)
		 * @param key
		 * @param subkey the key into the element-map where to store the {@code value}
		 * @param value
		 */
		public void putElement(K1 key, K2 subkey, V value);
		
		/**
		 * Get the element-map by {@code key}, then within the element-map looks up to {@code subkey}.
		 * @param key
		 * @param subkey
		 * @return null if no such element exists
		 */
		public V getElement(K1 key, K2 subkey);
	}
	
	public static <K, V> ListMultimap<K, V> newListMultimap(
			final Supplier<Map<K, List<V>>> mapFactory,
			final Supplier<List<V>> elementCollectionFactory){
		Objects.requireNonNull(mapFactory);
		Objects.requireNonNull(elementCollectionFactory);
		
		return new ListMultimap<K, V>() {
			
			final Map<K, List<V>> delegate = mapFactory.get();

			@Override public int size() { return delegate.size(); }
			@Override public boolean isEmpty() { return delegate.isEmpty();	}
			@Override public boolean containsKey(Object key) { return delegate.containsKey(key); }
			@Override public boolean containsValue(Object value) { return delegate.containsValue(value); }
			@Override public List<V> get(Object key) { return delegate.get(key); }
			@Override public List<V> put(K key, List<V> value) { return delegate.put(key, value); }
			@Override public List<V> remove(Object key) { return delegate.remove(key); }
			@Override public void putAll(Map<? extends K, ? extends List<V>> m) {	delegate.putAll(m);	}
			@Override public void clear() {	delegate.clear(); }
			@Override public Set<K> keySet() { return delegate.keySet(); }
			@Override public Collection<List<V>> values() { return delegate.values();	}
			@Override public Set<Entry<K, List<V>>> entrySet() { return delegate.entrySet(); }
			
			@Override 
			public void putElement(K key, V value) {
				final Collection<V> collection = delegate.computeIfAbsent(key, __->elementCollectionFactory.get());
				collection.add(value);
			}

		};
	}
	
	public static <K, V> SetMultimap<K, V> newSetMultimap(
			final Supplier<Map<K, Set<V>>> mapFactory,
			final Supplier<Set<V>> elementCollectionFactory){
		Objects.requireNonNull(mapFactory);
		Objects.requireNonNull(elementCollectionFactory);
		
		return new SetMultimap<K, V>() {
			
			final Map<K, Set<V>> delegate = mapFactory.get();

			@Override public int size() { return delegate.size(); }
			@Override public boolean isEmpty() { return delegate.isEmpty();	}
			@Override public boolean containsKey(Object key) { return delegate.containsKey(key); }
			@Override public boolean containsValue(Object value) { return delegate.containsValue(value); }
			@Override public Set<V> get(Object key) { return delegate.get(key); }
			@Override public Set<V> put(K key, Set<V> value) { return delegate.put(key, value); }
			@Override public Set<V> remove(Object key) { return delegate.remove(key); }
			@Override public void putAll(Map<? extends K, ? extends Set<V>> m) {	delegate.putAll(m);	}
			@Override public void clear() {	delegate.clear(); }
			@Override public Set<K> keySet() { return delegate.keySet(); }
			@Override public Collection<Set<V>> values() { return delegate.values();	}
			@Override public Set<Entry<K, Set<V>>> entrySet() { return delegate.entrySet(); }
			
			@Override 
			public void putElement(K key, V value) {
				final Collection<V> collection = delegate.computeIfAbsent(key, __->elementCollectionFactory.get());
				collection.add(value);
			}

		};
	}
	
	public static <K1, K2, V> MapMultimap<K1, K2, V> newMapMultimap(
			final Supplier<Map<K1, Map<K2, V>>> mapFactory,
			final Supplier<Map<K2, V>> elementMapFactory){
		Objects.requireNonNull(mapFactory);
		Objects.requireNonNull(elementMapFactory);
		
		return new MapMultimap<K1, K2, V>() {
			
			final Map<K1, Map<K2, V>> delegate = mapFactory.get();

			@Override public int size() { return delegate.size(); }
			@Override public boolean isEmpty() { return delegate.isEmpty();	}
			@Override public boolean containsKey(Object key) { return delegate.containsKey(key); }
			@Override public boolean containsValue(Object value) { return delegate.containsValue(value); }
			@Override public Map<K2, V> get(Object key) { return delegate.get(key); }
			@Override public Map<K2, V> put(K1 key, Map<K2, V> value) { return delegate.put(key, value); }
			@Override public Map<K2, V> remove(Object key) { return delegate.remove(key); }
			@Override public void putAll(Map<? extends K1, ? extends Map<K2, V>> m) {	delegate.putAll(m);	}
			@Override public void clear() {	delegate.clear(); }
			@Override public Set<K1> keySet() { return delegate.keySet(); }
			@Override public Collection<Map<K2, V>> values() { return delegate.values();	}
			@Override public Set<Entry<K1, Map<K2, V>>> entrySet() { return delegate.entrySet(); }
			
			@Override 
			public void putElement(K1 key, K2 subkey, V value) {
				final Map<K2, V> elementMap = delegate.computeIfAbsent(key, __->elementMapFactory.get());
				elementMap.put(subkey, value);
			}
			
			@Override 
			public V getElement(K1 key, K2 subkey) {
				final Map<K2, V> elementMap = delegate.get(key);
				return elementMap!=null ? elementMap.get(subkey) : null;
			}

		};
	}
	
	// -- CONVENIENT DEFAULTS
	
	/**
	 * @return HashMap of ArrayLists
	 */
	public static <K, V> ListMultimap<K, V> newListMultimap(){ 
		return newListMultimap(HashMap<K, List<V>>::new, ArrayList::new);
	}
	
	/**
	 * @return HashMap of HashSets
	 */
	public static <K, V> SetMultimap<K, V> newSetMultimap(){ 
		return newSetMultimap(HashMap<K, Set<V>>::new, HashSet::new);
	}

	/**
	 * @return HashMap of HashMaps
	 */
	public static <K1, K2, V> MapMultimap<K1, K2, V> newMapMultimap(){ 
		return newMapMultimap(HashMap<K1, Map<K2, V>>::new, HashMap::new);
	}

	
	
}

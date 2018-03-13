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
package org.apache.isis.applib.query;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class QueryDefaultTest_withStart_or_withCount {
    
    private QueryDefault<Customer> queryDefault;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    
    static class Customer {}
    
    @Before
    public void setUp() throws Exception {
        queryDefault = new QueryDefault<Customer>(Customer.class, "findByLastName", "lastName", "Smith");
    }

    @Test
    public void defaults() throws Exception {
        assertThat(queryDefault.getStart(), is(0L));
        assertThat(queryDefault.getCount(), is(0L));
    }


    @Test
    public void typicalHappyCase() throws Exception {
        final QueryDefault<Customer> q = queryDefault.withStart(10L).withCount(5L);
        
        assertThat(q, is(queryDefault));
        assertThat(q.getStart(), is(10L));
        assertThat(q.getCount(), is(5L));
    }

    @Test
    public void happyCase_startOnly() throws Exception {
        final QueryDefault<Customer> q = queryDefault.withStart(10L);
        
        assertThat(q, is(queryDefault));
        assertThat(q.getStart(), is(10L));
        assertThat(q.getCount(), is(0L));
    }

    @Test
    public void happyCase_startZero() throws Exception {
        final QueryDefault<Customer> q = queryDefault.withStart(0);
        
        assertThat(q, is(queryDefault));
        assertThat(q.getStart(), is(0L));
    }

    @Test
    public void startNegative() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        queryDefault.withStart(-1);
    }

    @Test
    public void happyCase_countOnly() throws Exception {
        final QueryDefault<Customer> q = queryDefault.withCount(20L);
        
        assertThat(q, is(queryDefault));
        assertThat(q.getStart(), is(0L));
        assertThat(q.getCount(), is(20L));
    }

    @Test
    public void countNegative() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        queryDefault.withCount(-1);
    }

    @Test
    public void countZero() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        queryDefault.withCount(0);
    }


}

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
package org.apache.isis.applib.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectContractsLegacyTest_equals {

    static class Invoice4 {
        private static final String KEY_PROPERTIES = "number";

        private String number;
        public String getNumber() {
            return number;
        }
        public void setNumber(String number) {
            this.number = number;
        }
        @Override
        public int hashCode() {
            return ObjectContractsLegacy.hashCode(this, KEY_PROPERTIES);
        }
        @Override
        public boolean equals(Object obj) {
            return ObjectContractsLegacy.equals(this, obj, KEY_PROPERTIES);
        }

    }

    private Invoice4 p;
    private Invoice4 q;
    private Invoice4 r;
    private String x;

    @Before
    public void setUp() throws Exception {
        p = new Invoice4();
        p.setNumber("123");
        q = new Invoice4();
        q.setNumber("123");
        r = new Invoice4();
        r.setNumber("456");

        x = "this is not an invoice";
    }

    @Test
    public void happyCase() throws Exception {
        assertTrue(ObjectContractsLegacy.equals(p, q, "number"));
    }

    @Test
    public void nullsAreEqual() throws Exception {
        assertTrue(ObjectContractsLegacy.equals(null, null, "number"));
    }

    @Test
    public void notEqualDifferentValues() throws Exception {
        assertFalse(ObjectContractsLegacy.equals(p, r, "number"));
    }

    @Test
    public void notEqualDifferentTypes() throws Exception {
        assertFalse(ObjectContractsLegacy.equals(p, x, "number"));
    }

    @Test
    public void notEqualNull() throws Exception {
        assertFalse(ObjectContractsLegacy.equals(p, null, "number"));
    }


}

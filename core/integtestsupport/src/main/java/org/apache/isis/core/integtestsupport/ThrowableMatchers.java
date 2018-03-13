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
package org.apache.isis.core.integtestsupport;

import java.util.List;

import org.apache.isis.applib.internal.exceptions._Exceptions;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ThrowableMatchers {

    ThrowableMatchers(){}

    public static TypeSafeMatcher<Throwable> causedBy(final Class<?> type) {
        return new TypeSafeMatcher<Throwable>() {
            @Override
            protected boolean matchesSafely(final Throwable throwable) {
            	final List<Throwable> causalChain = _Exceptions.getCausalChain(throwable); // non null result
            	
            	// legacy of return !FluentIterable.from(causalChain).filter(type).isEmpty();
            	
                return causalChain.stream().filter(t->t.getClass().equals(type)).findAny().isPresent();
            }

            @Override public void describeTo(final Description description) {
                description.appendText("Caused by " + type.getName());
            }
        };
    }

}

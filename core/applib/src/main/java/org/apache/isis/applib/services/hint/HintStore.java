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
package org.apache.isis.applib.services.hint;

import java.util.Set;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.bookmark.Bookmark;

public interface HintStore {

    public static class BookmarkWithHintId extends Bookmark {

        private final String hintId;

        public BookmarkWithHintId(final Bookmark bookmark, final String hintId) {
            super(bookmark.toString());
            this.hintId = hintId;
        }

        public String getHintId() {
            return hintId;
        }

        /**
         * Similar to {@link #toString()}, but using {@link #hintId} rather than {@link #identifier}.
         */
        public String toStringUsingHintId() {
            return state.getCode() + objectType + SEPARATOR + hintId;
        }
    }

    interface HintIdProvider {
        @Programmatic
        String hintId();
    }

    @Programmatic
    String get(final Bookmark bookmark, String hintKey);

    @Programmatic
    void set(final Bookmark bookmark, String hintKey, String value);

    @Programmatic
    void remove(final Bookmark bookmark, String hintKey);

    @Programmatic
    void removeAll(Bookmark bookmark);

    @Programmatic
    Set<String> findHintKeys(Bookmark bookmark);

}

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
package org.apache.isis.schema.utils.jaxbadapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.google.common.base.Strings;

import org.joda.time.LocalTime;

/**
 * Note: not actually registered as a JAXB adapter.
 */
public final class JodaLocalTimeStringAdapter {
    private JodaLocalTimeStringAdapter() {
    }

    public static LocalTime parse(final String localTimeStr) {
        if (Strings.isNullOrEmpty(localTimeStr)) {
            return null;
        }
        return LocalTime.parse(localTimeStr);
    }

    public static String print(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return localTime.toString();
    }

    public static class ForJaxb extends XmlAdapter<String, LocalTime> {

        @Override
        public LocalTime unmarshal(final String localTimeStr) throws Exception {
            return JodaLocalTimeStringAdapter.parse(localTimeStr);
        }

        @Override
        public String marshal(final LocalTime localTime) throws Exception {
            return JodaLocalTimeStringAdapter.print(localTime);
        }
    }

}

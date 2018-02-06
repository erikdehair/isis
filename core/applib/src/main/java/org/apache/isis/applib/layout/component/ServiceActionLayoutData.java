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
package org.apache.isis.applib.layout.component;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.layout.links.Link;

/**
 * Describes the layout of a single action, broadly corresponding to {@link org.apache.isis.applib.annotation.ActionLayout}.
 *
 * <p>
 *  Note that {@link org.apache.isis.applib.annotation.ActionLayout#contributed()} is omitted because this only applies
 *  to domain services.
 * </p>
 */
@XmlRootElement(
        name = "serviceAction"
)
@XmlType(
    name = "serviceAction"
    , propOrder = {
        "objectType"
        , "id"
        , "named"
        , "namedEscaped"
        , "bookmarking"
        , "cssClass"
        , "cssClassFa"
        , "describedAs"
        , "metadataError"
        , "link"
    }
)
public class ServiceActionLayoutData implements Serializable {


    private static final long serialVersionUID = 1L;

    public ServiceActionLayoutData() {
    }
    public ServiceActionLayoutData(final String objectType, final String id) {
        this.objectType = objectType;
        this.id = id;
    }


    private String objectType;

    @XmlAttribute(required = true)
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }



    private String id;
    /**
     * Method name.
     *
     * <p>
     *     Overloaded methods are not supported.
     * </p>
     */
    @XmlAttribute(name="id", required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    private BookmarkPolicy bookmarking;

    @XmlAttribute(required = false)
    public BookmarkPolicy getBookmarking() {
        return bookmarking;
    }

    public void setBookmarking(BookmarkPolicy bookmarking) {
        this.bookmarking = bookmarking;
    }


    private String cssClass;

    @XmlAttribute(required = false)
    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }


    private String cssClassFa;

    @XmlAttribute(required = false)
    public String getCssClassFa() {
        return cssClassFa;
    }

    public void setCssClassFa(String cssClassFa) {
        this.cssClassFa = cssClassFa;
    }




    private String describedAs;

    @XmlElement(required = false)
    public String getDescribedAs() {
        return describedAs;
    }

    public void setDescribedAs(String describedAs) {
        this.describedAs = describedAs;
    }



    private String named;

    @XmlElement(required = false)
    public String getNamed() {
        return named;
    }

    public void setNamed(String named) {
        this.named = named;
    }



    private Boolean namedEscaped;

    @XmlAttribute(required = false)
    public Boolean getNamedEscaped() {
        return namedEscaped;
    }

    public void setNamedEscaped(Boolean namedEscaped) {
        this.namedEscaped = namedEscaped;
    }




    private ServiceActionLayoutDataOwner owner;
    /**
     * Owner.
     *
     * <p>
     *     Set programmatically by framework after reading in from XML.
     * </p>
     */
    @XmlTransient
    public ServiceActionLayoutDataOwner getOwner() {
        return owner;
    }

    public void setOwner(final ServiceActionLayoutDataOwner owner) {
        this.owner = owner;
    }


    private String metadataError;

    /**
     * For diagnostics; populated by the framework if and only if a metadata error.
     */
    @XmlElement(required = false)
    public String getMetadataError() {
        return metadataError;
    }

    public void setMetadataError(final String metadataError) {
        this.metadataError = metadataError;
    }


    private Link link;

    /**
     * The link to access this resource from the REST API (Restful Objects viewer).
     *
     * <p>
     *     Populated by the framework automatically.
     * </p>
     */
    @XmlElement(required = false)
    public Link getLink() {
        return link;
    }

    public void setLink(final Link link) {
        this.link = link;
    }



    @XmlTransient
    public String getObjectTypeAndId() {
        return getObjectType() + "#" + getId();
    }


    @Override
    public String toString() {
        return "ServiceActionLayoutData{" +
                "objectType='" + objectType + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}

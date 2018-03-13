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
package org.apache.isis.applib.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.IsisApplibModule;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.internal.collections._Lists;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntitiesAdapter;

@XmlRootElement(name = "list")
@XmlType(
        propOrder = {
                "title",
                "actionOwningType",
                "actionId",
                "actionArguments",
                "elementObjectType",
                "objects"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(
        objectType = "isisApplib.DomainObjectList",
        editing = Editing.DISABLED,
        nature = Nature.VIEW_MODEL
)
@DomainObjectLayout(
        titleUiEvent = DomainObjectList.TitleUiEvent.class,
        iconUiEvent = DomainObjectList.IconUiEvent.class,
        cssClassUiEvent = DomainObjectList.CssClassUiEvent.class
)
public class DomainObjectList {

    // -- ui event classes
    public static class TitleUiEvent extends IsisApplibModule.TitleUiEvent<DomainObjectList>{}
    public static class IconUiEvent extends IsisApplibModule.IconUiEvent<DomainObjectList>{}
    public static class CssClassUiEvent extends IsisApplibModule.CssClassUiEvent<DomainObjectList>{}
    

    // -- domain event classes
    public static abstract class PropertyDomainEvent<T> extends IsisApplibModule.PropertyDomainEvent<DomainObjectList, T> { }
    public static abstract class CollectionDomainEvent<T> extends IsisApplibModule.CollectionDomainEvent<DomainObjectList, T> { }
    public static abstract class ActionDomainEvent extends IsisApplibModule.ActionDomainEvent<DomainObjectList> { }
    

    // -- constructors
    public DomainObjectList() {
    }
    public DomainObjectList(
            final String title,
            final String elementObjectType,
            final String actionOwningType,
            final String actionId,
            final String actionArguments) {
        this.title = title;
        this.elementObjectType = elementObjectType;
        this.actionOwningType = actionOwningType;
        this.actionId = actionId;
        this.actionArguments = actionArguments;
    }
    

    // -- title
    private String title;
    public String title() {
        return title;
    }
    

    // -- property: elementObjectType
    public static class ElementObjectTypeDomainEvent extends PropertyDomainEvent<String> { }

    private String elementObjectType;
    @Property(
            domainEvent = ElementObjectTypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    public String getElementObjectType() {
        return elementObjectType;
    }
    

    // -- property: actionOwningType
    public static class ActionOwningTypeDomainEvent extends PropertyDomainEvent<String> { }

    private String actionOwningType;

    @Property(
            domainEvent = ActionOwningTypeDomainEvent.class,
            optionality = Optionality.OPTIONAL,
            editing = Editing.DISABLED
    )
    public String getActionOwningType() {
        return actionOwningType;
    }
    

    // -- property: actionId
    public static class ActionIdDomainEvent extends PropertyDomainEvent<String> { }

    private String actionId;

    @Property(
            domainEvent = ActionIdDomainEvent.class,
            optionality = Optionality.OPTIONAL,
            editing = Editing.DISABLED
    )
    public String getActionId() {
        return actionId;
    }
    

    // -- property: actionArguments
    public static class ActionArgumentsDomainEvent extends PropertyDomainEvent<String> { }

    private String actionArguments;

    @Property(
            domainEvent = ActionArgumentsDomainEvent.class,
            optionality = Optionality.OPTIONAL,
            editing = Editing.DISABLED
    )
    public String getActionArguments() {
        return actionArguments;
    }
    

    // -- collection: objects
    public static class ObjectsDomainEvent extends CollectionDomainEvent<Object> { }

    @XmlJavaTypeAdapter(PersistentEntitiesAdapter.class)
    private List<Object> objects = _Lists.newArrayList();

    @Collection(
            domainEvent = ObjectsDomainEvent.class,
            editing = Editing.DISABLED
    )
    public List<Object> getObjects() {
        return objects;
    }

    public void setObjects(final List<Object> objects) {
        this.objects = objects;
    }
    

}

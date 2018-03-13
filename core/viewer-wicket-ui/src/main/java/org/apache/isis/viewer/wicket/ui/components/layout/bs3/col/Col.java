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
package org.apache.isis.viewer.wicket.ui.components.layout.bs3.col;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.isis.applib.internal.base._NullSafe;
import org.apache.isis.applib.layout.component.ActionLayoutData;
import org.apache.isis.applib.layout.component.CollectionLayoutData;
import org.apache.isis.applib.layout.component.DomainObjectLayoutData;
import org.apache.isis.applib.layout.component.FieldSet;
import org.apache.isis.applib.layout.grid.bootstrap3.BS3Col;
import org.apache.isis.applib.layout.grid.bootstrap3.BS3Row;
import org.apache.isis.applib.layout.grid.bootstrap3.BS3Tab;
import org.apache.isis.applib.layout.grid.bootstrap3.BS3TabGroup;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.viewer.wicket.model.links.LinkAndLabel;
import org.apache.isis.viewer.wicket.model.models.EntityModel;
import org.apache.isis.viewer.wicket.ui.ComponentFactory;
import org.apache.isis.viewer.wicket.ui.ComponentType;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.entityactions.AdditionalLinksPanel;
import org.apache.isis.viewer.wicket.ui.components.actionmenu.entityactions.LinkAndLabelUtil;
import org.apache.isis.viewer.wicket.ui.components.entity.fieldset.PropertyGroup;
import org.apache.isis.viewer.wicket.ui.components.layout.bs3.Util;
import org.apache.isis.viewer.wicket.ui.components.layout.bs3.row.Row;
import org.apache.isis.viewer.wicket.ui.components.layout.bs3.tabs.TabGroupPanel;
import org.apache.isis.viewer.wicket.ui.panels.HasDynamicallyVisibleContent;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;
import org.apache.isis.viewer.wicket.ui.util.Components;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

public class Col extends PanelAbstract<EntityModel> implements HasDynamicallyVisibleContent {

    private static final long serialVersionUID = 1L;

    private static final String ID_COL = "col";
    private static final String ID_ENTITY_HEADER_PANEL = "entityHeaderPanel";
    private static final String ID_ROWS = "rows";
    private static final String ID_TAB_GROUPS = "tabGroups";
    private static final String ID_FIELD_SETS = "fieldSets";
    private static final String ID_COLLECTIONS = "collections";

    private final BS3Col bs3Col;

    public Col(
            final String id,
            final EntityModel entityModel, final BS3Col bs3Col) {

        super(id, entityModel);

        this.bs3Col = bs3Col;

        buildGui();
    }

    private void buildGui() {

        setRenderBodyOnly(true);

        if(bs3Col.getSpan() == 0) {
            Components.permanentlyHide(this, ID_COL);
            return;
        }

        final WebMarkupContainer div = new WebMarkupContainer(ID_COL);

        CssClassAppender.appendCssClassTo(div, bs3Col.toCssClass());
        Util.appendCssClass(div, bs3Col, ID_COL);

        // icon/title
        final DomainObjectLayoutData domainObject = bs3Col.getDomainObject();

        final WebMarkupContainer actionOwner;
        final String actionIdToUse;
        final String actionIdToHide;
        if(domainObject != null) {
            final WebMarkupContainer entityHeaderPanel = new WebMarkupContainer(ID_ENTITY_HEADER_PANEL);
            div.add(entityHeaderPanel);
            final ComponentFactory componentFactory =
                    getComponentFactoryRegistry().findComponentFactory(ComponentType.ENTITY_ICON_TITLE_AND_COPYLINK, getModel());
            final Component component = componentFactory.createComponent(getModel());
            entityHeaderPanel.addOrReplace(component);

            actionOwner = entityHeaderPanel;
            actionIdToUse = "entityActions";
            actionIdToHide = "actions";

            visible = true;
        } else {
            Components.permanentlyHide(div, ID_ENTITY_HEADER_PANEL);
            actionOwner = div;
            actionIdToUse = "actions";
            actionIdToHide = null;
        }


        // actions
        // (rendering depends on whether also showing the icon/title)
        final List<ActionLayoutData> actionLayoutDatas = bs3Col.getActions();
        final List<ObjectAction> visibleActions =
            FluentIterable.from(actionLayoutDatas)
                    .filter(new Predicate<ActionLayoutData>() {
                        @Override public boolean apply(final ActionLayoutData actionLayoutData) {
                            return actionLayoutData.getMetadataError() == null;
                        }
                    })
                    .transform(new Function<ActionLayoutData, ObjectAction>() {
                        @Nullable @Override public ObjectAction apply(@Nullable final ActionLayoutData actionLayoutData) {
                            return getModel().getTypeOfSpecification().getObjectAction(actionLayoutData.getId());
                        }
                    })
                    .filter(Predicates.<ObjectAction>notNull())
                    //
                    // visibility needs to be determined at point of rendering, by ActionLink itself
                    //
                    //.filter(new Predicate<ObjectAction>() {
                    //    @Override public boolean apply(@Nullable final ObjectAction objectAction) {
                    //        final Consent visibility = objectAction
                    //                .isVisible(getModel().getObject(), InteractionInitiatedBy.USER, Where.OBJECT_FORMS);
                    //        return visibility.isAllowed();
                    //    }
                    //})
                    .toList();
        final List<LinkAndLabel> entityActionLinks =
                LinkAndLabelUtil.asActionLinksForAdditionalLinksPanel(getModel(), visibleActions, null);

        if (!entityActionLinks.isEmpty()) {
            AdditionalLinksPanel.addAdditionalLinks(actionOwner, actionIdToUse, entityActionLinks, AdditionalLinksPanel.Style.INLINE_LIST);
            visible = true;
        } else {
            Components.permanentlyHide(actionOwner, actionIdToUse);
        }
        if(actionIdToHide != null) {
            Components.permanentlyHide(div, actionIdToHide);
        }



        // rows
        final List<BS3Row> rows = Lists.newArrayList(this.bs3Col.getRows());
        if(!rows.isEmpty()) {
            final RepeatingViewWithDynamicallyVisibleContent rowsRv = buildRows(ID_ROWS, rows);
            div.add(rowsRv);
            visible = visible || rowsRv.isVisible();
        } else {
            Components.permanentlyHide(div, ID_ROWS);
        }


        // tab groups
        final List<BS3TabGroup> tabGroupsWithNonEmptyTabs =
                FluentIterable.from(bs3Col.getTabGroups())
                        .filter(new Predicate<BS3TabGroup>() {
                            @Override public boolean apply(@Nullable final BS3TabGroup bs3TabGroup) {
                                final List<BS3Tab> bs3TabsWithRows = _NullSafe.stream(bs3TabGroup.getTabs())
                                                .filter(BS3Tab.Predicates.notEmpty())
                                                .collect(Collectors.toList());
                                return !bs3TabsWithRows.isEmpty();
                            }
                        }).toList();
        if(!tabGroupsWithNonEmptyTabs.isEmpty()) {
            final RepeatingViewWithDynamicallyVisibleContent tabGroupRv =
                    new RepeatingViewWithDynamicallyVisibleContent(ID_TAB_GROUPS);

            for (BS3TabGroup bs3TabGroup : tabGroupsWithNonEmptyTabs) {

                final String id = tabGroupRv.newChildId();
                final List<BS3Tab> tabs = _NullSafe.stream(bs3TabGroup.getTabs())
                        .filter(BS3Tab.Predicates.notEmpty())
                        .collect(Collectors.toList());

                switch (tabs.size()) {
                case 0:
                    // shouldn't occur; previously have filtered these out
                    throw new IllegalStateException("Cannot render tabGroup with no tabs");
                case 1:
                    if(bs3TabGroup.isCollapseIfOne() == null || bs3TabGroup.isCollapseIfOne()) {
                        final BS3Tab bs3Tab = tabs.get(0);
                        // render the rows of the one-and-only tab of this tab group.
                        final List<BS3Row> tabRows = bs3Tab.getRows();
                        final RepeatingViewWithDynamicallyVisibleContent rowsRv = buildRows(id, tabRows);
                        tabGroupRv.add(rowsRv);
                        break;
                    }
                    // else fall through...
                default:
                    final WebMarkupContainer tabGroup = new TabGroupPanel(id, getModel(), bs3TabGroup);

                    tabGroupRv.add(tabGroup);
                    break;
                }

            }
            div.add(tabGroupRv);
            visible = visible || tabGroupRv.isVisible();
        } else {
            Components.permanentlyHide(div, ID_TAB_GROUPS);
        }



        // fieldsets
        final List<FieldSet> fieldSetsWithProperties = FluentIterable.from(bs3Col.getFieldSets())
                .filter(new Predicate<FieldSet>() {
                    @Override public boolean apply(@Nullable final FieldSet fieldSet) {
                        return !fieldSet.getProperties().isEmpty();
                    }
                }).toList();
        if(!fieldSetsWithProperties.isEmpty()) {
            final RepeatingViewWithDynamicallyVisibleContent fieldSetRv =
                    new RepeatingViewWithDynamicallyVisibleContent(ID_FIELD_SETS);

            for (FieldSet fieldSet : fieldSetsWithProperties) {

                final String id = fieldSetRv.newChildId();

                final PropertyGroup propertyGroup = new PropertyGroup(id, getModel(), fieldSet);
                fieldSetRv.add(propertyGroup);
            }
            div.add(fieldSetRv);
            visible = visible || fieldSetRv.isVisible();
        } else {
            Components.permanentlyHide(div, ID_FIELD_SETS);
        }


        // collections
        final List<CollectionLayoutData> collections =
                FluentIterable.from(bs3Col.getCollections()).filter(
                    new Predicate<CollectionLayoutData>() {
                        @Override
                        public boolean apply(final CollectionLayoutData collectionLayoutData) {
                            return collectionLayoutData.getMetadataError() == null;
                        }
                    }).toList();
        if(!collections.isEmpty()) {
            final RepeatingViewWithDynamicallyVisibleContent collectionRv =
                    new RepeatingViewWithDynamicallyVisibleContent(ID_COLLECTIONS);

            final EntityModel entityModel = getModel();
            final CollectionLayoutData snapshot = entityModel.getCollectionLayoutData();
            
            for (CollectionLayoutData collection : collections) {

                final String id = collectionRv.newChildId();

                // we successively trample over the layout data; but that's ok, this is synchronous code anyway...
                entityModel.setCollectionLayoutData(collection);

                // the entityModel's getLayoutData() provides the hint as to which collection of the entity to render.
                final ComponentFactory componentFactory =
                        getComponentFactoryRegistry().findComponentFactory(
                                ComponentType.ENTITY_COLLECTION, entityModel);
                final Component collectionPanel = componentFactory.createComponent(id, entityModel);
                collectionRv.add(collectionPanel);
            }
            div.add(collectionRv);
            visible = visible || collectionRv.isVisible();
            
            //XXX ISIS-1698 restore original state after trampling over
            entityModel.setCollectionLayoutData(snapshot);
            
        } else {
            Components.permanentlyHide(div, ID_COLLECTIONS);
        }


        final WebMarkupContainer panel = this;
        if(visible) {
            panel.add(div);
        } else {
            Components.permanentlyHide(panel, div.getId());
        }

    }

    private RepeatingViewWithDynamicallyVisibleContent buildRows(final String owningId, final List<BS3Row> rows) {
        final RepeatingViewWithDynamicallyVisibleContent rowRv =
                new RepeatingViewWithDynamicallyVisibleContent(owningId);

        for(final BS3Row bs3Row: rows) {
            final String id = rowRv.newChildId();
            final Row row = new Row(id, getModel(), bs3Row);
            rowRv.add(row);
        }
        return rowRv;
    }


    private boolean visible = false;
    @Override
    public boolean isVisible() {
        return visible;
    }


}

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

package org.apache.isis.viewer.wicket.ui.components.scalars;

import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.wicketstuff.select2.ChoiceProvider;

import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.spec.ObjectSpecId;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.core.runtime.system.session.IsisSessionFactory;
import org.apache.isis.viewer.wicket.model.mementos.ObjectAdapterMemento;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;
import org.apache.isis.viewer.wicket.ui.components.widgets.bootstrap.FormGroup;
import org.apache.isis.viewer.wicket.ui.components.widgets.select2.Select2;
import org.apache.isis.viewer.wicket.ui.components.widgets.select2.providers.ObjectAdapterMementoProviderForChoices;
import org.apache.isis.viewer.wicket.ui.util.CssClassAppender;

public abstract class ScalarPanelSelect2Abstract extends ScalarPanelAbstract2 {

    private static final long serialVersionUID = 1L;

    protected Select2 select2;

    public ScalarPanelSelect2Abstract(final String id, final ScalarModel scalarModel) {
        super(id, scalarModel);
    }


    protected Select2 createSelect2(final String id) {
        final Select2 select2 = Select2.createSelect2(id, scalarModel);
        setProviderAndCurrAndPending(select2, scalarModel.getActionArgsHint());
        select2.setRequired(scalarModel.isRequired());
        return select2;
    }

    private Label createScalarName(final String id) {
        final String labelCaption = getRendering().getLabelCaption(select2.component());
        return createScalarName(id, labelCaption);
    }

    protected FormGroup createFormGroupAndName(
            final FormComponent<?> component,
            final String formGroupId, final String nameId) {
        final FormGroup formGroup = new FormGroup(formGroupId, component);
        final String describedAs = getModel().getDescribedAs();
        if(describedAs != null) {
            formGroup.add(new AttributeModifier("title", Model.of(describedAs)));
        }
        formGroup.add(component);

        final String labelCaption = getRendering().getLabelCaption(select2.component());
        final Label scalarName = createScalarName(nameId, labelCaption);
        formGroup.addOrReplace(scalarName);
        return formGroup;
    }

    protected FormGroup createFormGroup(final FormComponent<?> formComponent) {
        setOutputMarkupId(true);
        select2.component().setOutputMarkupId(true);

        final String name = scalarModel.getName();
        select2.setLabel(Model.of(name));

        final FormGroup formGroup = createFormGroupAndName(formComponent, ID_SCALAR_IF_REGULAR, ID_SCALAR_NAME);

        addStandardSemantics();

        final ScalarModel model = getModel();
        if(model.isRequired() && model.canEnterEditMode()) {
            formGroup.add(new CssClassAppender("mandatory"));
        }
        return formGroup;
    }

    protected void addStandardSemantics() {
        select2.setRequired(getModel().isRequired());
        select2.add(new Select2Validator(this.scalarModel));
    }

    @Override
    protected Component getScalarValueComponent() {
        return select2.component();
    }


    /**
     * sets up the choices, also ensuring that any currently held value is compatible.
     */
    private void setProviderAndCurrAndPending(final Select2 select2, ObjectAdapter[] argsIfAvailable) {

        final ChoiceProvider<ObjectAdapterMemento> choiceProvider;
        choiceProvider = buildChoiceProvider(argsIfAvailable);

        select2.setProvider(choiceProvider);
        getModel().clearPending();

        if(choiceProvider instanceof ObjectAdapterMementoProviderForChoices) {
            final ObjectAdapterMementoProviderForChoices providerForChoices = (ObjectAdapterMementoProviderForChoices) choiceProvider;
            syncIfNull(select2, providerForChoices.getChoiceMementos());
        }
    }

    /**
     * Mandatory hook (is called by {@link #setProviderAndCurrAndPending(Select2, ObjectAdapter[])})
     */
    protected abstract ChoiceProvider<ObjectAdapterMemento> buildChoiceProvider(final ObjectAdapter[] argsIfAvailable);

    /**
     * Mandatory hook (is called by {@link #setProviderAndCurrAndPending(Select2, ObjectAdapter[])})
     */
    protected abstract void syncIfNull(final Select2 select2, final List<ObjectAdapterMemento> choicesMementos);


    // //////////////////////////////////////

    /**
     * Automatically "opens" the select2.
     */
    @Override
    protected void onSwitchFormForInlinePrompt(
            final WebMarkupContainer inlinePromptForm,
            final AjaxRequestTarget target) {

        target.appendJavaScript(
                String.format("Wicket.Event.publish(Isis.Topic.OPEN_SELECT2, '%s')", inlinePromptForm.getMarkupId()));

    }

    // //////////////////////////////////////

    /**
     * Hook method to refresh choices when changing.
     *
     * <p>
     * called from onUpdate callback
     */
    public boolean updateChoices(ObjectAdapter[] argsIfAvailable) {
        if (select2 == null) {
            return false;
        }
        setProviderAndCurrAndPending(select2, argsIfAvailable);
        return true;
    }



    /**
     * Repaints just the Select2 component
     *
     * @param target The Ajax request handler
     */
    @Override
    public void repaint(AjaxRequestTarget target) {
        target.add(select2.component());
    }

    static class Select2Validator implements IValidator<Object> {
        private static final long serialVersionUID = 1L;

        private final ScalarModel scalarModel;

        public Select2Validator(final ScalarModel scalarModel) {

            this.scalarModel = scalarModel;
        }

        @Override
        public void validate(final IValidatable<Object> validatable) {
            final Object proposedValueObj = validatable.getValue();

            final ObjectAdapterMemento proposedValue;

            if (proposedValueObj instanceof List) {
                final List proposedValueObjAsList = (List) proposedValueObj;
                if (proposedValueObjAsList.isEmpty()) {
                    return;
                }
                final ObjectAdapterMemento oam = (ObjectAdapterMemento) proposedValueObjAsList.get(0);
                final ObjectSpecId objectSpecId = oam.getObjectSpecId();
                proposedValue = ObjectAdapterMemento
                        .createForList(proposedValueObjAsList, objectSpecId);
            } else {
                proposedValue = (ObjectAdapterMemento) proposedValueObj;
            }

            final ObjectAdapter proposedAdapter = proposedValue.getObjectAdapter(
                    AdapterManager.ConcurrencyChecking.NO_CHECK,
                    getPersistenceSession(), getSpecificationLoader());

            final String reasonIfAny = scalarModel.validate(proposedAdapter);
            if (reasonIfAny != null) {
                final ValidationError error = new ValidationError();
                error.setMessage(reasonIfAny);
                validatable.error(error);
            }
        }

        PersistenceSession getPersistenceSession() {
            return getIsisSessionFactory().getCurrentSession().getPersistenceSession();
        }

        SpecificationLoader getSpecificationLoader() {
            return getIsisSessionFactory().getSpecificationLoader();
        }

        IsisSessionFactory getIsisSessionFactory() {
            return IsisContext.getSessionFactory();
        }

    }
}

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

package org.apache.isis.viewer.wicket.ui.components.scalars.isisapplib;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.AbstractTextComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;

import org.apache.isis.applib.value.Password;
import org.apache.isis.viewer.wicket.model.models.ScalarModel;
import org.apache.isis.viewer.wicket.ui.components.scalars.ScalarPanelTextFieldParseableAbstract;
import org.apache.isis.viewer.wicket.ui.components.scalars.TextFieldStringModel;

import de.agilecoders.wicket.core.util.Attributes;

/**
 * Panel for rendering scalars of type {@link Password Isis' applib.Password}.
 */
public class IsisPasswordPanel extends ScalarPanelTextFieldParseableAbstract {

    private static final long serialVersionUID = 1L;

    public IsisPasswordPanel(final String id, final ScalarModel scalarModel) {
        super(id, scalarModel);
    }


    @Override
    protected AbstractTextComponent<String> createTextFieldForRegular(String id) {
        final TextFieldStringModel textModel = new TextFieldStringModel(this);
        final PasswordTextField passwordField = new PasswordTextField(id, textModel) {
            @Override
            protected void onComponentTag(ComponentTag tag) {
                Attributes.set(tag, "type", "password");
                super.onComponentTag(tag);
            }
        };

        passwordField.setResetPassword(false);

        return passwordField;
    }

    @Override
    protected String getScalarPanelType() {
        return "isisPasswordPanel";
    }
}

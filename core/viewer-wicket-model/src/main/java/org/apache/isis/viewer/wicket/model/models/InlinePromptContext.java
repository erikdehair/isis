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
package org.apache.isis.viewer.wicket.model.models;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class InlinePromptContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Component scalarIfRegular;
    private final WebMarkupContainer scalarIfRegularInlinePromptForm;
    private final MarkupContainer scalarTypeContainer;

    public InlinePromptContext(
            final Component scalarIfRegular,
            final WebMarkupContainer scalarIfRegularInlinePromptForm,
            final MarkupContainer scalarTypeContainer) {
        this.scalarIfRegular = scalarIfRegular;
        this.scalarIfRegularInlinePromptForm = scalarIfRegularInlinePromptForm;
        this.scalarTypeContainer = scalarTypeContainer;
    }

    public Component getScalarIfRegular() {
        return scalarIfRegular;
    }

    public WebMarkupContainer getScalarIfRegularInlinePromptForm() {
        return scalarIfRegularInlinePromptForm;
    }

    public void onCancel() {
        scalarIfRegular.setVisible(true);
        scalarIfRegularInlinePromptForm.setVisible(false);
    }

    public MarkupContainer getScalarTypeContainer() {
        return scalarTypeContainer;
    }
}

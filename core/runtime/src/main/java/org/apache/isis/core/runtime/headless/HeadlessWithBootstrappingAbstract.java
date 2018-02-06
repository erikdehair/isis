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
package org.apache.isis.core.runtime.headless;

import java.io.PrintStream;

import com.google.common.base.Strings;

import org.apache.log4j.PropertyConfigurator;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.clock.Clock;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.commons.factory.InstanceUtil;
import org.apache.isis.core.runtime.headless.logging.LogConfig;
import org.apache.isis.core.runtime.headless.logging.LogStream;

/**
 * Provides headless access to the system, first bootstrapping the system if required.
 *
 * <p>
 *     This acts as a common superclass from which framework-provided adapter classes for integration tests
 *     (<tt>IntegrationTestAbstract3</tt>) and for BDD spec glue (<tt>CukeGlueIntegrationScopeAbstract</tt> inherit,
 *     to bootstrap the system with a given module for headless access.
 * </p>
 */
public abstract class HeadlessWithBootstrappingAbstract extends HeadlessAbstract {

    private static final Logger LOG = LoggerFactory.getLogger(HeadlessWithBootstrappingAbstract.class);

    private final LogConfig logConfig;
    protected static PrintStream logPrintStream(Level level) {
        return LogStream.logPrintStream(LOG, level);
    }

    private final static ThreadLocal<Boolean> setupLogging = new ThreadLocal<Boolean>() {{
        set(false);
    }};


    private final IsisSystemBootstrapper isisSystemBootstrapper;

    protected HeadlessWithBootstrappingAbstract(
            final Module module) {
        this(new LogConfig(Level.INFO), module);
    }

    protected HeadlessWithBootstrappingAbstract(
            final LogConfig logConfig,
            final Module module) {

        this.logConfig = logConfig;

        final boolean firstTime = !setupLogging.get();
        if(firstTime) {
            PropertyConfigurator.configure(logConfig.getLoggingPropertyFile());
            setupLogging.set(true);
        }

        final String integTestModuleFqcn = System.getProperty("isis.integTest.module");
        LOG.info("isis.integTest.module = " + integTestModuleFqcn);
        String moduleFqcn = integTestModuleFqcn;

        if(moduleFqcn == null) {
            final String headlessModuleFqcn = System.getProperty("isis.headless.module");
            LOG.info("isis.headless.module = " + headlessModuleFqcn);
            moduleFqcn = headlessModuleFqcn;
        }

        final Module moduleToUse =
                !Strings.isNullOrEmpty(moduleFqcn)
                        ? InstanceUtil.createInstance(moduleFqcn, Module.class)
                        : module;
        this.isisSystemBootstrapper =
                new IsisSystemBootstrapper(logConfig, moduleToUse);
    }


    private LocalDate timeBeforeTest;

    protected void bootstrapAndSetupIfRequired() {

        System.setProperty("isis.headless", "true");
        System.setProperty("isis.integTest", "true");
        System.setProperty("isis.bddSpec", "true");

        isisSystemBootstrapper.bootstrapIfRequired();
        isisSystemBootstrapper.injectServicesInto(this);
        fixtureScripts.setFixtureTracing(logConfig.getFixtureTracing());

        beginTransaction();

        isisSystemBootstrapper.setupModuleRefData();

        timeBeforeTest = Clock.getTimeAsLocalDate();
    }

    private void beginTransaction() {
        final IsisSystem isft = IsisSystem.get();
        isft.beginTran();
    }

    protected void tearDownAllModules() {

        final boolean testHealthy = transactionService != null;
        if(!testHealthy) {
            // avoid throwing an NPE here if something unexpected has occurred...
            return;
        }

        transactionService.nextTransaction(TransactionService.Policy.ALWAYS);

        isisSystemBootstrapper.tearDownAllModules();

        // reinstate clock
        setFixtureClockDate(timeBeforeTest);
    }

    protected void log(final String message) {
        switch (logConfig.getTestLoggingLevel()) {
        case ERROR:
            LOG.error(message);
            break;
        case WARN:
            LOG.warn(message);
            break;
        case INFO:
            LOG.info(message);
            break;
        case DEBUG:
            LOG.debug(message);
            break;
        case TRACE:
            LOG.trace(message);
            break;
        }
    }
}
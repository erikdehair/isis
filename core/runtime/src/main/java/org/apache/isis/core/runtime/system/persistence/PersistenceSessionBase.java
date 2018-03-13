package org.apache.isis.core.runtime.system.persistence;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.isis.applib.internal.collections._Lists;
import org.apache.isis.applib.internal.collections._Maps;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.command.CommandContext;
import org.apache.isis.applib.services.command.spi.CommandService;
import org.apache.isis.applib.services.eventbus.EventBusService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.iactn.InteractionContext;
import org.apache.isis.applib.services.metrics.MetricsService;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.util.ToString;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.metamodel.services.ServicesInjector;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.core.runtime.persistence.FixturesInstalledFlag;
import org.apache.isis.core.runtime.services.changes.ChangedObjectsServiceInternal;
import org.apache.isis.core.runtime.system.transaction.IsisTransactionManager;
import org.apache.isis.objectstore.jdo.datanucleus.persistence.queries.PersistenceQueryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PersistenceSessionBase implements PersistenceSession {

	// -- CONSTANTS
	
    protected static final Logger LOG = LoggerFactory.getLogger(PersistenceSession.class);

    // -- FIELDS

    protected final FixturesInstalledFlag fixturesInstalledFlag;

    protected final PersistenceQueryFactory persistenceQueryFactory;
    protected final IsisConfiguration configuration;
    protected final SpecificationLoader specificationLoader;
    protected final AuthenticationSession authenticationSession;

    protected final ServicesInjector servicesInjector;

    protected final CommandContext commandContext;
    protected final CommandService commandService;

    protected final InteractionContext interactionContext;
    protected final EventBusService eventBusService ;
    protected final ChangedObjectsServiceInternal changedObjectsServiceInternal;
    protected final FactoryService factoryService;
    protected final MetricsService metricsService;
    protected final ClockService clockService;
    protected final UserService userService;


    /**
     * Used to create the {@link #persistenceManager} when {@link #open()}ed.
     */
    protected final PersistenceManagerFactory jdoPersistenceManagerFactory;

    // not final only for testing purposes
    protected IsisTransactionManager transactionManager;


    /**
     * populated only when {@link #open()}ed.
     */
    protected PersistenceManager persistenceManager;

    /**
     * populated only when {@link #open()}ed.
     */
    protected final Map<Class<?>, PersistenceQueryProcessor<?>> persistenceQueryProcessorByClass = _Maps.newHashMap();


    protected final boolean concurrencyCheckingGloballyEnabled;

    // -- CONSTRUCTOR

    /**
     * Initialize the object store so that calls to this object store access
     * persisted objects and persist changes to the object that are saved.
     */
    protected PersistenceSessionBase(
            final ServicesInjector servicesInjector,
            final AuthenticationSession authenticationSession,
            final PersistenceManagerFactory jdoPersistenceManagerFactory,
            final FixturesInstalledFlag fixturesInstalledFlag) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("creating {}", this);
        }

        this.servicesInjector = servicesInjector;
        this.jdoPersistenceManagerFactory = jdoPersistenceManagerFactory;
        this.fixturesInstalledFlag = fixturesInstalledFlag;

        // injected
        this.configuration = servicesInjector.getConfigurationServiceInternal();
        this.specificationLoader = servicesInjector.getSpecificationLoader();
        this.authenticationSession = authenticationSession;

        this.commandContext = lookupService(CommandContext.class);
        this.commandService = lookupService(CommandService.class);
        this.interactionContext = lookupService(InteractionContext.class);
        this.eventBusService = lookupService(EventBusService.class);
        this.changedObjectsServiceInternal = lookupService(ChangedObjectsServiceInternal.class);
        this.metricsService = lookupService(MetricsService.class);
        this.factoryService = lookupService(FactoryService.class);
        this.clockService = lookupService(ClockService.class);
        this.userService = lookupService(UserService.class);

        // sub-components
        final AdapterManager adapterManager = this;
        this.persistenceQueryFactory = new PersistenceQueryFactory(adapterManager, this.specificationLoader);
        this.transactionManager = new IsisTransactionManager(this, authenticationSession, servicesInjector);

        this.state = State.NOT_INITIALIZED;

        final boolean concurrencyCheckingGloballyDisabled =
                this.configuration.getBoolean("isis.persistor.disableConcurrencyChecking", false);
        this.concurrencyCheckingGloballyEnabled = !concurrencyCheckingGloballyDisabled;

    }
    
    // -- GETTERS
    
    protected SpecificationLoader getSpecificationLoader() {
        return specificationLoader;
    }
    protected AuthenticationSession getAuthenticationSession() {
        return authenticationSession;
    }

    /**
     * The configured {@link ServicesInjector}.
     */
    @Override
    public ServicesInjector getServicesInjector() {
        return servicesInjector;
    }

    /**
     * The configured {@link IsisTransactionManager}.
     */
    @Override
    public IsisTransactionManager getTransactionManager() {
        return transactionManager;
    }
    
    /**
     * Only populated once {@link #open()}'d
     */
    @Override
    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }
    
    @Override
    public PersistenceManager pm() {
        return persistenceManager;
    }
    
    @Override
    public IsisConfiguration getConfiguration() {
        return configuration;
    }
    
    @Override
    public List<ObjectAdapter> getServices() {
        final List<Object> services = servicesInjector.getRegisteredServices();
        final List<ObjectAdapter> serviceAdapters = _Lists.newArrayList();
        for (final Object servicePojo : services) {
            ObjectAdapter serviceAdapter = getAdapterFor(servicePojo);
            if(serviceAdapter == null) {
                throw new IllegalStateException("ObjectAdapter for service " + servicePojo + " does not exist?!?");
            }
            serviceAdapters.add(serviceAdapter);
        }
        return serviceAdapters;
    }
    
    // -- ENUMS

    protected enum Type {
        TRANSIENT,
        PERSISTENT
    }
    
    protected enum Variant {
        TRANSIENT,
        VIEW_MODEL
    }

    protected enum State {
        NOT_INITIALIZED, OPEN, CLOSED
    }
    
    // -- STATE

    protected State state;

    protected void ensureNotOpened() {
        if (state != State.NOT_INITIALIZED) {
            throw new IllegalStateException("Persistence session has already been initialized");
        }
    }

    protected void ensureOpened() {
        ensureStateIs(State.OPEN);
    }

    private void ensureStateIs(final State stateRequired) {
        if (state == stateRequired) {
            return;
        }
        throw new IllegalStateException("State is: " + state + "; should be: " + stateRequired);
    }
    
    // -- TRANSACTIONS
    
    @Override
    public void startTransaction() {
        final javax.jdo.Transaction transaction = persistenceManager.currentTransaction();
        if (transaction.isActive()) {
            throw new IllegalStateException("Transaction already active");
        }
        transaction.begin();
    }

    @Override
    public void endTransaction() {
        final javax.jdo.Transaction transaction = persistenceManager.currentTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    @Override
    public void abortTransaction() {
        final javax.jdo.Transaction transaction = persistenceManager.currentTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    // -- HELPERS - SERVICE LOOKUP

    private <T> T lookupService(Class<T> serviceType) {
        T service = lookupServiceIfAny(serviceType);
        if(service == null) {
            throw new IllegalStateException("Could not locate service of type '" + serviceType + "'");
        }
        return service;
    }

    private <T> T lookupServiceIfAny(final Class<T> serviceType) {
        return servicesInjector.lookupService(serviceType);
    }

    protected <T> List<T> lookupServices(final Class<T> serviceClass) {
        return servicesInjector.lookupServices(serviceClass);
    }

    @Override
    public String toString() {
        return new ToString(this).toString();
    }

    
}

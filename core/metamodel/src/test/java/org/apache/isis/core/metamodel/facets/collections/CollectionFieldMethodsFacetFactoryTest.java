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

package org.apache.isis.core.metamodel.facets.collections;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jmock.auto.Mock;

import org.apache.isis.applib.security.UserMemento;
import org.apache.isis.core.metamodel.facetapi.Facet;
import org.apache.isis.core.metamodel.facets.AbstractFacetFactoryTest;
import org.apache.isis.core.metamodel.facets.FacetFactory.ProcessMethodContext;
import org.apache.isis.core.metamodel.facets.actcoll.typeof.TypeOfFacet;
import org.apache.isis.core.metamodel.facets.collections.accessor.CollectionAccessorFacetViaAccessor;
import org.apache.isis.core.metamodel.facets.collections.accessor.CollectionAccessorFacetViaAccessorFactory;
import org.apache.isis.core.metamodel.facets.collections.clear.CollectionClearFacetFactory;
import org.apache.isis.core.metamodel.facets.collections.clear.CollectionClearFacetViaAccessor;
import org.apache.isis.core.metamodel.facets.collections.clear.CollectionClearFacetViaClearMethod;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionAddToFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionAddToFacetViaAccessor;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionAddToFacetViaMethod;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionAddToRemoveFromAndValidateFacetFactory;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionClearFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionRemoveFromFacet;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionRemoveFromFacetViaAccessor;
import org.apache.isis.core.metamodel.facets.collections.modify.CollectionRemoveFromFacetViaMethod;
import org.apache.isis.core.metamodel.facets.collections.modify.TypeOfFacetInferredFromSupportingMethods;
import org.apache.isis.core.metamodel.facets.collections.validate.CollectionValidateAddToFacet;
import org.apache.isis.core.metamodel.facets.collections.validate.CollectionValidateAddToFacetViaMethod;
import org.apache.isis.core.metamodel.facets.collections.validate.CollectionValidateRemoveFromFacet;
import org.apache.isis.core.metamodel.facets.collections.validate.CollectionValidateRemoveFromFacetViaMethod;
import org.apache.isis.core.metamodel.facets.members.disabled.forsession.DisableForSessionFacet;
import org.apache.isis.core.metamodel.facets.members.disabled.forsession.DisableForSessionFacetViaMethod;
import org.apache.isis.core.metamodel.facets.members.disabled.forsession.DisableForSessionFacetViaMethodFactory;
import org.apache.isis.core.metamodel.facets.members.hidden.forsession.HideForSessionFacet;
import org.apache.isis.core.metamodel.facets.members.hidden.forsession.HideForSessionFacetViaMethod;
import org.apache.isis.core.metamodel.facets.members.hidden.forsession.HideForSessionFacetViaMethodFactory;
import org.apache.isis.core.metamodel.facets.propcoll.accessor.PropertyOrCollectionAccessorFacet;
import org.apache.isis.core.metamodel.spec.ObjectSpecification;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class CollectionFieldMethodsFacetFactoryTest extends AbstractFacetFactoryTest {

    private JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ObjectSpecification mockSpecification;

    public void setUp() throws Exception {
        super.setUp();

        // expecting
        allowing_specificationLoader_loadSpecification_any_willReturn(mockSpecification);
    }

    public void testPropertyAccessorFacetIsInstalledForJavaUtilCollectionAndMethodRemoved() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(CustomerStatic.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }


    public void testPropertyAccessorFacetIsInstalledForJavaUtilListAndMethodRemoved() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public List getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");


        facetFactory.process(new ProcessMethodContext(CustomerStatic.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForJavaUtilSetAndMethodRemoved() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Set getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForObjectArrayAndMethodRemoved() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        class Customer {
            @SuppressWarnings("unused")
            public Object[] getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testPropertyAccessorFacetIsInstalledForOrderArrayAndMethodRemoved() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Order[] getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor propertyAccessorFacetViaAccessor = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, propertyAccessorFacetViaAccessor.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(collectionAccessorMethod));
    }

    public void testAddToFacetIsInstalledViaAccessorIfNoExplicitAddToMethodExists() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionAddToFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAddToFacetViaAccessor);
        final CollectionAddToFacetViaAccessor collectionAddToFacetViaAccessor = (CollectionAddToFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, collectionAddToFacetViaAccessor.getMethods().get(0));
    }

    public void testCannotInferTypeOfFacetIfNoExplicitAddToOrRemoveFromMethods() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        assertNull(facetedMethod.getFacet(TypeOfFacet.class));
    }

    public void testRemoveFromFacetIsInstalledViaAccessorIfNoExplicitRemoveFromMethodExists() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionRemoveFromFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionRemoveFromFacetViaAccessor);
        final CollectionRemoveFromFacetViaAccessor collectionRemoveFromFacetViaAccessor = (CollectionRemoveFromFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, collectionRemoveFromFacetViaAccessor.getMethods().get(0));
    }

    public void testAddToFacetIsInstalledAndMethodRemoved() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void addToOrders(final Order o) {
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method addToMethod = findMethod(Customer.class, "addToOrders", new Class[] { Order.class });

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionAddToFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAddToFacetViaMethod);
        final CollectionAddToFacetViaMethod collectionAddToFacetViaMethod = (CollectionAddToFacetViaMethod) facet;
        assertEquals(addToMethod, collectionAddToFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(addToMethod));
    }

    public void testCanInferTypeOfFacetFromExplicitAddToMethod() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void addToOrders(final Order o) {
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromSupportingMethods);
        final TypeOfFacetInferredFromSupportingMethods typeOfFacetInferredFromSupportingMethods = (TypeOfFacetInferredFromSupportingMethods) facet;
        assertEquals(Order.class, typeOfFacetInferredFromSupportingMethods.value());
    }

    public void testRemoveFromFacetIsInstalledAndMethodRemoved() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void removeFromOrders(final Order o) {
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method removeFromMethod = findMethod(Customer.class, "removeFromOrders", new Class[] { Order.class });

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionRemoveFromFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionRemoveFromFacetViaMethod);
        final CollectionRemoveFromFacetViaMethod collectionRemoveFromFacetViaMethod = (CollectionRemoveFromFacetViaMethod) facet;
        assertEquals(removeFromMethod, collectionRemoveFromFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(removeFromMethod));
    }

    public void testCanInferTypeOfFacetFromExplicitRemoveFromMethod() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            };

            @SuppressWarnings("unused")
            public void removeFromOrders(final Order o) {
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(TypeOfFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof TypeOfFacetInferredFromSupportingMethods);
        final TypeOfFacetInferredFromSupportingMethods typeOfFacetInferredFromSupportingMethods = (TypeOfFacetInferredFromSupportingMethods) facet;
        assertEquals(Order.class, typeOfFacetInferredFromSupportingMethods.value());
    }

    public void testClearFacetIsInstalledAndMethodRemoved() {
        final CollectionClearFacetFactory facetFactory = new CollectionClearFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings({ "hiding", "unused" })
        class Order {
        }
        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void clearOrders() {
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method clearMethod = findMethod(Customer.class, "clearOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionClearFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionClearFacetViaClearMethod);
        final CollectionClearFacetViaClearMethod collectionClearFacetViaClearMethod = (CollectionClearFacetViaClearMethod) facet;
        assertEquals(clearMethod, collectionClearFacetViaClearMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(clearMethod));
    }

    public void testClearFacetIsInstalledViaAccessorIfNoExplicitClearMethod() {
        final CollectionClearFacetFactory facetFactory = new CollectionClearFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings({ "hiding", "unused" })
        class Order {
        }
        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionClearFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionClearFacetViaAccessor);
        final CollectionClearFacetViaAccessor collectionClearFacetViaAccessor = (CollectionClearFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, collectionClearFacetViaAccessor.getMethods().get(0));
    }

    public void testValidateAddToFacetIsInstalledAndMethodRemoved() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings({ "rawtypes", "unused" })
            public Collection getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void addToOrders(final Order o) {
            };

            @SuppressWarnings("unused")
            public String validateAddToOrders(final Order o) {
                return null;
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method validateAddToMethod = findMethod(Customer.class, "validateAddToOrders", new Class[] { Order.class });

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionValidateAddToFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionValidateAddToFacetViaMethod);
        final CollectionValidateAddToFacetViaMethod collectionValidateAddToFacetViaMethod = (CollectionValidateAddToFacetViaMethod) facet;
        assertEquals(validateAddToMethod, collectionValidateAddToFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(validateAddToMethod));
    }

    public void testValidateRemoveFromFacetIsInstalledAndMethodRemoved() {
        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactory = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }

            @SuppressWarnings("unused")
            public void removeFromOrders(final Order o) {
            };

            @SuppressWarnings("unused")
            public String validateRemoveFromOrders(final Order o) {
                return null;
            };
        }
        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method validateRemoveFromMethod = findMethod(Customer.class, "validateRemoveFromOrders", new Class[] { Order.class });

        facetFactory.process(new ProcessMethodContext(Customer.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionValidateRemoveFromFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionValidateRemoveFromFacetViaMethod);
        final CollectionValidateRemoveFromFacetViaMethod collectionValidateRemoveFromFacetViaMethod = (CollectionValidateRemoveFromFacetViaMethod) facet;
        assertEquals(validateRemoveFromMethod, collectionValidateRemoveFromFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(validateRemoveFromMethod));
    }

    public void testMethodFoundInSuperclass() {
        final CollectionAccessorFacetViaAccessorFactory facetFactory = new CollectionAccessorFacetViaAccessorFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }

        class CustomerEx extends Customer {
        }

        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");

        facetFactory.process(new ProcessMethodContext(CustomerEx.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(PropertyOrCollectionAccessorFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionAccessorFacetViaAccessor);
        final CollectionAccessorFacetViaAccessor collectionAccessorFacetViaMethod = (CollectionAccessorFacetViaAccessor) facet;
        assertEquals(collectionAccessorMethod, collectionAccessorFacetViaMethod.getMethods().get(0));
    }

    public void testMethodFoundInSuperclassButHelpeMethodsFoundInSubclasses() {
        final CollectionAccessorFacetViaAccessorFactory facetFactoryForAccessor = new CollectionAccessorFacetViaAccessorFactory();

        facetFactoryForAccessor.setServicesInjector(stubServicesInjector);

        final CollectionAddToRemoveFromAndValidateFacetFactory facetFactoryForHelpers = new CollectionAddToRemoveFromAndValidateFacetFactory();

        facetFactoryForHelpers.setServicesInjector(stubServicesInjector);

        @SuppressWarnings("hiding")
        class Order {
        }
        class Customer {
            @SuppressWarnings("unused")
            public Collection<Order> getOrders() {
                return null;
            }
        }

        class CustomerEx extends Customer {
            @SuppressWarnings("unused")
            public void removeFromOrders(final Order o) {
            };

            @SuppressWarnings("unused")
            public String validateRemoveFromOrders(final Order o) {
                return null;
            };
        }

        final Method collectionAccessorMethod = findMethod(Customer.class, "getOrders");
        final Method removeFromMethod = findMethod(CustomerEx.class, "removeFromOrders", new Class[] { Order.class });
        final Method validateRemoveFromMethod = findMethod(CustomerEx.class, "validateRemoveFromOrders", new Class[] { Order.class });

        facetFactoryForAccessor.process(new ProcessMethodContext(CustomerEx.class, null, collectionAccessorMethod, methodRemover, facetedMethod));
        facetFactoryForHelpers.process(new ProcessMethodContext(CustomerEx.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(CollectionRemoveFromFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof CollectionRemoveFromFacetViaMethod);
        final CollectionRemoveFromFacetViaMethod collectionRemoveFromFacetViaMethod = (CollectionRemoveFromFacetViaMethod) facet;
        assertEquals(removeFromMethod, collectionRemoveFromFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(removeFromMethod));

        final Facet facet1 = facetedMethod.getFacet(CollectionValidateRemoveFromFacet.class);
        assertNotNull(facet1);
        assertTrue(facet1 instanceof CollectionValidateRemoveFromFacetViaMethod);
        final CollectionValidateRemoveFromFacetViaMethod collectionValidateRemoveFromFacetViaMethod = (CollectionValidateRemoveFromFacetViaMethod) facet1;
        assertEquals(validateRemoveFromMethod, collectionValidateRemoveFromFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(validateRemoveFromMethod));
    }

    static class Order {
    }

    public static class CustomerStatic {
        public Collection<Order> getOrders() {
            return null;
        }

        public static String nameOrders() {
            return "Most Recent Orders";
        };

        public static String descriptionOrders() {
            return "Some old description";
        }

        public static boolean alwaysHideOrders() {
            return true;
        }

        public static boolean protectOrders() {
            return true;
        }

        public static boolean hideOrders(final UserMemento userMemento) {
            return true;
        }

        public static String disableOrders(final UserMemento userMemento) {
            return "disabled for this user";
        }

        public static void getOtherOrders() {
        }

        public static boolean alwaysHideOtherOrders() {
            return false;
        }

        public static boolean protectOtherOrders() {
            return false;
        }
    }

    public void testInstallsHiddenForSessionFacetAndRemovesMethod() {
        final HideForSessionFacetViaMethodFactory facetFactory = new HideForSessionFacetViaMethodFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        final Method collectionAccessorMethod = findMethod(CustomerStatic.class, "getOrders");
        final Method hideMethod = findMethod(CustomerStatic.class, "hideOrders", new Class[] { UserMemento.class });

        facetFactory.process(new ProcessMethodContext(CustomerStatic.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(HideForSessionFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof HideForSessionFacetViaMethod);
        final HideForSessionFacetViaMethod hideForSessionFacetViaMethod = (HideForSessionFacetViaMethod) facet;
        assertEquals(hideMethod, hideForSessionFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(hideMethod));
    }

    public void testInstallsDisabledForSessionFacetAndRemovesMethod() {
        final DisableForSessionFacetViaMethodFactory facetFactory = new DisableForSessionFacetViaMethodFactory();

        facetFactory.setServicesInjector(stubServicesInjector);

        final Method collectionAccessorMethod = findMethod(CustomerStatic.class, "getOrders");
        final Method disableMethod = findMethod(CustomerStatic.class, "disableOrders", new Class[] { UserMemento.class });

        facetFactory.process(new ProcessMethodContext(CustomerStatic.class, null, collectionAccessorMethod, methodRemover, facetedMethod));

        final Facet facet = facetedMethod.getFacet(DisableForSessionFacet.class);
        assertNotNull(facet);
        assertTrue(facet instanceof DisableForSessionFacetViaMethod);
        final DisableForSessionFacetViaMethod disableForSessionFacetViaMethod = (DisableForSessionFacetViaMethod) facet;
        assertEquals(disableMethod, disableForSessionFacetViaMethod.getMethods().get(0));

        assertTrue(methodRemover.getRemovedMethodMethodCalls().contains(disableMethod));
    }

}

package org.nakedobjects.object;


public interface ObjectFactory {
    void setUpAsNewLogicalObject(Object object);
 
    /**
     * Sets up an new object to work within the business container, and initialises the logical objects.
     */
    Object createObject(NakedObjectSpecification specification);
    
    Object createValueObject(NakedObjectSpecification specification);
 
    /**
     * Sets up an existing object to work within the business container.  This is 
     * only needed if the object is created outside the framework, such as through
     * serialization, or within an object persistor.
     */
    void initRecreatedObject(Object object);
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/
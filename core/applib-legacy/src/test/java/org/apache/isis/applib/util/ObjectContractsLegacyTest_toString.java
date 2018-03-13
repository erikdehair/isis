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
package org.apache.isis.applib.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class ObjectContractsLegacyTest_toString {

    private Invoice inv;
    private Invoice2 inv2;

    @Before
    public void setUp() throws Exception {
        inv = new Invoice();
        inv2 = new Invoice2();
        
        inv.setNumber("123");
        inv2.setNumber("123");
    }

    @Test
    public void vanilla() {
        assertThat(InvoiceItem.newInvoiceItem(null, null, null, null).toString(), is("InvoiceItem{invoice=null, productCode=null, quantity=null, rush=null}"));
        assertThat(InvoiceItem.newInvoiceItem(inv, null, null, null).toString(), is("InvoiceItem{invoice=Invoice{number=123}, productCode=null, quantity=null, rush=null}"));
        assertThat(InvoiceItem.newInvoiceItem(inv, "A", null, null).toString(), is("InvoiceItem{invoice=Invoice{number=123}, productCode=A, quantity=null, rush=null}"));
        assertThat(InvoiceItem.newInvoiceItem(inv, "A", new Integer(1), null).toString(), is("InvoiceItem{invoice=Invoice{number=123}, productCode=A, quantity=1, rush=null}"));
        assertThat(InvoiceItem.newInvoiceItem(inv, "A", new Integer(1), true).toString(), is("InvoiceItem{invoice=Invoice{number=123}, productCode=A, quantity=1, rush=true}"));
    }

    @Test
    public void customized() {
        assertThat(InvoiceItem2.newInvoiceItem(null, null, null, null).toString(), is("InvoiceItem2{invoice=null, productCode=null, quantity=null, rush=null}"));
        assertThat(InvoiceItem2.newInvoiceItem(inv2, null, null, null).toString(), is("InvoiceItem2{invoice=123, productCode=null, quantity=null, rush=null}"));
        assertThat(InvoiceItem2.newInvoiceItem(inv2, "A", null, null).toString(), is("InvoiceItem2{invoice=123, productCode=A, quantity=null, rush=null}"));
        assertThat(InvoiceItem2.newInvoiceItem(inv2, "A", new Integer(1), null).toString(), is("InvoiceItem2{invoice=123, productCode=A, quantity=1, rush=null}"));
        assertThat(InvoiceItem2.newInvoiceItem(inv2, "A", new Integer(1), false).toString(), is("InvoiceItem2{invoice=123, productCode=A, quantity=1, rush=false}"));
    }

}

class Invoice2 implements Comparable<Invoice2>, Numbered {
    private static final String KEY_PROPERTIES = "number";
    
    private String number;
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    @Override
    public String toString() {
        return ObjectContractsLegacy.toString(this, KEY_PROPERTIES);
    }
    @Override
    public int compareTo(Invoice2 o) {
        return ObjectContractsLegacy.compare(this, o, KEY_PROPERTIES);
    }
}
class InvoiceItem2 implements Comparable<InvoiceItem2> {

    static InvoiceItem2 newInvoiceItem(Invoice2 invoice, String productCode, Integer quantity, Boolean rush) {
        final InvoiceItem2 invoiceItem = new InvoiceItem2();
        invoiceItem.setInvoice(invoice);
        invoiceItem.setProductCode(productCode);
        invoiceItem.setQuantity(quantity);
        invoiceItem.setRush(rush);
        return invoiceItem;
    }

    private Invoice2 invoice;
    public Invoice2 getInvoice() {
        return invoice;
    }
    public void setInvoice(Invoice2 invoice) {
        this.invoice = invoice;
    }

    private String productCode;
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    private Integer quantity;
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
 
    private Boolean rush;
    public Boolean isRush() {
        return rush;
    }
    public void setRush(Boolean rush) {
        this.rush = rush;
    }
    
    
    private static final String KEY_PROPERTIES = "invoice desc, productCode, quantity, rush desc";
    
    @Override
    public String toString() {
        return new ObjectContractsLegacy().with(new NumberedEvaluator()).toStringOf(this, KEY_PROPERTIES);
    }
    @Override
    public int compareTo(InvoiceItem2 o) {
        return ObjectContractsLegacy.compare(this, o, KEY_PROPERTIES);
    }
}
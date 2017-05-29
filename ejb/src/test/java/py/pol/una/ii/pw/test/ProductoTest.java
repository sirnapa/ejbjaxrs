/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.test;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import py.pol.una.ii.pw.model.Member;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.service.MemberRegistration;
import py.pol.una.ii.pw.service.ProductoRegistration;
import py.pol.una.ii.pw.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

public class ProductoTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(Producto.class, ProductoRegistration.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml", "test-ds.xml");
    }

    @Inject
    ProductoRegistration productoRegistration;

    @Inject
    Logger log;

    @Test
    public void testRegister() throws Exception {
        Producto prod = new Producto();
        prod.setNombre("Producto test");
        prod.setCantidad((float) 50);
        prod.setPrecioCompra((float) 50000);
        prod.setPrecioVenta((float) 60000);
        log.info(prod.getNombre()+" was persisted with id " + prod.getId());
        log.info(""+productoRegistration.getClass());
        productoRegistration.register(prod);
        assertNotNull(prod.getId());
        log.info(prod.getNombre()+" was persisted with id " + prod.getId());
    }
}

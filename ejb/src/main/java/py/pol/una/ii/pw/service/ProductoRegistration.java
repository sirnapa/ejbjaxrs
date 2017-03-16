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
package py.pol.una.ii.pw.service;


import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.pol.una.ii.pw.model.Producto;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProductoRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Producto> productoEventSrc;

    public void register(Producto producto) throws Exception {
    	log.info("Registering " + producto.getNombre());
        em.persist(producto);
        productoEventSrc.fire(producto);
    }
    
    public Producto deleteProducto(Long id){
    	Producto producto = em.find(Producto.class, id);
    	if (producto!=null){
        	log.info("Deleting " + producto.getNombre());
    		em.remove(producto);
    	}
    	return producto;

    }
    
    public Producto updateProducto(Producto producto){
    	Producto productoAnterior = em.find(Producto.class, producto.getId());
    	if (productoAnterior!=null){
        	log.info("Updated " + productoAnterior.getNombre());
        	producto.setId(productoAnterior.getId());        			
    		em.merge(producto);
    	}
    	return producto;
    }
}

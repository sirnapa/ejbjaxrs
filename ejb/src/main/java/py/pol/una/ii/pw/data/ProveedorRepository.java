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
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

@ApplicationScoped
public class ProveedorRepository {

    @Inject
    private EntityManager em;

    public Proveedor findById(Long id) {
    	SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
        Proveedor prov = session.selectOne("getProveedorById",id);
        return prov;
        //return em.find(Proveedor.class, id);
    }

    public List<Proveedor> findAllOrderedByName() {
    	SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
        List<Proveedor> proveedores = session.selectList("selectAllProveedores", null);
        return proveedores;
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Proveedor> criteria = cb.createQuery(Proveedor.class);
//        Root<Proveedor> producto = criteria.from(Proveedor.class);
//        criteria.select(producto).orderBy(cb.asc(producto.get("nombre")));
//        return em.createQuery(criteria).getResultList();
    }

}

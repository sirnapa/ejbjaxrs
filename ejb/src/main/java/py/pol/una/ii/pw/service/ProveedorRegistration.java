package py.pol.una.ii.pw.service;


import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.pol.una.ii.pw.model.Proveedor;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ProveedorRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Proveedor> proveedorEventSrc;

    public void register(Proveedor proveedor) throws Exception {
    	log.info("Registering " + proveedor.getNombre());
        em.persist(proveedor);
        proveedorEventSrc.fire(proveedor);
    }
    
    public Proveedor deleteProveedor(Long id){
    	Proveedor proveedor = em.find(Proveedor.class, id);
    	if (proveedor!=null){
        	log.info("Deleting " + proveedor.getNombre());
    		em.remove(proveedor);
    	}
    	return proveedor;

    }
    
    public Proveedor updateProveedor(Proveedor proveedor){
    	Proveedor proveedorAnterior = em.find(Proveedor.class, proveedor.getId());
    	if (proveedorAnterior!=null){
        	log.info("Updated " + proveedorAnterior.getNombre());
        	proveedor.setId(proveedorAnterior.getId());        			
    		em.merge(proveedor);
    	}
    	return proveedor;
    }
}

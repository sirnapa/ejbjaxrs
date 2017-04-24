package py.pol.una.ii.pw.service;


import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.ibatis.session.SqlSession;

import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

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

	private SqlSession session;
    @PostConstruct
    void init(){
    	session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
    }
    
    public void register(Proveedor proveedor) throws Exception {
    	
		try {
			int insertSuccess = session.insert("insertProveedor", proveedor);
		} catch (Exception e) {
			log.info(e.toString());
		}
//    	log.info("Registering " + proveedor.getNombre());
//        em.persist(proveedor);
//        proveedorEventSrc.fire(proveedor);
    }
    
    public Proveedor deleteProveedor(Long id){
    	
        session.delete("deleteProveedorById", id);     
//    	Proveedor proveedor = em.find(Proveedor.class, id);
//    	if (proveedor!=null){
//        	log.info("Deleting " + proveedor.getNombre());
//    		em.remove(proveedor);
//    	}
    	return null;

    }
    
    public Proveedor updateProveedor(Proveedor proveedor){
    	
        session.delete("updateProveedor", proveedor);     
//    	Proveedor proveedorAnterior = em.find(Proveedor.class, proveedor.getId());
//    	if (proveedorAnterior!=null){
//        	log.info("Updated " + proveedorAnterior.getNombre());
//        	proveedor.setId(proveedorAnterior.getId());        			
//    		em.merge(proveedor);
//    	}
    	return null;
    }
}

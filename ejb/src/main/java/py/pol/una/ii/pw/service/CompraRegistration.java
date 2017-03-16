package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class CompraRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;
    
    @Resource
	SessionContext ctx;

    @Inject
    private Event<CompraCabecera> compraCabeceraEventSrc;
    
    @Inject
    private Event<CompraDetalle> compraDetalleEventSrc;
    
	
    
    public void registerCompraCabecera(CompraCabecera compraCabecera) {
    	em.persist(compraCabecera);
    	compraCabeceraEventSrc.fire(compraCabecera);
    }
    
    public void updateCompraCabecera(CompraCabecera compraCabecera) {
    	log.info("Registering Compra" + compraCabecera.getId_compraCabecera());
    	em.merge(compraCabecera);
    	compraCabeceraEventSrc.fire(compraCabecera);
    }
    
    public void registerCompraDetalle(CompraDetalle compraDetalle){
    	em.persist(compraDetalle);
    	log.info("Registering Detalle" + compraDetalle.getId_compraDetalle());
    	compraDetalleEventSrc.fire(compraDetalle);
    }
}

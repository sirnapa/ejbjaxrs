package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.VentaDetalle;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class VentaRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;
    
    @Resource
	SessionContext ctx;

    @Inject
    private Event<VentaCabecera> ventaCabeceraEventSrc;
    
    @Inject
    private Event<VentaDetalle> ventaDetalleEventSrc;
    
	
    
    public void registerVentaCabecera(VentaCabecera ventaCabecera) {
    	em.persist(ventaCabecera);
    	ventaCabeceraEventSrc.fire(ventaCabecera);
    }
    
    public void updateVentaCabecera(VentaCabecera ventaCabecera) {
    	log.info("Registering Venta" + ventaCabecera.getId_ventaCabecera());
    	em.merge(ventaCabecera);
    	ventaCabeceraEventSrc.fire(ventaCabecera);
    }
    
    public void registerVentaDetalle(VentaDetalle ventaDetalle){
    	em.persist(ventaDetalle);
    	log.info("Registering Detalle" + ventaDetalle.getId_ventaDetalle());
    	ventaDetalleEventSrc.fire(ventaDetalle);
    }
}

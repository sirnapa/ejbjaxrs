package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;

import  java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import java.util.List;
import java.util.logging.Logger;

@Stateful
@SessionScoped
@TransactionManagement(TransactionManagementType.BEAN)
public class CompraRegistration {
	 
	@Resource
	private TransactionManager tm;
	
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
    
	private CompraCabecera compraCabecera;
	
	private int contador=0;
	private Transaction transaction;
	

    public void registerCompraCabecera(List <CompraDetalle> compraDetalleList) {
		try {
			tm.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///// prueba
    	contador = contador+1;
    	log.info("VALOR DEL CONTADOR ="+contador);
    	////fin prueba
    	
    	//crea una cabecera para los detalles recibidos en lista si es la primera vez que se le llama
		if (compraCabecera == null) {
			compraCabecera = new CompraCabecera();
			if (compraDetalleList.size() > 0) {
				compraCabecera.setProveedor(compraDetalleList.get(0).getCompraCabecera().getProveedor());
			}
	    	em.persist(compraCabecera); //guarda esta cabecera para luego poder asociarle su detalle
		}
  
        Float montoTotal = 0.0F;
        CompraDetalle detalle;
        //itera en la lista de detalles guardando uno por uno
        for(int x=0;x<compraDetalleList.size();x++) {
        	detalle= compraDetalleList.get(x);
        	montoTotal = (detalle.getProducto().getPrecioCompra() * detalle.getCantidad()) + montoTotal;
        	detalle.setCompraCabecera(compraCabecera);
        	registerCompraDetalle(detalle);
        	}
        
        //actualiza la cabecera 
        compraCabecera.setFecha(new java.util.Date());
        compraCabecera.setMonto(montoTotal);
        updateCompraCabecera(compraCabecera);
        
        try {
			transaction = tm.suspend();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

    
    @Remove 
    public void cancelarCompra() {
    	try {
			tm.resume(transaction);
		} catch (InvalidTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			tm.rollback();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Remove
    public void confirmarCompra() {
    	try {
			tm.resume(transaction);
		} catch (InvalidTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			tm.commit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	@PreDestroy
	public void preDestroy() {
		log.info("INFO PREDESTROY: " + "@PreDestroy");
	}
	
	@PostConstruct
	public void postConstruct() throws NamingException {
		InitialContext ctx = new InitialContext();
		try {
	        // JBoss
	        tm= (TransactionManager)
	            ctx.lookup("java:jboss/TransactionManager");
	    }
	    catch (Exception e) { }
	}
}

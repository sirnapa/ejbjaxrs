package py.pol.una.ii.pw.service;

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.VentaDetalle;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class VentaRegistration {
	
	@Resource
	private TransactionManager tm;

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;
    
    @Resource
	SessionContext ctx;
    
    @Inject
    ClienteRegistration clienteRegistration;

    @Inject
    private Event<VentaCabecera> ventaCabeceraEventSrc;
    
    @Inject
    private Event<VentaDetalle> ventaDetalleEventSrc;
    
	private VentaCabecera ventaCabecera;
	
	private int contador=0;
	private Transaction transaction;
	private SqlSession session;
    
	 public void iniciarVenta(VentaCabecera ventaCab) {
		///// prueba
		contador = contador + 1;
		log.info("VALOR DEL CONTADOR =" + contador);
		//// fin prueba
		try {
			tm.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ventaCabecera = new VentaCabecera();
		ventaCabecera.setCliente(ventaCab.getCliente());
		em.persist(ventaCabecera); // guarda esta cabecera para luego poder agregarle su detalle
		log.info("Registering Venta" + ventaCabecera.getId_ventaCabecera());
		try {
			transaction = tm.suspend();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	    public void agregarItem(List <VentaDetalle> ventaDetallesList) {
			try {
				tm.resume(transaction);
			} catch (InvalidTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Float montoTotal = 0.0F;
	        VentaDetalle detalle;
			if (ventaCabecera.getDetalles() == null) {
				ventaCabecera.setDetalles(ventaDetallesList);
				for (int i = 0; i < ventaDetallesList.size(); i++) {
					detalle = ventaDetallesList.get(i);
					montoTotal = (detalle.getProducto().getPrecioVenta() * detalle.getCantidad()) + montoTotal;
					detalle.setVentaCabecera(ventaCabecera);
					registerVentaDetalle(detalle);
				}
			} else {
				// itera en la lista de detalles guardando uno por uno
				for (int i = 0; i < ventaDetallesList.size(); i++) {
					detalle = ventaDetallesList.get(i);
					montoTotal = (detalle.getProducto().getPrecioVenta() * detalle.getCantidad()) + montoTotal;
					detalle.setVentaCabecera(ventaCabecera);
					registerVentaDetalle(detalle);
					ventaCabecera.getDetalles().add(detalle);
				}
			}
			//Actualizar la deuda del cliente
            Float deuda = ventaCabecera.getCliente().getSaldoDeuda() + montoTotal;
        	ventaCabecera.getCliente().setSaldoDeuda(deuda);
            clienteRegistration.updateCliente(ventaCabecera.getCliente());
            
	        //actualiza la cabecera 
	        ventaCabecera.setFecha(new java.util.Date());
	        ventaCabecera.setMonto(montoTotal);
	        updateVentaCabecera(ventaCabecera);
	        
	        try {
				transaction = tm.suspend();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public void eliminarItem(VentaDetalle ventaDetalle) {
	    	
			try {
				tm.resume(transaction);
			} catch (InvalidTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SystemException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
	    	log.info("Deleted Detalle" + ventaDetalle.getId_ventaDetalle());
	    		CompraDetalle remover = em.find(CompraDetalle.class, ventaDetalle.getId_ventaDetalle());
	    	     em.remove(remover);
	    	     ventaCabecera.getDetalles().remove(ventaDetalle);
	    	     updateVentaCabecera(ventaCabecera);
	    	
	        try {
				transaction = tm.suspend();
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
    public void registerVentaCabecera(VentaCabecera ventaCabecera) {
    	em.persist(ventaCabecera);
    	log.info("Registering Venta" + ventaCabecera.getId_ventaCabecera());
    	ventaCabeceraEventSrc.fire(ventaCabecera);
    }
    
    @Remove 
    public void cancelarVenta() {
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
    public void confirmarVenta() {
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
    
    public void updateVentaCabecera(VentaCabecera ventaCabecera) {
    	log.info("Updated Venta" + ventaCabecera.getId_ventaCabecera());
    	em.merge(ventaCabecera);
    	ventaCabeceraEventSrc.fire(ventaCabecera);
    }
    
    public void registerVentaDetalle(VentaDetalle ventaDetalle){
    	em.persist(ventaDetalle);
    	log.info("Registering Detalle" + ventaDetalle.getId_ventaDetalle());
    	ventaDetalleEventSrc.fire(ventaDetalle);
    }
    
	@PreDestroy
	public void preDestroy() {
		log.info("INFO PREDESTROY: " + "@PreDestroy");
	}
	
	@PostConstruct
	public void postConstruct() throws NamingException {
		InitialContext ctx = new InitialContext();
    	session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
		try {
	        // JBoss
	        tm= (TransactionManager)
	            ctx.lookup("java:jboss/TransactionManager");
	    }
	    catch (Exception e) { }
	}
}

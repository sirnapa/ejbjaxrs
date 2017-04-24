package py.pol.una.ii.pw.service;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.SessionScoped;
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

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

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
	
	private SqlSession session;
    
	 public void iniciarCompra(CompraCabecera compraCab) {
		 int insertCab;
		try {
			tm.begin();
	    	//SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		compraCabecera = new CompraCabecera();
		compraCabecera.setProveedor(compraCab.getProveedor());
		insertCab = session.insert("insertCompraCab", compraCabecera);
		//em.persist(compraCabecera); // guarda esta cabecera para luego poder agregarle su detalle
		log.info("Registering Compra" + compraCabecera.getId_compraCabecera());
		try {
			transaction = tm.suspend();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
    public void agregarItem(List <CompraDetalle> compraDetalleList) {
		///// prueba
		contador = contador + 1;
		log.info("VALOR DEL CONTADOR =" + contador);
		//// fin prueba
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
        CompraDetalle detalle;
		if (compraCabecera.getDetalles() == null) {
			compraCabecera.setDetalles(compraDetalleList);
			for (int i = 0; i < compraDetalleList.size(); i++) {
				detalle = compraDetalleList.get(i);
				montoTotal = (detalle.getProducto().getPrecioCompra() * detalle.getCantidad()) + montoTotal;
				detalle.setCompraCabecera(compraCabecera);
				registerCompraDetalle(detalle);
			}
		} else {
			// itera en la lista de detalles guardando uno por uno
			for (int i = 0; i < compraDetalleList.size(); i++) {
				detalle = compraDetalleList.get(i);
				montoTotal = (detalle.getProducto().getPrecioCompra() * detalle.getCantidad()) + montoTotal;
				detalle.setCompraCabecera(compraCabecera);
				registerCompraDetalle(detalle);
				compraCabecera.getDetalles().add(detalle);
			}
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
    	log.info("Updated Compra" + compraCabecera.getId_compraCabecera());
    	int insertSuccess = session.update("updateCompraCab", compraCabecera);
    	//em.merge(compraCabecera);
    	compraCabeceraEventSrc.fire(compraCabecera);
    }
    
    public void eliminarItem(CompraDetalle compraDetalle) {
    	
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
		
    	log.info("Deleted Detalle" + compraDetalle.getId_compraDetalle());
    		CompraDetalle remover = em.find(CompraDetalle.class, compraDetalle.getId_compraDetalle());
    	     //em.remove(remover);
    		 session.delete("deleteCompraDetalleById", remover.getId_compraDetalle()); 
    	     compraCabecera.getDetalles().remove(compraDetalle);
    	     updateCompraCabecera(compraCabecera);
    	
        try {
			transaction = tm.suspend();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void registerCompraDetalle(CompraDetalle compraDetalle){
    	int insertCab = session.insert("insertCompraDet", compraDetalle);
    	//em.persist(compraDetalle);
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
		session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
		try {
	        // JBoss
	        tm= (TransactionManager)
	            ctx.lookup("java:jboss/TransactionManager");
	    }
	    catch (Exception e) { }
	}
}

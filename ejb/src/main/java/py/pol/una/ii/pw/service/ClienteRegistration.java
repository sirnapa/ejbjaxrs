package py.pol.una.ii.pw.service;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.ibatis.session.SqlSession;

import py.pol.una.ii.pw.model.Cliente;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

import java.util.logging.Logger;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Stateless
public class ClienteRegistration {

	@Inject
	private Logger log;

	@Inject
	private EntityManager em;

	@Inject
	private Event<Cliente> clienteEventSrc;
	private SqlSession session;
    @PostConstruct
    void init(){
    	session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
    }
    
	public void register(Cliente cliente) throws Exception {
		
		try {
			int insertSuccess = session.insert("insertCliente", cliente);
		} catch (Exception e) {
			log.info(e.toString());
		} finally {
			session.close();
		}
		// log.info("Registering " + cliente.getNombre());
		// cliente.setSaldoDeuda(0.0F);
		// em.persist(cliente);
		// clienteEventSrc.fire(cliente);
	}

	public Cliente deleteCliente(Long id) {
    	
        session.delete("deleteClienteById", id);       
//		Cliente cliente = em.find(Cliente.class, id);
//		if (cliente != null) {
//			log.info("Deleting " + cliente.getNombre());
//			em.remove(cliente);
//		}
		return null;

	}

	public Cliente updateCliente(Cliente cliente) {
    	
        session.delete("updateCliente", cliente);      
//		Cliente clienteAnterior = em.find(Cliente.class, cliente.getId());
//		if (clienteAnterior != null) {
//			log.info("Updated " + clienteAnterior.getNombre());
//			cliente.setId(clienteAnterior.getId());
//			em.merge(cliente);
//		}
		return null;
	}
}

package py.pol.una.ii.pw.service;


import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import py.pol.una.ii.pw.model.Cliente;

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

    public void register(Cliente cliente) throws Exception {
    	log.info("Registering " + cliente.getNombre());
    	cliente.setSaldoDeuda(0.0F);
        em.persist(cliente);
        clienteEventSrc.fire(cliente);
    }
    
    public Cliente deleteCliente(Long id){
    	Cliente cliente = em.find(Cliente.class, id);
    	if (cliente!=null){
        	log.info("Deleting " + cliente.getNombre());
    		em.remove(cliente);
    	}
    	return cliente;

    }
    
    public Cliente updateCliente(Cliente cliente){
    	Cliente clienteAnterior = em.find(Cliente.class, cliente.getId());
    	if (clienteAnterior!=null){
        	log.info("Updated " + clienteAnterior.getNombre());
        	cliente.setId(clienteAnterior.getId());        			
    		em.merge(cliente);
    	}
    	return cliente;
    }
}


package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import py.pol.una.ii.pw.model.Cliente;

@RequestScoped
public class ClienteListProducer {

    @Inject
    private ClienteRepository clienteRepository;

    private List<Cliente> clientes;

    @Produces
    @Named
    public List<Cliente> getClientes() {
        return clientes;
    }

    public void onClienteListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Cliente member) {
        retrieveAllClientesOrderedByName();
    }

    @PostConstruct
    public void retrieveAllClientesOrderedByName() {
        clientes = clienteRepository.findAllOrderedByName();
    }
}

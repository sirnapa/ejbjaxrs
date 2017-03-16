
package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import py.pol.una.ii.pw.model.Proveedor;

@RequestScoped
public class ProveedorListProducer {

    @Inject
    private ProveedorRepository proveedorRepository;

    private List<Proveedor> proveedors;

    @Produces
    @Named
    public List<Proveedor> getProveedors() {
        return proveedors;
    }

    public void onProveedorListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Proveedor member) {
        retrieveAllProveedorsOrderedByName();
    }

    @PostConstruct
    public void retrieveAllProveedorsOrderedByName() {
        proveedors = proveedorRepository.findAllOrderedByName();
    }
}

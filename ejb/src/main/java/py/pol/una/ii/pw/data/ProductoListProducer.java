
package py.pol.una.ii.pw.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import py.pol.una.ii.pw.model.Producto;

@RequestScoped
public class ProductoListProducer {

    @Inject
    private ProductoRepository productoRepository;

    private List<Producto> productos;

    @Produces
    @Named
    public List<Producto> getProductos() {
        return productos;
    }

    public void onProductoListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Producto member) {
        retrieveAllProductosOrderedByName();
    }

    @PostConstruct
    public void retrieveAllProductosOrderedByName() {
        productos = productoRepository.findAllOrderedByName();
    }
}

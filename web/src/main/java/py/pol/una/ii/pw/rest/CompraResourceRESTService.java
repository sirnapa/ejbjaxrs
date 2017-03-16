package py.pol.una.ii.pw.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.service.CompraRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the ventaCabecera and ventaDetalle table.
 */
@Path("/compras")
@RequestScoped
public class CompraResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private CompraRepository repository;

    @Inject
    CompraRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompraCabecera> listAllCompras() {
    	List<CompraCabecera> ventas = repository.findAllOrderedById();
    	log.info("Objeto a retornar" + ventas.size());
        return ventas;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompraCabecera lookupCompraById(@PathParam("id") Long id) {
    	CompraCabecera venta = repository.findById(id);
        if (venta == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta;
    }

    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	//crea una cabecera para los detalles recibidos en lista
        	CompraCabecera compraCabecera = new CompraCabecera();
        	if (lista.size()>0) {
        		compraCabecera.setProveedor(lista.get(0).getCompraCabecera().getProveedor());
        	}
        	//guarda esta cabecera para poder asociarle su detalle
            registration.registerCompraCabecera(compraCabecera);
            Float montoTotal = 0.0F;
            CompraDetalle detalle;
            //itera en la lista de detalles guardando uno por uno
            for(int x=0;x<lista.size();x++) {
            	detalle= lista.get(x);
            	montoTotal = (detalle.getProducto().getPrecioCompra() * detalle.getCantidad()) + montoTotal;
            	detalle.setCompraCabecera(compraCabecera);
            	registration.registerCompraDetalle(detalle);
            	}
            
            //actualiza la cabecera 
            compraCabecera.setFecha(new java.util.Date());
            compraCabecera.setMonto(montoTotal);
            registration.updateCompraCabecera(compraCabecera);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id:[0-9][0-9]*}/detalles")
    public List<CompraDetalle> listAllComprasDetalles(@PathParam("id") Long id) {
    	List<CompraDetalle> det = repository.findAllDetalles(id);
    	log.info("Objeto a retornar" + det.size());
        return det;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/detalles")
    public List<CompraDetalle> listAllDet() {
    	List<CompraDetalle> ventas = repository.findAllDeta();
    	log.info("Objeto a retornar" + ventas.size());
        return ventas;
    }
    
}

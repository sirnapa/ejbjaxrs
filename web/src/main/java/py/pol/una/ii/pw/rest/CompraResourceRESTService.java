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
    @Path("/iniciarCompra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarCompra(CompraCabecera compraCab) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.iniciarCompra(compraCab);
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @POST
    @Path("/agregarItem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarItemCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.agregarItem(lista);
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @POST
    @Path("/eliminarItem")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarItemCompra(CompraDetalle item) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.eliminarItem(item);
             //Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @POST
    @Path("/cancelarCompra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelarCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.cancelarCompra();
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @POST
    @Path("/confirmarCompra")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmarCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.confirmarCompra();
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

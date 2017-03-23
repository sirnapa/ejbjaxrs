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

import py.pol.una.ii.pw.data.VentaRepository;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.VentaDetalle;
import py.pol.una.ii.pw.service.ClienteRegistration;
import py.pol.una.ii.pw.service.VentaRegistration;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the ventaCabecera and ventaDetalle table.
 */
@Path("/ventas")
@RequestScoped
public class VentaResourceRESTService {
    @Inject
    private Logger log;

    @Inject
    private VentaRepository repository;

    @Inject
    VentaRegistration registration;
    
    @Inject
    ClienteRegistration clienteRegistration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<VentaCabecera> listAllVentas() {
    	List<VentaCabecera> ventas = repository.findAllOrderedById();
    	log.info("Objeto a retornar" + ventas.size());
        return ventas;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public VentaCabecera lookupVentaById(@PathParam("id") Long id) {
    	VentaCabecera venta = repository.findById(id);
        if (venta == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return venta;
    }
    
    @POST
    @Path("/iniciarVenta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarVenta(VentaCabecera ventaCab) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.iniciarVenta(ventaCab);
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
    public Response agregarItemVenta(ArrayList<VentaDetalle> lista) {
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
    public Response eliminarItemVenta(VentaDetalle item) {
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
    @Path("/cancelarVenta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelarCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.cancelarVenta();
            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce){
        	log.info(ce.getMessage());
        }
        return builder.build();
    }
    
    @POST
    @Path("/confirmarVenta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmarCompra(ArrayList<CompraDetalle> lista) {
    	Response.ResponseBuilder builder = null;
        try {
        	registration.confirmarVenta();
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
    public List<VentaDetalle> listAllVentasDetalles(@PathParam("id") Long id) {
    	List<VentaDetalle> det = repository.findAllDetalles(id);
    	log.info("Objeto a retornar" + det.size());
        return det;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/detalles")
    public List<VentaDetalle> listAllDet() {
    	List<VentaDetalle> ventas = repository.findAllDeta();
    	log.info("Objeto a retornar" + ventas.size());
        return ventas;
    }
    
}

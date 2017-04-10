package py.pol.una.ii.pw.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import py.pol.una.ii.pw.data.VentaRepository;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.VentaDetalle;
import py.pol.una.ii.pw.service.ClienteRegistration;
import py.pol.una.ii.pw.service.VentaMasiva;
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
    private VentaMasiva masivo;
    
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
    
	@POST
	@Path("/ventaMasiva")
	@Consumes(MediaType.MULTIPART_FORM_DATA)  
	public Response uploadFile(MultipartFormDataInput input) {
		
		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (InputPart inputPart : inputParts) {

		 try {

			MultivaluedMap<String, String> header = inputPart.getHeaders();
			fileName = getFileName(header);

			//convertir el archivo a inoutstream
			InputStream inputStream = inputPart.getBody(InputStream.class,null);

			byte [] bytes = IOUtils.toByteArray(inputStream);

			//construimos la ruta del archivo
			fileName = "C:\\Users\\user\\Desktop\\" + fileName;

			FileReader fileReader = writeFile(bytes,fileName);
			masivo.ventaMasiva(fileReader);
		  } catch (IOException e) {
			e.printStackTrace();
		  }
		}

		return Response.status(200).build();
	}
	
	private String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}
	
	private FileReader writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();
		FileReader fr = new FileReader(file);
		return fr;
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
    
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allVentas")
    public List<String> getMasivo() throws SQLException, NamingException, IOException{
    	FileInputStream inputStream = null;
    	Scanner sc = null;
    	List<String> resultado = new ArrayList<String>();
    	File file = masivo.getAllVentas();
		inputStream = new FileInputStream("C:\\Users\\user\\Desktop\\" + file.getName());
		sc = new Scanner(inputStream, "UTF-8");
		while (sc.hasNextLine()) {
			String linea = sc.nextLine();
			resultado.add(linea);
		}
		sc.close();
    	return  resultado;
    }
}

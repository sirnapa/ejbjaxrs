package py.pol.una.ii.pw.rest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.json.Json;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.apache.commons.io.IOUtils;
import org.glassfish.json.JsonProviderImpl;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import py.pol.una.ii.pw.data.CompraRepository;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;
import py.pol.una.ii.pw.service.CompraMasiva;
import py.pol.una.ii.pw.service.CompraRegistration;
import javax.ws.rs.core.StreamingOutput;

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
    private EntityManager em;

    @Inject
    private CompraMasiva masivo;
    
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

	@POST
	@Path("/compraMasiva")
	@Consumes(MediaType.MULTIPART_FORM_DATA)  
	public Response uploadFile(MultipartFormDataInput input) {
		
		String fileName = "";

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");

		for (InputPart inputPart : inputParts) {

		 try {

			MultivaluedMap<String, String> header = inputPart.getHeaders();
			fileName = getFileName(header);

			//convertir el archivo a inputstream
			InputStream inputStream = inputPart.getBody(InputStream.class,null);

			byte [] bytes = IOUtils.toByteArray(inputStream);

			//construimos la ruta del archivo
			fileName = "C:\\Users\\user\\Desktop\\" + fileName;

			FileReader fileReader = writeFile(bytes,fileName);
			masivo.compraMasiva(fileReader, fileName);
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
	
    protected Response.ResponseBuilder getNoCacheResponseBuilder( Response.Status status ) {
        CacheControl cc = new CacheControl();
        cc.setNoCache( true );
        cc.setMaxAge( -1 );
        cc.setMustRevalidate( true );
        return Response.status( status ).cacheControl( cc );
    }

    private BigInteger queryGeneratedUuidRecordsSize() {
        return (BigInteger)((em.createNativeQuery( "SELECT count(u) FROM compras_cab u").getSingleResult()));
    }
    
    @SuppressWarnings("unchecked")
	private List<CompraCabecera> listAllGeneratedUuidEntities( int recordPosition, int recordsPerRoundTrip ) {
    	     return  em.createNamedQuery( "CompraCabecera.listAll" )
    	    	        .setFirstResult( recordPosition )
    	    	        .setMaxResults( recordsPerRoundTrip )
    	    	        .getResultList();
    	}


	@GET
	@Path( "/list-all" )
	@Produces( "application/json" )
	public Response streamGeneratedUuids() {
	 
	 
	    return getNoCacheResponseBuilder( Response.Status.OK ).entity( new StreamingOutput() {
	 
	        // Instruct how StreamingOutput's write method is to stream the data
	        @Override
	        public void write( OutputStream os ) throws IOException, WebApplicationException {
	            int recordsPerRoundTrip = 100;                      // Number of records for every round trip to the database
	            int recordPosition = 0;                             // Initial record position index
	            BigInteger big = queryGeneratedUuidRecordsSize();
	            int recordSize =  big.intValue();  // Total records found for the query
	            Gson gson = new GsonBuilder().create();
	            // Start streaming the data
	            PrintWriter writer = new PrintWriter( new BufferedWriter( new OutputStreamWriter( os ) ) );
	                writer.print( "{\"result\": [" );
	 
	                while ( recordSize > 0 ) {
	                    // Get the paged data set from the DB
	                    List<CompraCabecera> generatedUuidEntities = listAllGeneratedUuidEntities( recordPosition, recordsPerRoundTrip );
	                    for ( CompraCabecera generatedUuidEntity : generatedUuidEntities ) {
	                        if ( recordPosition > 0 ) {
	                            writer.print( "," );
	                        }
	 
	                        // Stream the data in Json object format
		                   int index=0;
                           List <CompraDetalle> det = (List<CompraDetalle>) generatedUuidEntity.getDetalles();
                           for (CompraDetalle detalle : det){
                           gson.toJson("id_compraCab", writer);
                           gson.toJson(det.get(index).getCompraCabecera().getId_compraCabecera(), writer);
                           index++;
                           log.info(gson.toString());
                           }
//                           StringWriter stringWriter = new StringWriter();
//                           JsonGenerator gen = Json.createGenerator( stringWriter );
//                           gen.writeStartObject();
//                           gen.write("id_CompraCab", det.get(index).getCompraCabecera().getId_compraCabecera());
   	                       writer.print(gson);
//	 
	                        // Increase the recordPosition for every record streamed
	                        recordPosition++;
	                    }
	 
	                    // update the recordSize (remaining no. of records)
	                    recordSize -= recordsPerRoundTrip;
	                }
	 
	                // Done!
	                writer.print( "]}" );
	        }
	    } ).build();
	}

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/allCompras")
    public List<String> getMasivo() throws SQLException, NamingException, IOException{
    	FileInputStream inputStream = null;
    	Scanner sc = null;
    	List<String> resultado = new ArrayList<String>();
    	File file = masivo.getAllCompras();
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

package py.pol.una.ii.pw.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import py.pol.una.ii.pw.data.ClienteRepository;
import py.pol.una.ii.pw.data.ProductoRepository;
import py.pol.una.ii.pw.data.ProveedorRepository;
import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.model.Cliente;
import py.pol.una.ii.pw.model.VentaCabecera;
import py.pol.una.ii.pw.model.VentaDetalle;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class VentaMasiva {

	@Resource(lookup = "java:/PostgresDS")
	DataSource datasource;

	@Inject
	ClienteRepository repository;

	@Inject
	ProductoRepository repositoryProducto;

	@Inject
	private Logger log;
	
    @Resource
	SessionContext ctx;

	@Inject
	private EntityManager em;

	int mb = 1024 * 1024;
	@Resource
	UserTransaction tx;

		private void getUserTransaction() throws NamingException {
		InitialContext ctx = new InitialContext();
		try {
			// JBoss
			tx = (UserTransaction) ctx.lookup("java:jboss/UserTransaction");
		} catch (Exception e) {
		}
	}

	public void ventaMasiva(FileReader fr) throws IOException {
		try {
			getUserTransaction();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			tx.begin();
		} catch (NotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("INICIO");
		System.out.println("MEMORIA TOTAL= " + ((Runtime.getRuntime().totalMemory()) / mb));
		System.out.println(
				"MEMORIA USADA= " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb));
		System.out.println("MEMORIA LIBRE= " + ((Runtime.getRuntime().freeMemory()) / mb));
		FileInputStream inputStream = null;
		Scanner sc = null;
		inputStream = new FileInputStream("C:\\Users\\user\\Desktop\\ejemploFile.txt");
		sc = new Scanner(inputStream, "UTF-8");

		// BufferedReader br = new BufferedReader(fr);
		// String linea = br.readLine();
		int totalCompras = 0;
		while (sc.hasNextLine()) {
			// while ((linea = br.readLine()) != null) {
			String linea = sc.nextLine();
			totalCompras++;
			VentaCabecera compra = parseVenta(linea);
			em.persist(compra);
			Float montoTotal = 0.0F;
			for (VentaDetalle det : compra.getDetalles()) {
				det.setVentaCabecera(compra);
				montoTotal = (det.getProducto().getPrecioCompra() * det.getCantidad()) + montoTotal;
				em.persist(det);
			}
			compra.setFecha(new java.util.Date());
			compra.setMonto(montoTotal);
			em.merge(compra);
			log.info("Registrada venta nro: " + totalCompras + " con Id nro: " + compra.getId_ventaCabecera()
					+ " Cliente: " + compra.getCliente().getNombre());
		}
		sc.close();
		log.info("FIN");
		System.out.println("MEMORIA TOTAL= " + ((Runtime.getRuntime().totalMemory()) / mb));
		System.out.println(
				"MEMORIA USADA= " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb));
		System.out.println("MEMORIA LIBRE= " + ((Runtime.getRuntime().freeMemory()) / mb));
		try {
			tx.commit();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicMixedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeuristicRollbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public VentaCabecera parseVenta(String s) {

		String[] line = s.split(",", 2);
		VentaCabecera compra = new VentaCabecera();
		Cliente provider = repository.findById(Long.parseLong(line[0].substring(10, line[0].length())));
		compra.setCliente(provider);

		Collection<VentaDetalle> detallesList = null;
		detallesList = parseDetalles(line[1]);

		compra.setDetalles(detallesList);
		return compra;
	}

	public Collection<VentaDetalle> parseDetalles(String s) {
		ArrayList<VentaDetalle> listaDetalles = new ArrayList<VentaDetalle>();
		String[] line = s.split(";", 5);
		ArrayList<String> detalle = new ArrayList<String>();
		for (int i = 0; i < line.length; i++) {
			String[] producto = line[i].split("-", 2);
			VentaDetalle det = new VentaDetalle();
			det.setProducto(
					repositoryProducto.findById(Long.parseLong(producto[0].substring(9, producto[0].length()))));
			det.setCantidad((float) ((Integer.parseInt(producto[1].substring(10, producto[1].length())))));
			listaDetalles.add(det);
			detalle.add(line[i]);
		}
		return listaDetalles;
	}
	
	public File getAllVentas() throws SQLException, NamingException, IOException {
		getUserTransaction();
		File archivoIntermedio;

		Connection conn;
		conn = datasource.getConnection();
		conn.setAutoCommit(false);
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY,
				ResultSet.FETCH_FORWARD);
		st.setFetchSize(1000);
		ResultSet rs = st.executeQuery("SELECT * FROM compras_cab");
		List<VentaCabecera> records=new ArrayList<VentaCabecera>();
		int index=0;
		while (rs.next()) {
			VentaCabecera compra = new VentaCabecera();
			compra.setId_ventaCabecera(rs.getLong("id_ventaCabecera"));
			compra.setFecha(rs.getDate("fecha"));
			compra.setMonto(rs.getFloat("monto"));
			compra.setCliente(em.find(Cliente.class, rs.getLong("id_cliente")));
		    records.add(compra);
			System.out.println("MEMORIA USADA= "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb));
			//Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
			//String pretty = prettyGson.toJson(compra);
			index++;
			rs.absolute(index);
		}
		archivoIntermedio = writeFile(records);
		records.clear();
		rs.close();
		st.setFetchSize(0);
		return archivoIntermedio;
	}
	
	private File writeFile(List<VentaCabecera> lista) throws IOException {


		String ruta = "C:\\Users\\user\\Desktop\\GetMasivo.txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        Gson gson = new Gson();
        if(archivo.exists()) {
        	bw = new BufferedWriter(new FileWriter(archivo));
        	for (VentaCabecera item: lista){
        		bw.write(gson.toJson(item));
        		bw.write("\r\n");
        	}
        } else {
            bw = new BufferedWriter(new FileWriter(archivo, true));
        	for (VentaCabecera item: lista){
        		bw.write(gson.toJson(item));
        		bw.write("\r\n");
        	}
        }
        bw.close();
        return archivo;
	}

	

}

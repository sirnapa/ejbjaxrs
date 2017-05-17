package py.pol.una.ii.pw.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

import org.apache.ibatis.session.SqlSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import py.pol.una.ii.pw.data.ProductoRepository;
import py.pol.una.ii.pw.data.ProveedorRepository;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;
import py.pol.una.ii.pw.model.CompraDetalle;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CompraMasiva {

	@Resource(lookup = "java:/WebDS")
	DataSource datasource;

	@Inject
	ProveedorRepository repository;

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

	private SqlSession session;
    @PostConstruct
    void init(){
    	session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
    }
    
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public File getAllCompras() throws SQLException, NamingException, IOException {
		
		//SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
		getUserTransaction();
		File archivoIntermedio;

		Connection conn;
		conn = datasource.getConnection();
		conn.setAutoCommit(false);
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY,
				ResultSet.FETCH_FORWARD);
		st.setFetchSize(1000);
		ResultSet rs = st.executeQuery("SELECT * FROM compras_cab");
		List<CompraCabecera> records=new ArrayList<CompraCabecera>();
		int index=0;
		while (rs.next()) {
			CompraCabecera compra = new CompraCabecera();
			compra.setId_compraCabecera(rs.getLong("id_compraCabecera"));
			compra.setFecha(rs.getDate("fecha"));
			compra.setMonto(rs.getFloat("monto"));
			compra.setProveedor(em.find(Proveedor.class, rs.getLong("id_proveedor")));
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
	
	private File writeFile(List<CompraCabecera> lista) throws IOException {


		String ruta = "C:\\Users\\user\\Desktop\\GetMasivo.txt";
        File archivo = new File(ruta);
        BufferedWriter bw;
        Gson gson = new Gson();
        if(archivo.exists()) {
        	bw = new BufferedWriter(new FileWriter(archivo));
        	for (CompraCabecera item: lista){
        		bw.write(gson.toJson(item));
        		bw.write("\r\n");
        	}
        } else {
            bw = new BufferedWriter(new FileWriter(archivo, true));
        	for (CompraCabecera item: lista){
        		bw.write(gson.toJson(item));
        		bw.write("\r\n");
        	}
        }
        bw.close();
        return archivo;
	}


	private void getUserTransaction() throws NamingException {
		InitialContext ctx = new InitialContext();
		try {
			// JBoss
			tx = (UserTransaction) ctx.lookup("java:jboss/UserTransaction");
		} catch (Exception e) {
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void compraMasiva(FileReader fr, String filename) throws IOException {
		
		log.info("INICIO");
		System.out.println("MEMORIA TOTAL= " + ((Runtime.getRuntime().totalMemory()) / mb));
		System.out.println(
				"MEMORIA USADA= " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb));
		System.out.println("MEMORIA LIBRE= " + ((Runtime.getRuntime().freeMemory()) / mb));
		FileInputStream inputStream = null;
		Scanner sc = null;
		inputStream = new FileInputStream(filename);
		sc = new Scanner(inputStream, "UTF-8");


		int totalCompras = 0;
		int insertCab, insertDet;
		while (sc.hasNextLine()) {
			// while ((linea = br.readLine()) != null) {
			String linea = sc.nextLine();
			totalCompras++;
			CompraCabecera compra = parseCompra(linea);
			insertCab = session.insert("insertCompraCab", compra);
			Float montoTotal = 0.0F;
			for (CompraDetalle det : compra.getDetalles()) {
				det.setCompraCabecera(compra);
				montoTotal = (det.getProducto().getPrecioCompra() * det.getCantidad()) + montoTotal;
				insertCab = session.insert("insertCompraDet", det);
			}
			compra.setFecha(new java.util.Date());
			compra.setMonto(montoTotal);
			int insertSuccess = session.update("updateCompraCab", compra);
			//em.merge(compra);
			log.info("Registrada compra nro: " + totalCompras + " con Id nro: " + compra.getId_compraCabecera()
					+ " Proveedor: " + compra.getProveedor().getNombre());
		}
		sc.close();
		log.info("FIN");
		System.out.println("MEMORIA TOTAL= " + ((Runtime.getRuntime().totalMemory()) / mb));
		System.out.println(
				"MEMORIA USADA= " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb));
		System.out.println("MEMORIA LIBRE= " + ((Runtime.getRuntime().freeMemory()) / mb));
		return;
	}

	public CompraCabecera parseCompra(String s) {

		String[] line = s.split(",", 2);
		CompraCabecera compra = new CompraCabecera();
		Proveedor provider = repository.findById(Long.parseLong(line[0].substring(10, line[0].length())));
		compra.setProveedor(provider);

		Collection<CompraDetalle> detallesList = null;
		detallesList = parseDetalles(line[1]);

		compra.setDetalles(detallesList);
		return compra;
	}

	public Collection<CompraDetalle> parseDetalles(String s) {
		ArrayList<CompraDetalle> listaDetalles = new ArrayList<CompraDetalle>();
		String[] line = s.split(";", 5);
		ArrayList<String> detalle = new ArrayList<String>();
		for (int i = 0; i < line.length; i++) {
			String[] producto = line[i].split("-", 2);
			CompraDetalle det = new CompraDetalle();
			det.setProducto(
					repositoryProducto.findById(Long.parseLong(producto[0].substring(9, producto[0].length()))));
			det.setCantidad((float) ((Integer.parseInt(producto[1].substring(10, producto[1].length())))));
			listaDetalles.add(det);
			detalle.add(line[i]);
		}
		return listaDetalles;
	}

}

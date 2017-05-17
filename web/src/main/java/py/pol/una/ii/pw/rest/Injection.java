/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package py.pol.una.ii.pw.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.ibatis.session.SqlSession;

import py.pol.una.ii.pw.data.ProductoRepository;
import py.pol.una.ii.pw.model.Cliente;
import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.Producto;
import py.pol.una.ii.pw.service.ProductoRegistration;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

@Path("/injection")
@RequestScoped
public class Injection {
	@Inject
	private Logger log;

	@Inject
	private ProductoRepository repository;

	@Inject
	ProductoRegistration registration;

	@Resource(lookup = "java:/WebDS")
	DataSource datasource;

	@POST
	@Path("/sql")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Cliente> sqlInjection(String nombre) throws SQLException {
		List<Cliente> lista=new ArrayList<Cliente>();
		Response.ResponseBuilder builder = null;
			Connection conn;
			conn = datasource.getConnection();
			String query = "SELECT * FROM cliente WHERE nombre='" + nombre + "'";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
		    while (rs.next()) {
		        Cliente c = new Cliente();
		        c.setId(rs.getLong("id_cliente"));
		        c.setCi(rs.getString("ci"));
		        c.setNombre(rs.getString("nombre"));
		        c.setSaldoDeuda(rs.getFloat("saldodeuda"));
		        lista.add(c);
		      }
		return lista;
	}

	@POST
	@Path("/command")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public List<String> commandInjection(String args) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		List<String> lista = new ArrayList<String>();
		String[] cmd = new String[3];
		cmd[0] = "cmd.exe" ;
                cmd[1] = "/C";
                cmd[2] = "echo start" + args;
		Process proc = runtime.exec(cmd);
		
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		
		String line;
		while ((line = br.readLine()) != null) {
			lista.add(line);
		}
		return lista;
	}
	/**
	 * <p>
	 * Validates the given Member variable and throws validation exceptions
	 * based on the type of error. If the error is standard bean validation
	 * errors then it will throw a ConstraintValidationException with the set of
	 * the constraints violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing member with the same email is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param member
	 *            Member to be validated
	 * @throws ConstraintViolationException
	 *             If Bean Validation errors exist
	 * @throws ValidationException
	 *             If member with the same email already exists
	 */

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation
	 * fields, and their message. This can then be used by clients to show
	 * violations.
	 * 
	 * @param violations
	 *            A set of violations that needs to be reported
	 * @return JAX-RS response containing all violations
	 */
	private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
		log.fine("Validation completed. violations found: " + violations.size());

		Map<String, String> responseObj = new HashMap<String, String>();

		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
		}

		return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
	}

	/**
	 * Checks if a member with the same email address is already registered.
	 * This is the only way to easily capture the "@UniqueConstraint(columnNames
	 * = "email")" constraint from the Member class.
	 * 
	 * @param email
	 *            The email to check
	 * @return True if the email already exists, and false otherwise
	 */
	// public boolean emailAlreadyExists(String email) {
	// Producto producto = null;
	// try {
	// producto = repository.findByEmail(email);
	// } catch (NoResultException e) {
	// // ignore
	// }
	// return producto != null;
	// }
}

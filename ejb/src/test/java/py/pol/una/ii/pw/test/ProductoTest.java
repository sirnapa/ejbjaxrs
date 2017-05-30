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
package py.pol.una.ii.pw.test;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import py.pol.una.ii.pw.model.Producto;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;

public class ProductoTest extends FunctionalTest{

	
    @Test
    public void PingTestProducto() {
        given().when().get("/productos").then().statusCode(200);
    }
    
	@Test
	public final void listarTodo() {
        given().when().get("/productos").then()
        .body("nombre", Matchers.hasItem(Matchers.equalTo("Producto 1")));
	}
	
	@Test
    public void productoPost() {
        Map<String,String> producto = new HashMap<String, String>();
        producto.put("id", "null");
        producto.put("nombre", "audi");
        producto.put("precioVenta", "10000");
        producto.put("precioCompra", "20000");
        producto.put("cantidad", "5");

        given()
        .contentType("application/json")
        .body(producto)
        .when().post("/productos").then()
        .statusCode(200);
    }
	
	@Test
    public void productoDelete() {
        given().pathParam("prodID", 579402)
        .when().delete("/productos/{prodID}").then()
        .statusCode(204);

    }
	
	@Test
    public void productoUpdate() {
        Map<String,String> producto = new HashMap<String, String>();
        producto.put("id", "579370");
        producto.put("nombre", "test cambio");
        producto.put("precioVenta", "10000");
        producto.put("precioCompra", "20000");
        producto.put("cantidad", "5");

        given().pathParam("prodID", 579402)
        .contentType("application/json")
        .body(producto)
        .when().put("/productos/{prodID}").then()
        .statusCode(204);

    }
}
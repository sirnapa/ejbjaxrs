package py.pol.una.ii.pw.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement
@Table(name = "Producto")
public class Producto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
	
    @NotNull
    @Size(min = 1, max = 25)
    private String nombre;
    
    @NotNull
    @Column(name = "precioventa")
    private Float precioVenta;
    
    @NotNull
    @Column(name = "preciocompra")
    private Float precioCompra;
    
    @NotNull
    @Column(name = "cantidad")
    private Float cantidad;

	public Float getCantidad() {
		return cantidad;
	}

	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}

	public Float getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(Float precioVenta) {
		this.precioVenta = precioVenta;
	}

	public Float getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(Float precioCompra) {
		this.precioCompra = precioCompra;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}

package py.pol.una.ii.pw.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "Compras_Det")
public class CompraDetalle implements Serializable{
	/** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id_compraDetalle;
	
	@ManyToOne
	@JoinColumn(name="id_producto")
	private Producto producto;

	private Float cantidad;

	@ManyToOne
    @JoinColumn(name = "id_compraCabecera")
	private CompraCabecera compraCabecera;
	
	public Integer getId_compraDetalle() {
		return id_compraDetalle;
	}

	public void setId_compraDetalle(Integer id_compraDetalle) {
		this.id_compraDetalle = id_compraDetalle;
	}

	public Float getCantidad() {
		return cantidad;
	}

	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}

	public CompraCabecera getCompraCabecera() {
		return compraCabecera;
	}

	public void setCompraCabecera(CompraCabecera compraCabecera) {
		this.compraCabecera = compraCabecera;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}
}

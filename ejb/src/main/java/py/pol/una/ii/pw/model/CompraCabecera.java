package py.pol.una.ii.pw.model;

import java.io.Serializable;
import java.util.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
//import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "Compras_Cab")
public class CompraCabecera implements Serializable{
	/** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id_compraCabecera;

	@ManyToOne
	@JoinColumn(name="id_proveedor")
	private Proveedor proveedor;
	
	private Date fecha;
	private Float monto;
	

	public Long getId_compraCabecera() {
		return id_compraCabecera;
	}
	public void setId_compraCabecera(Long id_compraCabecera) {
		this.id_compraCabecera = id_compraCabecera;
	}
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Float getMonto() {
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
}

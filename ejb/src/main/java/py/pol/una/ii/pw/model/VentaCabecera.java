package py.pol.una.ii.pw.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;

@Entity
@Table(name = "Ventas_Cab")
public class VentaCabecera implements Serializable{
	/** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id_ventaCabecera;
	
	@ManyToOne
	@JoinColumn(name="id_cliente")
	private Cliente cliente;
	
	@OneToMany(mappedBy = "ventaCabecera")
	private Collection<VentaDetalle> detalles;
	
	private Date fecha;
	private Float monto;

	public Collection<VentaDetalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(Collection<VentaDetalle> detalles) {
		this.detalles = detalles;
	}

	public Long getId_ventaCabecera() {
		return id_ventaCabecera;
	}

	public void setId_ventaCabecera(Long id_ventaCabecera) {
		this.id_ventaCabecera = id_ventaCabecera;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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


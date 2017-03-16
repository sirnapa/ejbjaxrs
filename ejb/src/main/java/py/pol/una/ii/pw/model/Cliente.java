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


@Entity //anotation 
@XmlRootElement
@Table(name = "Cliente")
public class Cliente implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue
    @Column(name = "id_cliente")
    private Long id;
	
    @NotNull
    @Size(min = 1, max = 25)
    private String nombre;
   

	@NotNull
    @Size(min = 1, max = 25)
    private String ci;

	private Float saldoDeuda;

	public Float getSaldoDeuda() {
		return saldoDeuda;
	}

	public void setSaldoDeuda(Float saldoDeuda) {
		this.saldoDeuda = saldoDeuda;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
    public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}
}

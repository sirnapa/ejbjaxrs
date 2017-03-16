
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

import py.pol.una.ii.pw.model.VentaDetalle;
import py.pol.una.ii.pw.model.VentaCabecera;

@ApplicationScoped
public class VentaRepository {

    @Inject
    private EntityManager em;

    public VentaCabecera findById(Long id) {
        return em.find(VentaCabecera.class, id);
    }

    public List<VentaCabecera> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VentaCabecera> criteria = cb.createQuery(VentaCabecera.class);
        Root<VentaCabecera> ventaCab = criteria.from(VentaCabecera.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(ventaCab).orderBy(cb.asc(ventaCab.get(VentaCabecera_.name)));
        criteria.select(ventaCab).orderBy(cb.asc(ventaCab.get("id_ventaCabecera")));
        return em.createQuery(criteria).getResultList();
    }

    public List<VentaDetalle> findAllDeta() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VentaDetalle> criteria = cb.createQuery(VentaDetalle.class);
        Root<VentaDetalle> ventaDet = criteria.from(VentaDetalle.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(ventaCab).orderBy(cb.asc(ventaCab.get(VentaCabecera_.name)));
        criteria.select(ventaDet).orderBy(cb.asc(ventaDet.get("id_ventaDetalle")));
        return em.createQuery(criteria).getResultList();
    }
    

    public List<VentaDetalle> findAllDetalles(Long id) {
    	  CriteriaBuilder cb = em.getCriteriaBuilder();
    	  
    	  CriteriaQuery<VentaDetalle> q = cb.createQuery(VentaDetalle.class);
    	  Root<VentaDetalle> c = q.from(VentaDetalle.class);
    	  ParameterExpression<VentaCabecera> p = cb.parameter(VentaCabecera.class);
    	  q.select(c).where(cb.equal(c.get("ventaCabecera"), p));
    	  TypedQuery<VentaDetalle> query = em.createQuery(q);
    	  query.setParameter(p, this.findById(id));
    	  List<VentaDetalle> results = query.getResultList();
    	  return results;
      }
}


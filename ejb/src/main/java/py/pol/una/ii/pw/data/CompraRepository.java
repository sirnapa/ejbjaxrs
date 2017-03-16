
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

import py.pol.una.ii.pw.model.CompraCabecera;
import py.pol.una.ii.pw.model.CompraDetalle;

@ApplicationScoped
public class CompraRepository {

    @Inject
    private EntityManager em;

    public CompraCabecera findById(Long id) {
        return em.find(CompraCabecera.class, id);
    }

    public List<CompraCabecera> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CompraCabecera> criteria = cb.createQuery(CompraCabecera.class);
        Root<CompraCabecera> compraCab = criteria.from(CompraCabecera.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(compraCab).orderBy(cb.asc(compraCab.get(CompraCabecera_.name)));
        criteria.select(compraCab).orderBy(cb.asc(compraCab.get("id_compraCabecera")));
        return em.createQuery(criteria).getResultList();
    }

    //encuentra todos los detalles
    public List<CompraDetalle> findAllDeta() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CompraDetalle> criteria = cb.createQuery(CompraDetalle.class);
        Root<CompraDetalle> compraDet = criteria.from(CompraDetalle.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(compraCab).orderBy(cb.asc(compraCab.get(CompraCabecera_.name)));
        criteria.select(compraDet).orderBy(cb.asc(compraDet.get("id_compraDetalle")));
        return em.createQuery(criteria).getResultList();
    }
    
    //encuentra los detalles para la compra especificada por el id
    public List<CompraDetalle> findAllDetalles(Long id) {
    	  CriteriaBuilder cb = em.getCriteriaBuilder();
    	  
    	  CriteriaQuery<CompraDetalle> q = cb.createQuery(CompraDetalle.class);
    	  Root<CompraDetalle> c = q.from(CompraDetalle.class);
    	  ParameterExpression<CompraCabecera> p = cb.parameter(CompraCabecera.class);
    	  q.select(c).where(cb.equal(c.get("compraCabecera"), p));
    	  TypedQuery<CompraDetalle> query = em.createQuery(q);
    	  query.setParameter(p, this.findById(id));
    	  List<CompraDetalle> results = query.getResultList();
    	  return results;
      }
}


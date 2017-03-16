
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
import py.pol.una.ii.pw.model.Pago;
import py.pol.una.ii.pw.model.VentaCabecera;

@ApplicationScoped
public class PagoRepository {

    @Inject
    private EntityManager em;

    public Pago findById(Long id) {
        return em.find(Pago.class, id);
    }

    public List<Pago> findAllOrderedById() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pago> criteria = cb.createQuery(Pago.class);
        Root<Pago> p = criteria.from(Pago.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(p).orderBy(cb.asc(p.get(Pago_.name)));
        criteria.select(p).orderBy(cb.asc(p.get("id")));
        return em.createQuery(criteria).getResultList();
    }
}


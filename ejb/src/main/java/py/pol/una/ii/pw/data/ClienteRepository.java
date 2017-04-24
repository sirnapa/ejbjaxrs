
package py.pol.una.ii.pw.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

import py.pol.una.ii.pw.model.Cliente;
import py.pol.una.ii.pw.model.Proveedor;
import py.pol.una.ii.pw.util.MyBatisSqlSessionFactory;

@ApplicationScoped
public class ClienteRepository {

    @Inject
    private EntityManager em;

    public Cliente findById(Long id) {
    	SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
        Cliente cli = session.selectOne("getClienteById",id);
        return cli;
       // return em.find(Cliente.class, id);
    }

    public List<Cliente> findAllOrderedByName() {
    	SqlSession session = MyBatisSqlSessionFactory.getSqlSessionFactory().openSession();
        List<Cliente> clientes = session.selectList("selectAllClientes", null);
        return clientes;
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Cliente> criteria = cb.createQuery(Cliente.class);
//        Root<Cliente> cliente = criteria.from(Cliente.class);
//        criteria.select(cliente).orderBy(cb.asc(cliente.get("nombre")));
//        return em.createQuery(criteria).getResultList();
    }

}

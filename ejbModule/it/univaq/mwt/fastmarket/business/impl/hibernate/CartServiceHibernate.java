package it.univaq.mwt.fastmarket.business.impl.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.CartService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Cart;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@Stateless
@Remote(CartService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CartServiceHibernate implements CartService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public CartServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Cart> getAllCarts() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<Cart> carts = session.createQuery("from Cart c INNER JOIN fetch c.user").list();
		Set<Cart> result = new HashSet<Cart>(carts);

		return result;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Cart> findAllCartsPaginated(RequestGrid requestGrid)
			throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(Cart.class, "c")
				.setFetchMode("c.user", FetchMode.JOIN)
				.createAlias("c.user", "user"); // inner join by default
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("c.id");
		} else if ("user.username".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("user.username");
		} else {
			requestGrid.setSortCol("c." + requestGrid.getSortCol());
		}

		if (!"".equals(requestGrid.getSortCol())
				&& !"".equals(requestGrid.getSortDir())) {
			if ("asc".equals(requestGrid.getSortDir())) {
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));
			} else {
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));
			}
		}

		if (!"".equals(requestGrid.getsSearch())) {
			criteria.add(Restrictions
					.like("c.name",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(Cart.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<Cart> carts = criteria.list();
		
		return new ResponseGrid<Cart>(requestGrid.getsEcho(), records, records,
				carts);

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Cart cart) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(cart);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Cart findCartById(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Cart where id=:id");
		query.setParameter("id", id);
		Cart cart = (Cart) query.uniqueResult();
		if (cart == null) {
			throw new BusinessException("Cart not found");
		} else {
			return cart;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Cart cart) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(cart);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Cart cart) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.delete(cart);
	}

}

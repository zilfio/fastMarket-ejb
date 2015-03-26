package it.univaq.mwt.fastmarket.business.impl.hibernate;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.CartLineService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.CartLine;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(CartLineService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CartLineServiceHibernate implements CartLineService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public CartLineServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<CartLine> findAllCartLinesPaginated(RequestGrid requestGrid) throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(CartLine.class, "cl")
				.setFetchMode("cl.product", FetchMode.JOIN)
				.createAlias("cl.product", "product");

		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("cl.id");
		} else if ("product.name".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("product.name");
		} else {
			requestGrid.setSortCol("cl." + requestGrid.getSortCol());
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
					.like("product.name",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(CartLine.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<CartLine> cartLines = criteria.list();

		return new ResponseGrid<CartLine>(requestGrid.getsEcho(), records,
				records, cartLines);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(CartLine cartLine) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(cartLine);

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public CartLine findCartLineById(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from CartLine where id=:id");
		query.setParameter("id", id);
		CartLine cartLine = (CartLine) query.uniqueResult();
		if (cartLine == null) {
			throw new BusinessException("CartLine not found");
		} else {
			return cartLine;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(CartLine cartLine) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(cartLine);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(CartLine cartLine) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.delete(cartLine);
	}

}

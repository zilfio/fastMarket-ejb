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
import it.univaq.mwt.fastmarket.business.DeliveryService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Delivery;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(DeliveryService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DeliveryServiceHibernate implements DeliveryService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public DeliveryServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Delivery> findAllDeliveriesPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(Delivery.class,"d")
				.setFetchMode("d.cart", FetchMode.JOIN)
				.createAlias("d.cart", "cart"); // inner join by default
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("d.id");
		} else if ("cart.name".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("cart.name");
		} else {
			requestGrid.setSortCol("d." + requestGrid.getSortCol());
		}
	
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("cart.name", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(Delivery.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<Delivery> deliveries = criteria.list();
		
		return new ResponseGrid<Delivery>(requestGrid.getsEcho(), records, records, deliveries);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Delivery delivery) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(delivery);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Delivery findDeliveryByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Delivery where id=:id");
		query.setParameter("id", id);
		Delivery delivery = (Delivery) query.uniqueResult();
		if(delivery == null) {
			throw new BusinessException("Delivery not found");
		} else {
			return delivery;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Delivery delivery) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(delivery);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Delivery delivery) throws BusinessException {
		Session session = sessionFactory.openSession();
		Delivery delivery2 = (Delivery) session.load(Delivery.class, delivery.getId());
		session.delete(delivery2);
	}

}

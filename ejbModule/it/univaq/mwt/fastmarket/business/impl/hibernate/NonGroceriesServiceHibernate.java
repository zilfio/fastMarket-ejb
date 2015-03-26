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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.NonGroceriesService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.NonGrocery;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(NonGroceriesService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class NonGroceriesServiceHibernate implements NonGroceriesService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public NonGroceriesServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<NonGrocery> findAllNonGroceriesPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(NonGrocery.class,"ng");
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("ng.id");
		} else {
			requestGrid.setSortCol("ng." + requestGrid.getSortCol());
		}
	
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("ng.name", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(NonGrocery.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<NonGrocery> nonGroceries = criteria.list();
		
		return new ResponseGrid<NonGrocery>(requestGrid.getsEcho(), records, records, nonGroceries);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(NonGrocery nonGrocery) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(nonGrocery);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public NonGrocery findNonGroceryByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from NonGrocery where id=:id");
		query.setParameter("id", id);
		NonGrocery nonGrocery = (NonGrocery) query.uniqueResult();
		if(nonGrocery == null) {
			throw new BusinessException("NonGrocery not found");
		} else {
			return nonGrocery;
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(NonGrocery nonGrocery) throws BusinessException {	
		Session session = sessionFactory.openSession();
		session.merge(nonGrocery);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(NonGrocery nonGrocery) throws BusinessException {
		Session session = sessionFactory.openSession();
		NonGrocery nonGrocery2 = (NonGrocery) session.load(NonGrocery.class, nonGrocery.getId());
		session.delete(nonGrocery2);
	}

}

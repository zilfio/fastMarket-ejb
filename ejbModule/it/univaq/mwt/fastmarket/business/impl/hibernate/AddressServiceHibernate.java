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

import it.univaq.mwt.fastmarket.business.AddressService;
import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Address;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(AddressService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class AddressServiceHibernate implements AddressService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public AddressServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Address> findAllAddressesPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(Address.class,"a");
		
		// ordinamento colonne
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("a.id");
		} else {
			requestGrid.setSortCol("a." + requestGrid.getSortCol());
		}
	
		// ordinamento colonna: asc o disc
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		// casella di ricerca
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("a.street", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(Address.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<Address> addresses = criteria.list();
		
		return new ResponseGrid<Address>(requestGrid.getsEcho(), records, records, addresses);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Address address) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(address);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Address findAddressByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Address where id=:id");
		query.setParameter("id", id);
		Address address = (Address) query.uniqueResult();
		if(address == null) {
			throw new BusinessException("Address not found");
		} else {
			return address;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Address address) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(address);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Address address) throws BusinessException {
		Session session = sessionFactory.openSession();
		Address address2 = (Address) session.load(Address.class, address.getId());
		session.delete(address2);
	}

}

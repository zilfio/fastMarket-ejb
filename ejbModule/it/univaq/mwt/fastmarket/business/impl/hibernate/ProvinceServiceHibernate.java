package it.univaq.mwt.fastmarket.business.impl.hibernate;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.ProvinceService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Province;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@Stateless
@Remote(ProvinceService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ProvinceServiceHibernate implements ProvinceService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public ProvinceServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Province> getAllProvinces() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<Province> provinces = session.createQuery("from Province").list();
		Set<Province> result = new HashSet<Province>(provinces);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Province> findAllProvincesPaginated(
			RequestGrid requestGrid) throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(Province.class, "p");

		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("p.id");
		} else {
			requestGrid.setSortCol("p." + requestGrid.getSortCol());
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
					.like("p.name",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(Province.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<Province> provinces = criteria.list();

		return new ResponseGrid<Province>(requestGrid.getsEcho(), records,
				records, provinces);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Province province) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(province);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Province findProvinceByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Province where id=:id");
		query.setParameter("id", id);
		Province province = (Province) query.uniqueResult();
		if (province == null) {
			throw new BusinessException("Province not found");
		} else {
			return province;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Province province) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(province);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Province province) throws BusinessException {
		Session session = sessionFactory.openSession();
		Province province2 = (Province) session.load(Province.class,
				province.getId());
		session.delete(province2);
	}

}

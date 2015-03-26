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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.DistrictService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.District;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(DistrictService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DistrictServiceHibernate implements DistrictService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public DistrictServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<District> getAllDistricts() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<District> districts = session.createQuery("from District").list();
		Set<District> result = new HashSet<District>(districts);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<District> findAllDistrictsPaginated(
			RequestGrid requestGrid) throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(District.class, "d");

		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("d.id");
		} else {
			requestGrid.setSortCol("d." + requestGrid.getSortCol());
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
					.like("d.name",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(District.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<District> districts = criteria.list();

		return new ResponseGrid<District>(requestGrid.getsEcho(), records,
				records, districts);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(District district) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(district);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public District findDistrictByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from District where id=:id");
		query.setParameter("id", id);
		District district = (District) query.uniqueResult();
		if (district == null) {
			throw new BusinessException("District not found");
		} else {
			return district;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(District district) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(district);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(District district) throws BusinessException {
		Session session = sessionFactory.openSession();
		District district2 = (District) session.load(District.class,
				district.getId());
		session.delete(district2);
	}
}

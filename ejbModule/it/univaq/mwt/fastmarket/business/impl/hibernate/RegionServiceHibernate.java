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
import it.univaq.mwt.fastmarket.business.RegionService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Region;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

@Stateless
@Remote(RegionService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RegionServiceHibernate implements RegionService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public RegionServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Region> getAllRegions() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<Region> regions = session.createQuery("from Region").list();
		Set<Region> result = new HashSet<Region>(regions);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Region> findAllRegionsPaginated(RequestGrid requestGrid)
			throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(Region.class, "r");

		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("r.id");
		} else {
			requestGrid.setSortCol("r." + requestGrid.getSortCol());
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
					.like("r.name",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(Region.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<Region> regions = criteria.list();

		return new ResponseGrid<Region>(requestGrid.getsEcho(), records,
				records, regions);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Region region) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(region);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Region findRegionByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Region where id=:id");
		query.setParameter("id", id);
		Region region = (Region) query.uniqueResult();
		if (region == null) {
			throw new BusinessException("Region not found");
		} else {
			return region;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Region region) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(region);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Region region) throws BusinessException {
		Session session = sessionFactory.openSession();
		Region region2 = (Region) session.load(Region.class, region.getId());
		session.delete(region2);
	}

}

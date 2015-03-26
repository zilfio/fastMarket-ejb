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

import it.univaq.mwt.fastmarket.business.BrandService;
import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Brand;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(BrandService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class BrandServiceHibernate implements BrandService {

	@Resource(name="hib") 
	SessionFactory sessionFactory;
	
	public BrandServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Set<Brand> getAllBrands() throws BusinessException {
            Session session = sessionFactory.openSession();
            Query query = session.createQuery("from Brand");
            List<Brand> brands = query.list();
            Set<Brand> result = new HashSet<Brand>(brands);
            return result;
    }
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Brand> findAllBrandsPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.openSession();
		
		Criteria criteria = session.createCriteria(Brand.class,"b");
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("b.id");
		} else {
			requestGrid.setSortCol("b." + requestGrid.getSortCol());
		}
	
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("b.name", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(Brand.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<Brand> brands = criteria.list();
		
		return new ResponseGrid<Brand>(requestGrid.getsEcho(), records, records, brands);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Brand brand) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(brand);
		
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Brand findBrandByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Brand where id=:id");
		query.setParameter("id", id);
		Brand brand = (Brand) query.uniqueResult();
		if(brand == null) {
			throw new BusinessException("Brand not found");
		} else {
			return brand;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Brand brand) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(brand);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Brand brand) throws BusinessException {
		Session session = sessionFactory.openSession();
		Brand brand2 = (Brand) session.load(Brand.class, brand.getId());
		session.delete(brand2);
	}

}

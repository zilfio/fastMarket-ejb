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
import it.univaq.mwt.fastmarket.business.CategoryService;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.model.Category;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(CategoryService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CategoryServiceHibernate implements CategoryService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public CategoryServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Category> getAllCategories() throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Category");
		List<Category> categories = query.list();
		Set<Category> result = new HashSet<Category>(categories);
		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Category> findAllCategoriesPaginated(
			RequestGrid requestGrid) throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(Category.class, "c");

		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("c.id");
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

		Long records = (Long) session.createCriteria(Category.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<Category> categories = criteria.list();

		return new ResponseGrid<Category>(requestGrid.getsEcho(), records,
				records, categories);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Category category) throws BusinessException {
		Session session = sessionFactory.openSession();

		session.save(category);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Category findCategoryByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Category where id=:id");
		query.setParameter("id", id);
		Category category = (Category) query.uniqueResult();
		if (category == null) {
			throw new BusinessException("Category not found");
		} else {
			return category;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Category category) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(category);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Category category) throws BusinessException {
		Session session = sessionFactory.openSession();
		Category category2 = (Category) session.load(Category.class,
				category.getId());
		session.delete(category2);
	}

}

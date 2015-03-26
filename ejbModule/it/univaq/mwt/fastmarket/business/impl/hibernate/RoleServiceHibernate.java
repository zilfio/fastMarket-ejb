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
import javax.transaction.Transactional;

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
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.RoleService;
import it.univaq.mwt.fastmarket.business.model.Role;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Stateless
@Remote(RoleService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RoleServiceHibernate implements RoleService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public RoleServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Role> getAllRoles() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<Role> roles = session.createQuery("from Role").list();
		Set<Role> result = new HashSet<Role>(roles);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<Role> findAllRolesPaginated(RequestGrid requestGrid)
			throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(Role.class, "r");

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

		Long records = (Long) session.createCriteria(Role.class)
				.setProjection(Projections.rowCount()).uniqueResult();

		List<Role> roles = criteria.list();
		
		return new ResponseGrid<Role>(requestGrid.getsEcho(), records, records,
				roles);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(Role role) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.save(role);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Role findRoleByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Role where id=:id");
		query.setParameter("id", id);
		Role role = (Role) query.uniqueResult();
		if (role == null) {
			throw new BusinessException("Role not found");
		} else {
			return role;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Role findRoleByName(String name) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from Role where name=:name");
		query.setParameter("name", name);
		Role role = (Role) query.uniqueResult();
		return role;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<Role> getRolesByUserID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session
				.createQuery("select roles from User u where u.id = :id");
		query.setParameter("id", id);
		List<Role> roles = query.list();
		Set<Role> result = new HashSet<Role>(roles);

		return result;
	}

	
	public Set<Role> getRolesUserRegistred() throws BusinessException {
		Session session = sessionFactory.openSession();

		if (findRoleByName("UserRegistred") == null) {
			Role role = new Role(0, "UserRegistred", "");
			create(role);
		}

		Query query = session.createQuery("from Role where name = :name");
		query.setParameter("name", "UserRegistred");
		List<Role> roles = query.list();
		Set<Role> result = new HashSet<Role>(roles);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(Role role) throws BusinessException {
		Session session = sessionFactory.openSession();
		session.merge(role);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(Role role) throws BusinessException {
		Session session = sessionFactory.openSession();
		Role role2 = (Role) session.load(Role.class, role.getId());
		session.delete(role2);
	}

}

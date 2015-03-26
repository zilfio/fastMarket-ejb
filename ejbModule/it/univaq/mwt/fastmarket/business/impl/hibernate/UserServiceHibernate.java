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
import it.univaq.mwt.fastmarket.business.UserService;
import it.univaq.mwt.fastmarket.business.model.User;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;
import it.univaq.mwt.fastmarket.common.utility.MD5;

@Stateless
@Remote(UserService.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserServiceHibernate implements UserService {

	@Resource(name = "hib")
	SessionFactory sessionFactory;

	public UserServiceHibernate() {
		new Configuration().configure().buildSessionFactory();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Set<User> getAllUsers() throws BusinessException {
		Session session = sessionFactory.openSession();
		List<User> users = session.createQuery("from User").list();
		Set<User> result = new HashSet<User>(users);

		return result;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ResponseGrid<User> findAllUsersPaginated(RequestGrid requestGrid)
			throws BusinessException {

		Session session = sessionFactory.openSession();

		Criteria criteria = session.createCriteria(User.class, "u");
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("u.id");
		} else {
			requestGrid.setSortCol("u." + requestGrid.getSortCol());
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
					.like("u.username",
							ConversionUtility.addPercentSuffix(requestGrid
									.getsSearch())).ignoreCase());
		}

		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
				.setMaxResults((int) requestGrid.getiDisplayLength());

		Long records = (Long) session.createCriteria(User.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<User> users = criteria.list();
		
		return new ResponseGrid<User>(requestGrid.getsEcho(), records, records,
				users);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void create(User user) throws BusinessException {
		// setto la password in md5
		user.setPassword(MD5.generateMD5(user.getPassword()));

		Session session = sessionFactory.openSession();
		session.save(user);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public User findUserByID(Long id) throws BusinessException {
		Session session = sessionFactory.openSession();
		Query query = session
				.createQuery("from User u left join fetch u.roles where u.id=:id");
		query.setParameter("id", id);
		User user = (User) query.uniqueResult();
		if (user == null) {
			throw new BusinessException("User not found");
		} else {
			return user;
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public User findUserByUsername(String username) throws BusinessException {
		Session session = sessionFactory.openSession();
		;
		Query query = session
				.createQuery("from User u left join fetch u.roles where u.username=:username");
		query.setParameter("username", username);
		User user = (User) query.uniqueResult();
		return user;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void update(User user) throws BusinessException {
		// recupero utente
		User old_user = findUserByID(user.getId());

		// se le password sono diverse allora applico l'md5 altrimenti la lascio
		// invariata
		if (!user.getPassword().equals(old_user.getPassword())) {
			user.setPassword(MD5.generateMD5(user.getPassword()));
		}

		Session session = sessionFactory.openSession();
		session.merge(user);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void delete(User user) throws BusinessException {
		Session session = sessionFactory.openSession();
		User user2 = (User) session.load(User.class, user.getId());
		session.delete(user2);
	}

}

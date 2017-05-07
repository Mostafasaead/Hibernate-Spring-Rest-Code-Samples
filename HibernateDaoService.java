package com.se.adminusermanagement.dao;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.se.exceptions.DataNotFoundException;

/**
 * hibnerate based DAO service depend on sessionFactory
 * 
 * @author Mostafa El-Gazzar
 * 
 */
@Component("umAdminDAO")
@Transactional(propagation = Propagation.REQUIRED)
public class HibernateDaoService {
	private static final Logger LOG = Logger.getLogger(HibernateDaoService.class.getName());
	@Autowired
	@Qualifier("umAdminSessionFactory")
	public SessionFactory sessionFactory;

	private static final ThreadLocal sessionThread = new ThreadLocal();

	public Session getCurrentSession() throws HibernateException {
		Session session = (Session) sessionThread.get();
		if (session == null) {
			session = sessionFactory.openSession();
			sessionThread.set(session);
		}
		return session;
	}

	public void closeSession() throws HibernateException {
		Session session = (Session) sessionThread.get();
		if (session != null)
			session.close();
		sessionThread.set(null);
	}

	/**
	 * Create a given entity in data store
	 * 
	 * @param <T>
	 *            Type of Entity
	 * @return <T> Created Entity
	 */
	public <T> T create(T t) {
		try {
			Session session = getCurrentSession();
			// Transaction tx = session.beginTransaction();
			getCurrentSession().save(t);
			// tx.commit();
			session.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSession();
		}

		return t;
	}

	/**
	 * Updates an existing entity in the database.
	 * 
	 * @param <T>
	 *            Type of entity
	 * @param t
	 *            Entity to update
	 * @return Updated entity
	 */
	public <T> T update(T t) {
		try {
			Session session = getCurrentSession();
			// Transaction tx = session.beginTransaction();
			session.update(t);
			// tx.commit();
			session.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSession();
		}
		return t;
	}

	/**
	 * delete an existing entity in the database.
	 * 
	 * @param <T>
	 *            Entity to be deleted
	 */
	public <T> void delete(T t) {

		try {
			Session session = getCurrentSession();
			// Transaction tx = session.beginTransaction();
			session.delete(t);
			// tx.commit();
			session.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSession();
		}
	}

	/**
	 * Finds a given entity in the data store.
	 * 
	 * @param <T>
	 *            Type of entity
	 * @param type
	 *            Type of entity
	 * @param id
	 *            Unique identifier of the entity
	 * @return Entity matching the unique identifier
	 * @throws DataNotFoundException
	 *             If no match could be found
	 */
	public <T> T findById(Class<T> type, Object id) throws DataNotFoundException {
		if (id == null) {
			LOG.log(Level.SEVERE, "null is not a valid primary key for " + type.getName());
			throw new DataNotFoundException("null is not a valid primary key for " + type.getName());
		}
		Session session = getCurrentSession();

		@SuppressWarnings("unchecked")
		T entity = null;
		try {
			entity = (T) session.get(type, (Serializable) id);
			if (entity == null) {
				LOG.log(Level.SEVERE, type.getName() + " with ID " + id + " not found");
				throw new DataNotFoundException(type.getName() + " with ID " + id + " not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeSession();
		}
		return entity;
	}

	/**
	 * Finds all the entities of a given type.
	 * 
	 * @param <T>
	 *            Type of entity
	 * @param type
	 *            Type of entity
	 * @return {@link List} of all entities of the given type
	 */
	public <T> List<T> findAll(Class<T> type) throws DataNotFoundException {
		@SuppressWarnings("unchecked")
		Session session = getCurrentSession();
		List<T> entities = null;
		try {
			entities = (List<T>) session.createCriteria(type).list();
			if (entities == null) {
				throw new DataNotFoundException(type.getName());
			}
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSession();
		}

		return entities;
	}

	/**
	 * Finds a range of entities of a given type in a given range.
	 * 
	 * @param <T>
	 *            Type of entity
	 * @param type
	 *            Type of entity
	 * @param start
	 *            First entity to retrieve
	 * @param resultLimit
	 *            Number of entities to retrieve
	 * @return {@link List} of entities of the given type in the given range
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> List<T> findAll(Class<T> type, int start, int resultLimit) {
		Session session = getCurrentSession();
		try {
			return new LinkedList(session.createCriteria(type).setFirstResult(start).setMaxResults(resultLimit).list());

		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			closeSession();
		}

	}

	/**
	 * Finds a {@link List} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @return {@link List} of entities returned by the {@link Query}
	 */
	public <T> List<T> findWithNamedQuery(String query, Map<String, Object> parameters) {

		Session session = getCurrentSession();
		try {
			Query namedQuery = session.getNamedQuery(query);
			return findWithQuery(namedQuery, parameters);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			closeSession();
		}

	}

	/**
	 * Finds a {@link List} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @return {@link List} of entities returned by the {@link Query}
	 */
	public <T> List<T> findWithNamedQuery(String query, Map<String, Object> parameters, int start, int resultLimit) {

		Session session = getCurrentSession();
		try {
			Query namedQuery = session.getNamedQuery(query);
			return findWithQuery(namedQuery, parameters, start, resultLimit);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			closeSession();
		}

	}

	/**
	 * Finds a {@link Object} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @return {@link Object} of entity returned by the {@link Query}
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findObjectWithNamedQuery(String query, Map<String, Object> parameters) throws DataNotFoundException {

		Session session = getCurrentSession();
		try {
			Query namedQuery = session.getNamedQuery(query);
			return (T) findObjectWithQuery(namedQuery, parameters);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			closeSession();
		}

	}

	/**
	 * Finds a {@link List} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @param start
	 *            First record of the result set
	 * @param resultLimit
	 *            Maximum number of results
	 * @return {@link List} of entities returned by the {@link Query}
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findWithQuery(Query query, Map<String, Object> parameters) {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		setQueryParameters(query, rawParameters);
		return query.list();
	}

	/**
	 * Finds a {@link Object} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @return {@link Object} of entities returned by the {@link Query}
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findObjectWithQuery(Query query, Map<String, Object> parameters) throws DataNotFoundException {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		setQueryParameters(query, rawParameters);
		List entities = query.list();
		if (!entities.isEmpty()) {
			if (entities.size() > 1) {
				LOG.log(Level.INFO, "More Objects Found With This Creatria . May Cause Probelm , Try To Get All Objects Instead");

			}
			return (T) entities.get(0);
		} else {
			LOG.log(Level.SEVERE, "No Object Found");
			throw new DataNotFoundException("No Object Found");
		}

	}

	/**
	 * Finds a {@link List} of entity returned by the given {@link Query}.
	 * 
	 * @param query
	 *            {@link Query} to execute
	 * @param parameters
	 *            Parameters of the query
	 * @param start
	 *            First record of the result set
	 * @param resultLimit
	 *            Maximum number of results
	 * @return {@link List} of entities returned by the {@link Query}
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> findWithQuery(Query query, Map<String, Object> parameters, int start, int resultLimit) {
		Set<Entry<String, Object>> rawParameters = parameters.entrySet();
		query.setFirstResult(start);
		query.setMaxResults(start + resultLimit);
		setQueryParameters(query, rawParameters);
		return query.list();
	}

	/**
	 * native delete SQL statement
	 * 
	 * @param deletequery
	 *            string of SQL Delete Command To Execute
	 */
	public void deleteNatively(String deletequery) {

		Session session = getCurrentSession();
		try {
			// Transaction tx = session.beginTransaction();
			session.createSQLQuery(deletequery).executeUpdate();
			// tx.commit();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeSession();
		}

	}

	private void setQueryParameters(Query query, Set<Entry<String, Object>> parameters) {
		for (Entry<String, Object> param : parameters) {
			query.setParameter(param.getKey(), param.getValue());
		}
	}
}
/**
 * 
 */
package com.se.adminusermanagement.services.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.adminusermanagement.dao.HibernateDaoService;
import com.se.adminusermanagement.entities.Users;
import com.se.adminusermanagement.services.interfaces.UserService;
import com.se.exceptions.DataNotFoundException;
import com.se.log.SELogger;

/**
 * @author Mostafa El-Gazzar
 * 
 */
@Service("usersService")
public class UserServiceImpl implements UserService {
	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class
			.getName());

	@Autowired
	private HibernateDaoService umAdminDAO;

	public List<Users> findAllUsers(int start, int limit) {
		try {
			// return umAdminDAO.findAll(Company.class);
			return umAdminDAO.findAll(Users.class, start, limit);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, " Users Table Is Empty");
		}
		return null;
	}

	public Users findUsersById(Integer usersId) {
		long startTime = System.currentTimeMillis();
		SELogger.info("Starting method findUsersById");
		System.out.println("Starting method findUsersById");

		try {

			SELogger.info("Ending method findUsersById : "
					+ (System.currentTimeMillis() - startTime) + " MS");

			return umAdminDAO.findById(Users.class, usersId);

		} catch (DataNotFoundException e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, " Company Table Is Empty");
		}
		return null;
	}

	public List<Users> findUserByType(String userTypeId) {
		long startTime = System.currentTimeMillis();
		SELogger.info("Starting method findUserByType");
		System.out.println("Starting method findUserByType");

		try {
			StringBuilder queryBuilder = new StringBuilder(
					"SELECT DISTINCT users FROM Users as users Where company.status = 0 AND ");
			if (!userTypeId.equals("")) {
				queryBuilder.append("users.userTypeId=:userTypeId");
			}
			Query q = (Query) umAdminDAO.getCurrentSession().createQuery(
					queryBuilder.toString());

			if (!userTypeId.equals(""))
				q.setParameter("userTypeId", Integer.valueOf(userTypeId));

			
			SELogger.info("Ending method findUserByType : "
					+ (System.currentTimeMillis() - startTime) + " MS");
			
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			SELogger.error("Empty result from findUserByType method with Exception:"
					+ e.getMessage());
			LOG.log(Level.SEVERE, " User Table Is Empty");
			return null;
		}
	}

}

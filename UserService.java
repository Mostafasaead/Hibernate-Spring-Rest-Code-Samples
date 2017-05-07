/**
 * 
 */
package com.se.adminusermanagement.services.interfaces;

import java.util.List;

import com.se.adminusermanagement.entities.Users;

/**
 * @author Mostafa El-Gazzar
 *
 */
public interface UserService {
	public List<Users> findAllUsers(int start,int limit);
	public Users findUsersById(Integer usersId);
	public List<Users> findUserByType(String userTypeId);
}

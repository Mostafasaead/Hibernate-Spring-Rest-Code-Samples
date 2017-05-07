/**
 * 
 */
package com.se.adminusermanagement.services.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se.adminusermanagement.dao.HibernateDaoService;
import com.se.adminusermanagement.entities.CompanyProduct;
import com.se.adminusermanagement.services.interfaces.CompanyProductService;
import com.se.exceptions.DataNotFoundException;

/**
 * @author Mostafa El-Gazzar
 *
 */
@Service("companyProductServiceImpl")
public class CompanyProductServiceImpl implements CompanyProductService{
	private static final Logger LOG = Logger.getLogger(CompanyProductServiceImpl.class.getName());
	
	@Autowired
	private HibernateDaoService umAdminDAO;

	public List<CompanyProduct> findAllCompanyProducts() {
		try {
			//return umAdminDAO.findAll(Company.class);
			return umAdminDAO.findAll(CompanyProduct.class);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, " CompanyProduct Table Is Empty");
		}
		return null;
	}
	
	public CompanyProduct findCompanyProductById(int companyProductId) {
		try {
			//return umAdminDAO.findAll(Company.class);
			return umAdminDAO.findById(CompanyProduct.class, companyProductId);
		} catch (DataNotFoundException e) {
			e.printStackTrace();
			LOG.log(Level.SEVERE, " CompanyProduct Table Is Empty");
		}
		return null;
	}


}

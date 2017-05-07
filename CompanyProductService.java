/**
 * 
 */
package com.se.adminusermanagement.services.interfaces;

import java.util.List;

import com.se.adminusermanagement.entities.CompanyProduct;

/**
 * @author Mostafa El-Gazzar
 *
 */
public interface CompanyProductService {
	public List<CompanyProduct> findAllCompanyProducts();
	public CompanyProduct findCompanyProductById(int companyProductId);
	
}

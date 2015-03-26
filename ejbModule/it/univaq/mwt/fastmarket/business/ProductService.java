package it.univaq.mwt.fastmarket.business;

import it.univaq.mwt.fastmarket.business.model.Brand;
import it.univaq.mwt.fastmarket.business.model.Category;
import it.univaq.mwt.fastmarket.business.model.Image;
import it.univaq.mwt.fastmarket.business.model.IntoleranceCategory;
import it.univaq.mwt.fastmarket.business.model.Product;

import java.util.Set;

import javax.ejb.Remote;

@Remote
public interface ProductService {

	Set<Product> getAllProducts() throws BusinessException;
	
	Product findProductById(Long id) throws BusinessException;
	
	Set<Product> getLastProducts(int start, int maxRows) throws BusinessException;
	
	Set<Product> searchProductByName(String name) throws BusinessException;
	
	Set<Product> searchProductByCategory(Long category) throws BusinessException;
	
	Set<Product> searchAdvancedProduct(Brand brand, Category category, IntoleranceCategory intoleranceCategory) throws BusinessException;

	String saveProductImageIntoFileSystem(Image image) throws BusinessException;
	
	boolean deleteProductImageIntoFileSystem(String path) throws BusinessException;
	
}

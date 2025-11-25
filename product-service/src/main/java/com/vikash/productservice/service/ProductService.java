package com.vikash.productservice.service;

import java.util.List;

import com.vikash.productservice.dto.ProductRequest;
import com.vikash.productservice.dto.ProductResponse;

public interface ProductService {

	public void createProduct(ProductRequest productRequest);
	public List<ProductResponse> getAllProducts();
	
}

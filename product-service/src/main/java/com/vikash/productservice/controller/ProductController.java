package com.vikash.productservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import com.vikash.productservice.dto.ProductRequest;
import com.vikash.productservice.dto.ProductResponse;
import com.vikash.productservice.service.impl.ProductServiceImpl;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

	private final ProductServiceImpl productService;

	@PostMapping
	public void createProduct(@RequestBody ProductRequest productRequest) {
		productService.createProduct(productRequest);
	}

	@GetMapping
	public List<ProductResponse> getAllProducts() {
		return productService.getAllProducts();
	}
}

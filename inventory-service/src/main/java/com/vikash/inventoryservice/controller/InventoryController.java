package com.vikash.inventoryservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import com.vikash.inventoryservice.dto.InventoryResponse;
import com.vikash.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@GetMapping
	public List<InventoryResponse> isInStock(@RequestParam("skuCode") List<String> skuCodeList) {
		return inventoryService.isInStock(skuCodeList);
	}
}

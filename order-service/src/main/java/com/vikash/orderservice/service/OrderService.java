package com.vikash.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.vikash.orderservice.dto.InventoryResponse;
import com.vikash.orderservice.dto.OrderLineItemsDto;
import com.vikash.orderservice.dto.OrderRequest;
import com.vikash.orderservice.model.Order;
import com.vikash.orderservice.model.OrderLineItems;
import com.vikash.orderservice.repository.OrderRepository;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;

  // ✅ Inject the plain builder (NOT the @LoadBalanced one)
  @Qualifier("plainWebClientBuilder")
  private final WebClient.Builder webClientBuilder;

  private final Tracer tracer;

  @Value("${inventory.service.url}")
  private String inventoryServiceBaseUrl;

  public String placeOrder(OrderRequest orderRequest) {
    Order order = new Order();
    order.setOrderNumber(UUID.randomUUID().toString());

    var orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
        .map(this::mapToEntity)
        .collect(Collectors.toList());
    order.setOrderLineItemsList(orderLineItems);

    List<String> skuCodes = order.getOrderLineItemsList().stream()
        .map(OrderLineItems::getSkuCode)
        .collect(Collectors.toList());

    log.info("Calling inventory service at {}", inventoryServiceBaseUrl);

    Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");

    try (Tracer.SpanInScope ignored = tracer.withSpan(inventoryServiceLookup.start())) {

      WebClient client = webClientBuilder
          .baseUrl(inventoryServiceBaseUrl) // http://localhost:8082
          .build();

      InventoryResponse[] inventoryResponses = client.get()
          .uri(uriBuilder -> uriBuilder
              .path("/api/inventory")
              .queryParam("skuCode", skuCodes) // expands to repeated params
              .build())
          .retrieve()
          .bodyToMono(InventoryResponse[].class)
          .block();

      log.debug("Inventory responses: {}", Arrays.toString(inventoryResponses));

      if (inventoryResponses == null || inventoryResponses.length == 0) {
        throw new IllegalStateException("Inventory service returned empty response");
      }

      boolean allProductsInStock = Arrays.stream(inventoryResponses)
          .allMatch(InventoryResponse::isInStock);

      if (allProductsInStock) {
        orderRepository.save(order);
        return "Order placed successfully!";
      } else {
        throw new IllegalArgumentException("Order out of stock");
      }
    } finally {
      inventoryServiceLookup.end();
    }
  }

  private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
    OrderLineItems oli = new OrderLineItems();
    oli.setPrice(dto.getPrice());
    oli.setQuantity(dto.getQuantity());
    oli.setSkuCode(dto.getSkuCode());
    return oli;
  }
}

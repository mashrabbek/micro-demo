package com.example.api.core.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
  private final int productId;
  private final String name;
  private final int weight;
  private final String serviceAddress;

  public Product() {
    productId = 0;
    name = null;
    weight = 0;
    serviceAddress = null;
  }

}

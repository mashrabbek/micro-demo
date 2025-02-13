package com.example.api.composite.product;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationSummary {

  private final int recommendationId;
  private final String author;
  private final int rate;

}

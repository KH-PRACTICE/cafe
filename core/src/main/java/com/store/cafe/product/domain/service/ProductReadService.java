package com.store.cafe.product.domain.service;

import com.store.cafe.product.domain.exception.ProductNotFoundException;
import com.store.cafe.product.domain.model.entity.Product;
import com.store.cafe.product.domain.model.entity.ProductRepository;
import com.store.cafe.product.domain.model.vo.PricedOrderItem;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReadService {

    private final ProductRepository productRepository;

    public List<PricedOrderItem> attachPrices(List<StockDecreaseResult> stockResults) {

        List<Long> productIds = stockResults
                .stream()
                .map(StockDecreaseResult::productId).toList();

        Map<Long, Product> productMap =
                productRepository.findByProductIds(productIds)
                        .stream()
                        .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return stockResults.stream()
                .map(result -> {
                    Product product = productMap.get(result.productId());
                    if (product == null) {
                        throw new ProductNotFoundException("상품을 찾을 수 없습니다: " + result.productId());
                    }
                    return PricedOrderItem.of(
                            product.getProductId(),
                            result.quantity(),
                            product.getPrice()
                    );
                })
                .toList();
    }
}
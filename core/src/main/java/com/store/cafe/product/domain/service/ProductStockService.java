package com.store.cafe.product.domain.service;

import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.product.domain.exception.ProductNotFoundException;
import com.store.cafe.product.domain.model.entity.ProductStock;
import com.store.cafe.product.domain.model.entity.ProductStockRepository;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    public List<StockDecreaseResult> decreaseStocks(List<OrderItemCommand> commands) {

        Map<Long, ProductStock> stockMap = getProductStockMap(commands.stream()
                .map(OrderItemCommand::productId));

        return commands.stream()
                .map(cmd -> {
                    ProductStock stock = stockMap.get(cmd.productId());
                    if (stock == null) {
                        throw new ProductNotFoundException("재고 정보 없음: " + cmd.productId());
                    }
                    stock.decrease(cmd.quantity());
                    return new StockDecreaseResult(cmd.productId(), cmd.quantity());
                })
                .toList();
    }

    public void restoreStocks(List<OrderItem> orderItems) {

        Map<Long, ProductStock> stockMap = getProductStockMap(orderItems.stream()
                .map(OrderItem::getProductId));

        for (OrderItem item : orderItems) {
            ProductStock stock = stockMap.get(item.getProductId());
            if (stock != null) {
                stock.increase(item.getQuantity());
            }
        }
    }

    private Map<Long, ProductStock> getProductStockMap(Stream<Long> orderItems) {
        List<Long> productIds = orderItems
                .sorted()
                .toList();

        List<ProductStock> stocks = productStockRepository.findAllByProductIdInForUpdate(productIds);

        return stocks.stream()
                .collect(Collectors.toMap(
                        ProductStock::getProductId,
                        Function.identity()));
    }
}
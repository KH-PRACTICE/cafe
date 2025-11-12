package com.store.cafe.product.domain.service;

import com.store.cafe.order.application.command.OrderItemCommand;
import com.store.cafe.order.domain.model.entity.OrderItem;
import com.store.cafe.product.domain.exception.ProductNotFoundException;
import com.store.cafe.product.domain.model.entity.ProductStock;
import com.store.cafe.product.domain.model.entity.ProductStockRepository;
import com.store.cafe.product.domain.model.vo.StockDecreaseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    public List<StockDecreaseResult> decreaseStocks(List<OrderItemCommand> commands) {

        List<Long> productIds = commands.stream()
                .map(OrderItemCommand::productId)
                .sorted()
                .toList();

        Map<Long, ProductStock> stockMap = getProductStockMap(productIds);

        return commands.stream()
                .map(cmd -> {
                    ProductStock stock = stockMap.get(cmd.productId());
                    if (stock == null) {
                        throw new ProductNotFoundException("Not Found Product Stock. productId=" + cmd.productId());
                    }
                    stock.decrease(cmd.quantity());
                    return new StockDecreaseResult(cmd.productId(), cmd.quantity());
                })
                .toList();
    }

    public void restoreStocks(List<OrderItem> orderItems) {

        List<Long> productIds = orderItems.stream()
                .map(OrderItem::getProductId)
                .sorted()
                .toList();

        Map<Long, ProductStock> stockMap = getProductStockMap(productIds);

        for (OrderItem item : orderItems) {
            ProductStock stock = stockMap.get(item.getProductId());
            if (stock != null) {
                stock.increase(item.getQuantity());
            }
        }
    }

    private Map<Long, ProductStock> getProductStockMap(List<Long> productIds) {

        List<ProductStock> stocks = productStockRepository.findAllByProductIdInForUpdate(productIds);

        return stocks.stream()
                .collect(Collectors.toMap(
                        ProductStock::getProductId,
                        Function.identity()));
    }
}
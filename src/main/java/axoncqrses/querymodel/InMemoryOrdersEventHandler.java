package axoncqrses.querymodel;


import axoncqrses.coreapi.events.OrderConfirmedEvent;
import axoncqrses.coreapi.events.OrderCreatedEvent;
import axoncqrses.coreapi.events.OrderShippedEvent;
import axoncqrses.coreapi.events.ProductAddedEvent;
import axoncqrses.coreapi.events.ProductCountDecrementedEvent;
import axoncqrses.coreapi.events.ProductCountIncrementedEvent;
import axoncqrses.coreapi.events.ProductRemovedEvent;
import axoncqrses.coreapi.queries.FindAllOrderedProductsQuery;
import axoncqrses.coreapi.queries.Order;
import axoncqrses.coreapi.queries.OrderStatus;
import axoncqrses.coreapi.queries.OrderUpdatesQuery;
import axoncqrses.coreapi.queries.TotalProductsShippedQuery;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@ProcessingGroup("orders")
@Profile("!mongo")
public class InMemoryOrdersEventHandler implements OrdersEventHandler {

    private final Map<String, Order> orders = new HashMap<>();
    private final QueryUpdateEmitter emitter;

    public InMemoryOrdersEventHandler(QueryUpdateEmitter emitter) {
        this.emitter = emitter;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        String orderId = event.getOrderId();
        orders.put(orderId, new Order(orderId));
    }

    @EventHandler
    public void on(ProductAddedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.addProduct(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductCountIncrementedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.incrementProductInstance(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductCountDecrementedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.decrementProductInstance(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(ProductRemovedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.removeProduct(event.getProductId());
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(OrderConfirmedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.setOrderConfirmed();
            emitUpdate(order);
            return order;
        });
    }

    @EventHandler
    public void on(OrderShippedEvent event) {
        orders.computeIfPresent(event.getOrderId(), (orderId, order) -> {
            order.setOrderShipped();
            emitUpdate(order);
            return order;
        });
    }

    @QueryHandler
    public List<Order> handle(FindAllOrderedProductsQuery query) {
        return new ArrayList<>(orders.values());
    }

    @QueryHandler
    public Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query) {
        return Mono.fromCallable(orders::values)
          .flatMapMany(Flux::fromIterable);
    }

    @QueryHandler
    public Integer handle(TotalProductsShippedQuery query) {
        return orders.values()
          .stream()
          .filter(o -> o.getOrderStatus() == OrderStatus.SHIPPED)
          .map(o -> Optional.ofNullable(o.getProducts()
              .get(query.getProductId()))
            .orElse(0))
          .reduce(0, Integer::sum);
    }

    @QueryHandler
    public Order handle(OrderUpdatesQuery query) {
        return orders.get(query.getOrderId());
    }

    private void emitUpdate(Order order) {
        emitter.emit(OrderUpdatesQuery.class, q -> order.getOrderId()
          .equals(q.getOrderId()), order);
    }

    @Override
    public void reset(List<Order> orderList) {
        orders.clear();
        orderList.forEach(o -> orders.put(o.getOrderId(), o));
    }
}

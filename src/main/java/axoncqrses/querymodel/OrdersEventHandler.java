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
import axoncqrses.coreapi.queries.OrderUpdatesQuery;
import axoncqrses.coreapi.queries.TotalProductsShippedQuery;

import org.reactivestreams.Publisher;

import java.util.List;

public interface OrdersEventHandler {

    void on(OrderCreatedEvent event);

    void on(ProductAddedEvent event);

    void on(ProductCountIncrementedEvent event);

    void on(ProductCountDecrementedEvent event);

    void on(ProductRemovedEvent event);

    void on(OrderConfirmedEvent event);

    void on(OrderShippedEvent event);

    List<Order> handle(FindAllOrderedProductsQuery query);

    Publisher<Order> handleStreaming(FindAllOrderedProductsQuery query);

    Integer handle(TotalProductsShippedQuery query);

    Order handle(OrderUpdatesQuery query);

    void reset(List<Order> orderList);
}

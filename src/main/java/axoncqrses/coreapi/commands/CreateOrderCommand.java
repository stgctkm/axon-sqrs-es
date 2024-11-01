package axoncqrses.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.Objects;

public record CreateOrderCommand(@TargetAggregateIdentifier String orderId) {
}
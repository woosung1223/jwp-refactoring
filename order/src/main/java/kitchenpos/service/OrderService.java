package kitchenpos.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.dto.request.ChangeOrderStatusRequest;
import kitchenpos.dto.request.CreateOrderRequest;
import kitchenpos.dto.request.OrderLineItemRequest;
import kitchenpos.dto.response.OrderResponse;
import kitchenpos.event.ValidateMenuExistsEvent;
import kitchenpos.event.ValidateOrderTableIsNotEmptyEvent;
import kitchenpos.exception.OrderNotFoundException;
import kitchenpos.repository.OrderRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    public OrderService(ApplicationEventPublisher eventPublisher, OrderRepository orderRepository) {
        this.eventPublisher = eventPublisher;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = saveOrder(request);
        setupOrderLineItems(request, order);

        return OrderResponse.from(order, order.getOrderLineItems());
    }

    private Order saveOrder(CreateOrderRequest request) {
        eventPublisher.publishEvent(new ValidateOrderTableIsNotEmptyEvent(request.getOrderTableId()));
        Order order = new Order(request.getOrderTableId(), OrderStatus.COOKING, LocalDateTime.now());

        return orderRepository.save(order);
    }

    private void setupOrderLineItems(CreateOrderRequest request, Order order) {
        List<OrderLineItem> orderLineItems = request.getOrderLineItems()
                .stream()
                .map(this::createOrderLineItem)
                .collect(Collectors.toList());

        order.setupOrderLineItems(orderLineItems);
    }

    private OrderLineItem createOrderLineItem(OrderLineItemRequest orderLineItemRequest) {
        eventPublisher.publishEvent(new ValidateMenuExistsEvent(orderLineItemRequest.getMenuId()));
        return new OrderLineItem(orderLineItemRequest.getMenuId(), orderLineItemRequest.getQuantity());
    }

    public List<OrderResponse> findAll() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(each -> OrderResponse.from(each, each.getOrderLineItems()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse changeOrderStatus(Long orderId, ChangeOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);
        order.changeOrderStatus(OrderStatus.valueOf(request.getOrderStatus()));

        return OrderResponse.from(order, order.getOrderLineItems());
    }

    public void validateOrdersCompleted(Long orderTableId) {
        List<Order> orders = orderRepository.findByOrderTableId(orderTableId);
        for (Order order : orders) {
            order.validateOrderIsCompleted();
        }
    }
}

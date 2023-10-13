package kitchenpos.dto;

import java.util.List;

public class CreateOrderRequest {

    private Long orderTableId;
    private List<OrderLineItemRequest> orderLineItems;

    public CreateOrderRequest(Long orderTableId, List<OrderLineItemRequest> orderLineItems) {
        this.orderTableId = orderTableId;
        this.orderLineItems = orderLineItems;
    }

    public Long getOrderTableId() {
        return orderTableId;
    }

    public List<OrderLineItemRequest> getOrderLineItems() {
        return orderLineItems;
    }
}

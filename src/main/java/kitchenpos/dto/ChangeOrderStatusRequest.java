package kitchenpos.dto;

public class ChangeOrderStatusRequest {

    private String orderStatus;

    public ChangeOrderStatusRequest(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}

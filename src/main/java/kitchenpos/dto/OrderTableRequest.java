package kitchenpos.dto;

public class OrderTableRequest {

    private Long id;

    public OrderTableRequest(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

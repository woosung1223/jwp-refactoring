package kitchenpos.dao;

import java.util.List;
import java.util.Optional;
import kitchenpos.domain.OrderTable;

public interface OrderTableDao {
    OrderTable save(OrderTable entity);

    Optional<OrderTable> findById(Long id);

    List<OrderTable> findAll();

    List<OrderTable> findAllByIdIn(List<Long> ids);

    List<OrderTable> findAllByTableGroupId(Long tableGroupId);

    default OrderTable getById(Long id) {
        return findById(id).orElseThrow(IllegalArgumentException::new);
    }
}

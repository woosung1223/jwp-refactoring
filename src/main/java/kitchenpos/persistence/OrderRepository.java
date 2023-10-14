package kitchenpos.persistence;

import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderTable(OrderTable orderTable);

    List<Order> findByOrderTableIn(List<OrderTable> orderTables);
}

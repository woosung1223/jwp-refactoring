package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import kitchenpos.dto.request.ChangeEmptyTableRequest;
import kitchenpos.dto.request.ChangeTableGuestRequest;
import kitchenpos.dto.request.CreateOrderTableRequest;
import kitchenpos.dto.response.OrderTableResponse;
import kitchenpos.exception.NumberOfGuestIsNotPositiveException;
import kitchenpos.exception.OrderIsNotCompletedException;
import kitchenpos.exception.OrderTableEmptyException;
import kitchenpos.exception.OrderTableNotFoundException;
import kitchenpos.exception.TableGroupExistsException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

@SuppressWarnings("NonAsciiCharacters")
class TableServiceTest extends ServiceTestContext {

    @Test
    void 테이블을_정상_생성하면_생성한_테이블이_반환된다() {
        // given
        CreateOrderTableRequest request = new CreateOrderTableRequest(2, false);

        // when
        OrderTableResponse response = tableService.create(request);

        // then
        assertThat(response.getId()).isNotNull();
    }

    @Test
    void 모든_테이블을_조회할_수_있다() {
        // given
        CreateOrderTableRequest request = new CreateOrderTableRequest(2, false);
        tableService.create(request);

        // when
        List<OrderTableResponse> response = tableService.findAll();

        // then
        assertThat(response).hasSize(1);
    }

    @Test
    void 빈_테이블로_변경할_시_해당_테이블이_없다면_예외를_던진다() {
        // given
        Long orderTableId = Long.MAX_VALUE;

        ChangeEmptyTableRequest request = new ChangeEmptyTableRequest(false);

        // when, then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTableId, request))
                .isInstanceOf(OrderTableNotFoundException.class);
    }

    @Test
    void 테이블_그룹이_있는_경우_빈_테이블로_변경하려_할_때_예외를_던진다() {
        // given
        TableGroup tableGroup = new TableGroup(LocalDateTime.now());
        tableGroupRepository.save(tableGroup);

        OrderTable orderTable = new OrderTable(tableGroup, 2, false);
        orderTableRepository.save(orderTable);

        ChangeEmptyTableRequest request = new ChangeEmptyTableRequest(false);

        // when, then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), request))
                .isInstanceOf(TableGroupExistsException.class);
    }

    @ParameterizedTest
    @EnumSource(mode = Mode.INCLUDE, names = {"COOKING", "MEAL"})
    void 빈_테이블로_변경하려_할_때_주문_상태가_COOKING이거나_MEAL이면_예외를_던진다(OrderStatus orderStatus) {
        // given
        OrderTable orderTable = new OrderTable(null, 2, false);
        orderTableRepository.save(orderTable);

        Order order = new Order(orderTable, orderStatus, LocalDateTime.now());
        orderRepository.save(order);

        ChangeEmptyTableRequest request = new ChangeEmptyTableRequest(false);

        // when, then
        assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), request))
                .isInstanceOf(OrderIsNotCompletedException.class);
    }

    @Test
    void 손님_수를_0명_미만으로_변경하려고_하면_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);
        orderTableRepository.save(orderTable);

        ChangeTableGuestRequest request = new ChangeTableGuestRequest(-1);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(NumberOfGuestIsNotPositiveException.class);
    }

    @Test
    void 손님_수를_변경할_때_테이블이_없다면_예외를_던진다() {
        // given
        Long orderTableId = Long.MAX_VALUE;
        ChangeTableGuestRequest request = new ChangeTableGuestRequest(2);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(OrderTableNotFoundException.class);
    }

    @Test
    void 빈_테이블에_대해_손님_수를_변경하려_하면_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(null, -1, true);
        orderTableRepository.save(orderTable);

        ChangeTableGuestRequest request = new ChangeTableGuestRequest(2);

        // when, then
        assertThatThrownBy(() -> tableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(OrderTableEmptyException.class);
    }

    @Test
    void 테이블을_정상적으로_변경하면_변경된_테이블을_반환한다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);
        orderTableRepository.save(orderTable);

        ChangeTableGuestRequest request = new ChangeTableGuestRequest(5);

        // when
        OrderTableResponse response = tableService.changeNumberOfGuests(orderTable.getId(), request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getId()).isNotNull();
            softly.assertThat(response.getNumberOfGuests()).isEqualTo(5);
        });
    }
}

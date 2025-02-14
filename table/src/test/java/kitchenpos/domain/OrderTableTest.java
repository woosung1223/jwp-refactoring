package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import kitchenpos.exception.NotEnoughGuestsException;
import kitchenpos.exception.OrderTableEmptyException;
import kitchenpos.exception.OrderTableNotEmptyException;
import kitchenpos.exception.TableGroupExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class OrderTableTest {

    @Test
    void 테이블_그룹이_존재한다면_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(1L, 1, false);

        // when, then
        Assertions.assertThatThrownBy(() -> orderTable.validateTableGroupNotExists())
                .isInstanceOf(TableGroupExistsException.class);
    }

    @Test
    void 테이블_그룹이_존재하지_않는다면_예외를_던지지_않는다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);

        // when, then
        Assertions.assertThatCode(() -> orderTable.validateTableGroupNotExists())
                .doesNotThrowAnyException();
    }

    @Test
    void 빈_테이블이라면_방문_인원_수를_변경하려_할_때_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, true);

        // when, then
        Assertions.assertThatThrownBy(() -> orderTable.changeNumberOfGuests(2))
                .isInstanceOf(OrderTableEmptyException.class);
    }

    @Test
    void 방문_인원_수가_음수라면_변경하려_할_때_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);

        // when, then
        Assertions.assertThatThrownBy(() -> orderTable.changeNumberOfGuests(-1))
                .isInstanceOf(NotEnoughGuestsException.class);
    }

    @Test
    void 방문_인원_수를_정상적으로_변경할_수_있다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);

        // when
        orderTable.changeNumberOfGuests(4);

        // then
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    void 테이블이_비지_않았다면_예외를_던진다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, false);

        // when, then
        Assertions.assertThatThrownBy(() -> orderTable.validateIsEmpty())
                .isInstanceOf(OrderTableNotEmptyException.class);
    }

    @Test
    void 테이블이_비었다면_예외를_던지지_않는다() {
        // given
        OrderTable orderTable = new OrderTable(null, 1, true);

        // when, then
        Assertions.assertThatCode(() -> orderTable.validateIsEmpty())
                .doesNotThrowAnyException();
    }
}

package kitchenpos.application;

import static kitchenpos.fixture.TableFixture.getOrderTable;
import static kitchenpos.fixture.TableFixture.getTableGroupRequest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TableGroupServiceTest extends ServiceTest{

    @Autowired
    private TableGroupService tableGroupService;

    @Test
    @DisplayName("존재하지 않는 테이블로 테이블 그룹을 생성하면 에외가 발생한다.")
    void createWithNonExistTables() {
        // given
        final OrderTable orderTable1 = getOrderTable(true);
        final OrderTable orderTable2 = getOrderTable(true);
        final OrderTable orderTable3 = getOrderTable(true);
        given(orderTableDao.findAllByIdIn(any())).willReturn(Arrays.asList(orderTable1, orderTable2));

        // when
        final TableGroup tableGroup = getTableGroupRequest(Arrays.asList(orderTable1,
                orderTable2,
                orderTable3));

        // then
        assertThatThrownBy(() -> tableGroupService.create(tableGroup))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 주문 테이블로 테이블 그룹을 생성할 수 없습니다.");
    }
}

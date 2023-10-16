package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.CreateMenuRequest;
import kitchenpos.dto.request.MenuProductRequest;
import kitchenpos.dto.response.MenuResponse;
import kitchenpos.exception.MenuGroupNotFoundException;
import kitchenpos.exception.MenuPriceIsBiggerThanActualPriceException;
import kitchenpos.exception.PriceIsNegativeException;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.ProductFixture;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class MenuServiceTest extends ServiceTestContext {

    @Test
    void 메뉴_가격이_0보다_작으면_예외를_던진다() {
        // given
        MenuGroup menuGroup = MenuGroupFixture.from("name");
        Product product = ProductFixture.of("name", BigDecimal.valueOf(1000L));

        menuGroupRepository.save(menuGroup);
        productRepository.save(product);

        CreateMenuRequest request = new CreateMenuRequest("menuName",
                BigDecimal.valueOf(-1),
                menuGroup.getId(),
                List.of(new MenuProductRequest(product.getId(), 1L)));

        // when, then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(PriceIsNegativeException.class);
    }

    @Test
    void 메뉴_그룹_아이디에_해당하는_메뉴_그룹이_없는_경우_예외를_던진다() {
        // given
        Product product = ProductFixture.of("name", BigDecimal.valueOf(1000L));

        productRepository.save(product);

        CreateMenuRequest request = new CreateMenuRequest("menuName",
                BigDecimal.valueOf(1000L),
                Long.MAX_VALUE,
                List.of(new MenuProductRequest(product.getId(), 1L)));

        // when, then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(MenuGroupNotFoundException.class);
    }

    @Test
    void 가격이_실제_메뉴_상품들의_총_가격보다_크면_예외를_던진다() {
        // given
        MenuGroup menuGroup = MenuGroupFixture.from("name");
        Product product = ProductFixture.of("name", BigDecimal.valueOf(1000L));

        menuGroupRepository.save(menuGroup);
        productRepository.save(product);

        CreateMenuRequest request = new CreateMenuRequest("menuName",
                BigDecimal.valueOf(2001L),
                menuGroup.getId(),
                List.of(new MenuProductRequest(product.getId(), 1L)));

        // when, then
        assertThatThrownBy(() -> menuService.create(request))
                .isInstanceOf(MenuPriceIsBiggerThanActualPriceException.class);
    }

    @Test
    void 메뉴를_정상적으로_생성하는_경우_생성한_메뉴가_반환된다() {
        // given
        MenuGroup menuGroup = MenuGroupFixture.from("name");
        Product product = ProductFixture.of("name", BigDecimal.valueOf(1000L));

        menuGroupRepository.save(menuGroup);
        productRepository.save(product);

        CreateMenuRequest request = new CreateMenuRequest("menuName",
                BigDecimal.valueOf(999L),
                menuGroup.getId(),
                List.of(new MenuProductRequest(product.getId(), 1L)));

        // when
        MenuResponse response = menuService.create(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getId()).isNotNull();
            softly.assertThat(response.getName()).isEqualTo(request.getName());
        });
    }

    @Test
    void 전체_메뉴를_조회할_수_있다() {
        // given
        MenuGroup menuGroup = MenuGroupFixture.from("name");
        Product product = ProductFixture.of("name", BigDecimal.valueOf(1000L));

        menuGroupRepository.save(menuGroup);
        productRepository.save(product);

        CreateMenuRequest request = new CreateMenuRequest("menuName",
                BigDecimal.valueOf(999L),
                menuGroup.getId(),
                List.of(new MenuProductRequest(product.getId(), 1L)));

        menuService.create(request);

        // when
        List<MenuResponse> response = menuService.findAll();

        // then
        assertThat(response).hasSize(1);
    }
}

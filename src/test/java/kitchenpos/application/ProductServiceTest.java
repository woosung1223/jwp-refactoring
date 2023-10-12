package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.dto.CreateProductRequest;
import kitchenpos.dto.ProductResponse;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest extends ServiceTestContext {

    @Test
    void 상품_생성_시_가격이_0보다_작으면_예외를_던진다() {
        // given
        CreateProductRequest request = new CreateProductRequest("name", BigDecimal.valueOf(-1L));

        // when, then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품_생성_시_가격을_지정하지_않았다면_예외를_던진다() {
        // given
        CreateProductRequest request = new CreateProductRequest("name", null);

        // when, then
        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 상품을_정상적으로_생성하는_경우_생성한_상품이_반환된다() {
        // given
        CreateProductRequest request = new CreateProductRequest("name", BigDecimal.valueOf(1000L));

        // when
        ProductResponse response = productService.create(request);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getId()).isNotNull();
            softly.assertThat(response.getName()).isEqualTo(request.getName());
        });
    }

    @Test
    void 전체_상품을_조회할_수_있다() {
        // given
        CreateProductRequest request = new CreateProductRequest("name", BigDecimal.valueOf(1000L));
        productService.create(request);

        // when
        List<ProductResponse> response = productService.findAll();

        // then
        assertThat(response).hasSize(2);
    }
}

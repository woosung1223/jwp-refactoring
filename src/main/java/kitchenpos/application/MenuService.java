package kitchenpos.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.application.dto.request.MenuCreateRequest;
import kitchenpos.application.dto.request.MenuProductCreateRequest;
import kitchenpos.application.dto.response.MenuResponse;
import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuDao menuDao;
    private final MenuGroupDao menuGroupDao;
    private final ProductDao productDao;

    public MenuService(
            final MenuDao menuDao,
            final MenuGroupDao menuGroupDao,
            final ProductDao productDao
    ) {
        this.menuDao = menuDao;
        this.menuGroupDao = menuGroupDao;
        this.productDao = productDao;
    }

    @Transactional
    public MenuResponse create(final MenuCreateRequest request) {
        if (!menuGroupDao.existsById(request.getMenuGroupId())) {
            throw new IllegalArgumentException("메뉴 그룹이 존재 하지 않습니다.");
        }
        final Menu menu = new Menu(
                request.getName(),
                request.getPrice(),
                request.getMenuGroupId(),
                mapToMenuProducts(request.getMenuProducts())
        );
        return MenuResponse.from(menuDao.save(menu));
    }

    private List<MenuProduct> mapToMenuProducts(final List<MenuProductCreateRequest> requests) {
        return requests.stream()
                .map(it -> {
                    final Product product = getProductById(it.getProductId());
                    return new MenuProduct(product.getId(), it.getQuantity(), product.getPrice());
                })
                .collect(Collectors.toList());
    }

    private Product getProductById(final Long productId) {
        return productDao.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품이 존재합니다."));
    }

    public List<MenuResponse> list() {
        final List<Menu> menus = menuDao.findAll();
        return menus.stream()
                .map(MenuResponse::from)
                .collect(Collectors.toList());
    }
}

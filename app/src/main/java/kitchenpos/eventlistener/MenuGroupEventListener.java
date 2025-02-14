package kitchenpos.eventlistener;

import kitchenpos.event.ValidateMenuGroupExistsEvent;
import kitchenpos.service.MenuGroupService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MenuGroupEventListener {

    private final MenuGroupService menuGroupService;

    public MenuGroupEventListener(MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @EventListener
    public void validateMenuGroupExists(ValidateMenuGroupExistsEvent event) {
        menuGroupService.validateMenuGroupExists(event.getMenuGroupId());
    }
}

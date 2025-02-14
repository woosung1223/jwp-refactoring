package kitchenpos.controller;

import java.net.URI;
import javax.validation.Valid;
import kitchenpos.dto.request.CreateTableGroupRequest;
import kitchenpos.dto.response.TableGroupResponse;
import kitchenpos.service.TableGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableGroupRestController {

    private final TableGroupService tableGroupService;

    public TableGroupRestController(TableGroupService tableGroupService) {
        this.tableGroupService = tableGroupService;
    }

    @PostMapping("/api/table-groups")
    public ResponseEntity<TableGroupResponse> create(@Valid @RequestBody CreateTableGroupRequest request) {
        TableGroupResponse response = tableGroupService.create(request);
        URI uri = URI.create("/api/table-groups/" + response.getId());

        return ResponseEntity.created(uri)
                .body(response);
    }

    @DeleteMapping("/api/table-groups/{tableGroupId}")
    public ResponseEntity<Void> ungroup(@PathVariable Long tableGroupId) {
        tableGroupService.ungroup(tableGroupId);

        return ResponseEntity.noContent()
                .build();
    }
}

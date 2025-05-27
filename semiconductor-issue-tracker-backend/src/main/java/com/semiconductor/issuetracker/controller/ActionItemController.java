package com.semiconductor.issuetracker.controller;

import com.semiconductor.issuetracker.model.ActionItem;
import com.semiconductor.issuetracker.service.ActionItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/action-items")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ActionItemController {

    private final ActionItemService actionItemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllActionItems(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String gbm,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String line,
            @RequestParam(required = false) String eqpId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {

        try {
            Map<String, Object> filters = new HashMap<>();
            if (status != null && !status.isEmpty()) filters.put("status", status);
            if (priority != null && !priority.isEmpty()) filters.put("priority", priority);
            if (assignee != null && !assignee.isEmpty()) filters.put("assignee", assignee);
            if (issueType != null && !issueType.isEmpty()) filters.put("issueType", issueType);
            if (gbm != null && !gbm.isEmpty()) filters.put("gbm", gbm);
            if (area != null && !area.isEmpty()) filters.put("area", area);
            if (line != null && !line.isEmpty()) filters.put("line", line);
            if (eqpId != null && !eqpId.isEmpty()) filters.put("eqpId", eqpId);
            if (title != null && !title.isEmpty()) filters.put("title", title);
            if (fromDate != null && !fromDate.isEmpty()) filters.put("fromDate", fromDate);
            if (toDate != null && !toDate.isEmpty()) filters.put("toDate", toDate);
            
            // Pagination
            filters.put("limit", size);
            filters.put("offset", page * size);
            filters.put("sortBy", sortBy);
            filters.put("sortOrder", sortOrder);

            List<ActionItem> actionItems = actionItemService.getActionItemsByFilters(filters);
            int totalCount = actionItemService.countActionItemsByFilters(filters);

            Map<String, Object> response = new HashMap<>();
            response.put("data", actionItems);
            response.put("totalCount", totalCount);
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting action items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action items: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getActionItemById(@PathVariable Long id) {
        try {
            ActionItem actionItem = actionItemService.getActionItemById(id);
            return ResponseEntity.ok(actionItem);
        } catch (RuntimeException e) {
            log.error("Error getting action item by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting action item by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action item: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createActionItem(@Valid @RequestBody ActionItem actionItem) {
        try {
            ActionItem createdActionItem = actionItemService.createActionItem(actionItem);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdActionItem);
        } catch (Exception e) {
            log.error("Error creating action item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create action item: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateActionItem(@PathVariable Long id, @Valid @RequestBody ActionItem actionItem) {
        try {
            actionItem.setId(id);
            ActionItem updatedActionItem = actionItemService.updateActionItem(actionItem);
            return ResponseEntity.ok(updatedActionItem);
        } catch (RuntimeException e) {
            log.error("Error updating action item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating action item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update action item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteActionItem(@PathVariable Long id) {
        try {
            actionItemService.deleteActionItem(id);
            return ResponseEntity.ok(Map.of("message", "Action item deleted successfully"));
        } catch (RuntimeException e) {
            log.error("Error deleting action item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting action item with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete action item: " + e.getMessage()));
        }
    }

    @GetMapping("/assignee/{assignee}")
    public ResponseEntity<Object> getActionItemsByAssignee(@PathVariable String assignee) {
        try {
            List<ActionItem> actionItems = actionItemService.getActionItemsByAssignee(assignee);
            return ResponseEntity.ok(actionItems);
        } catch (Exception e) {
            log.error("Error getting action items by assignee: {}", assignee, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action items: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getActionItemsByStatus(@PathVariable String status) {
        try {
            List<ActionItem> actionItems = actionItemService.getActionItemsByStatus(status);
            return ResponseEntity.ok(actionItems);
        } catch (Exception e) {
            log.error("Error getting action items by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action items: " + e.getMessage()));
        }
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Object> getActionItemsByPriority(@PathVariable String priority) {
        try {
            List<ActionItem> actionItems = actionItemService.getActionItemsByPriority(priority);
            return ResponseEntity.ok(actionItems);
        } catch (Exception e) {
            log.error("Error getting action items by priority: {}", priority, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action items: " + e.getMessage()));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<Object> getOverdueActionItems() {
        try {
            List<ActionItem> actionItems = actionItemService.getOverdueActionItems();
            return ResponseEntity.ok(actionItems);
        } catch (Exception e) {
            log.error("Error getting overdue action items", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get overdue action items: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getActionItemStatistics() {
        try {
            Map<String, Long> statistics = actionItemService.getActionItemStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error getting action item statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }
}

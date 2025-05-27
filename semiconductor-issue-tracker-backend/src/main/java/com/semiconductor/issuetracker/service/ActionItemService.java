package com.semiconductor.issuetracker.service;

import com.semiconductor.issuetracker.mapper.ActionItemMapper;
import com.semiconductor.issuetracker.model.ActionItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ActionItemService {

    private final ActionItemMapper actionItemMapper;

    public List<ActionItem> getAllActionItems() {
        log.debug("Getting all action items");
        return actionItemMapper.findAll();
    }

    public List<ActionItem> getActionItemsByFilters(Map<String, Object> filters) {
        log.debug("Getting action items with filters: {}", filters);
        return actionItemMapper.findByFilters(filters);
    }

    public ActionItem getActionItemById(Long id) {
        log.debug("Getting action item by id: {}", id);
        ActionItem actionItem = actionItemMapper.findById(id);
        if (actionItem == null) {
            throw new RuntimeException("Action item not found with id: " + id);
        }
        return actionItem;
    }

    public ActionItem createActionItem(ActionItem actionItem) {
        log.debug("Creating new action item: {}", actionItem.getTitle());
        
        // Set default values
        if (actionItem.getStatus() == null) {
            actionItem.setStatus("OPEN");
        }
        if (actionItem.getPriority() == null) {
            actionItem.setPriority("MEDIUM");
        }
        
        int result = actionItemMapper.insert(actionItem);
        if (result > 0) {
            log.info("Action item created successfully with id: {}", actionItem.getId());
            return actionItem;
        } else {
            throw new RuntimeException("Failed to create action item");
        }
    }

    public ActionItem updateActionItem(ActionItem actionItem) {
        log.debug("Updating action item with id: {}", actionItem.getId());
        
        // Check if action item exists
        ActionItem existing = actionItemMapper.findById(actionItem.getId());
        if (existing == null) {
            throw new RuntimeException("Action item not found with id: " + actionItem.getId());
        }
        
        // Set resolved_at if status is changed to RESOLVED
        if ("RESOLVED".equals(actionItem.getStatus()) && !"RESOLVED".equals(existing.getStatus())) {
            actionItem.setResolvedAt(LocalDateTime.now());
        }
        
        int result = actionItemMapper.update(actionItem);
        if (result > 0) {
            log.info("Action item updated successfully with id: {}", actionItem.getId());
            return actionItemMapper.findById(actionItem.getId());
        } else {
            throw new RuntimeException("Failed to update action item");
        }
    }

    public void deleteActionItem(Long id) {
        log.debug("Deleting action item with id: {}", id);
        
        // Check if action item exists
        ActionItem existing = actionItemMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Action item not found with id: " + id);
        }
        
        int result = actionItemMapper.deleteById(id);
        if (result > 0) {
            log.info("Action item deleted successfully with id: {}", id);
        } else {
            throw new RuntimeException("Failed to delete action item");
        }
    }

    public int countActionItemsByFilters(Map<String, Object> filters) {
        log.debug("Counting action items with filters: {}", filters);
        return actionItemMapper.countByFilters(filters);
    }

    public List<ActionItem> getActionItemsByAssignee(String assignee) {
        log.debug("Getting action items by assignee: {}", assignee);
        return actionItemMapper.findByAssignee(assignee);
    }

    public List<ActionItem> getActionItemsByStatus(String status) {
        log.debug("Getting action items by status: {}", status);
        return actionItemMapper.findByStatus(status);
    }

    public List<ActionItem> getActionItemsByPriority(String priority) {
        log.debug("Getting action items by priority: {}", priority);
        return actionItemMapper.findByPriority(priority);
    }

    public List<ActionItem> getOverdueActionItems() {
        log.debug("Getting overdue action items");
        return actionItemMapper.findOverdue();
    }

    public Map<String, Long> getActionItemStatistics() {
        log.debug("Getting action item statistics");
        
        List<ActionItem> allItems = actionItemMapper.findAll();
        
        long totalCount = allItems.size();
        long openCount = allItems.stream().filter(item -> "OPEN".equals(item.getStatus())).count();
        long inProgressCount = allItems.stream().filter(item -> "IN_PROGRESS".equals(item.getStatus())).count();
        long resolvedCount = allItems.stream().filter(item -> "RESOLVED".equals(item.getStatus())).count();
        long closedCount = allItems.stream().filter(item -> "CLOSED".equals(item.getStatus())).count();
        long highPriorityCount = allItems.stream().filter(item -> "HIGH".equals(item.getPriority()) || "CRITICAL".equals(item.getPriority())).count();
        long overdueCount = actionItemMapper.findOverdue().size();
        
        return Map.of(
            "total", totalCount,
            "open", openCount,
            "inProgress", inProgressCount,
            "resolved", resolvedCount,
            "closed", closedCount,
            "highPriority", highPriorityCount,
            "overdue", overdueCount
        );
    }
}

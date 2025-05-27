package com.semiconductor.issuetracker.controller;

import com.semiconductor.issuetracker.model.Inform;
import com.semiconductor.issuetracker.service.InformService;
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
@RequestMapping("/informs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InformController {

    private final InformService informService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInforms(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Map<String, Object> filters = new HashMap<>();
            if (status != null && !status.isEmpty()) filters.put("status", status);
            if (priority != null && !priority.isEmpty()) filters.put("priority", priority);
            if (category != null && !category.isEmpty()) filters.put("category", category);
            if (author != null && !author.isEmpty()) filters.put("author", author);
            if (title != null && !title.isEmpty()) filters.put("title", title);
            if (fromDate != null && !fromDate.isEmpty()) filters.put("fromDate", fromDate);
            if (toDate != null && !toDate.isEmpty()) filters.put("toDate", toDate);
            
            // Pagination
            filters.put("limit", size);
            filters.put("offset", page * size);

            List<Inform> informs = informService.getInformsByFilters(filters);
            int totalCount = informService.countInformsByFilters(filters);

            Map<String, Object> response = new HashMap<>();
            response.put("data", informs);
            response.put("totalCount", totalCount);
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("totalPages", (int) Math.ceil((double) totalCount / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting informs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get informs: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getInformById(@PathVariable Long id) {
        try {
            Inform inform = informService.getInformById(id);
            return ResponseEntity.ok(inform);
        } catch (RuntimeException e) {
            log.error("Error getting inform by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting inform by id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get inform: " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Object> createInform(@Valid @RequestBody Inform inform) {
        try {
            Inform createdInform = informService.createInform(inform);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInform);
        } catch (Exception e) {
            log.error("Error creating inform", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create inform: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateInform(@PathVariable Long id, @Valid @RequestBody Inform inform) {
        try {
            inform.setId(id);
            Inform updatedInform = informService.updateInform(inform);
            return ResponseEntity.ok(updatedInform);
        } catch (RuntimeException e) {
            log.error("Error updating inform with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating inform with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update inform: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteInform(@PathVariable Long id) {
        try {
            informService.deleteInform(id);
            return ResponseEntity.ok(Map.of("message", "Inform deleted successfully"));
        } catch (RuntimeException e) {
            log.error("Error deleting inform with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting inform with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete inform: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Object> getInformsByStatus(@PathVariable String status) {
        try {
            List<Inform> informs = informService.getInformsByStatus(status);
            return ResponseEntity.ok(informs);
        } catch (Exception e) {
            log.error("Error getting informs by status: {}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get informs: " + e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Object> getInformsByCategory(@PathVariable String category) {
        try {
            List<Inform> informs = informService.getInformsByCategory(category);
            return ResponseEntity.ok(informs);
        } catch (Exception e) {
            log.error("Error getting informs by category: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get informs: " + e.getMessage()));
        }
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Object> getInformsByPriority(@PathVariable String priority) {
        try {
            List<Inform> informs = informService.getInformsByPriority(priority);
            return ResponseEntity.ok(informs);
        } catch (Exception e) {
            log.error("Error getting informs by priority: {}", priority, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get informs: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Object> getInformStatistics() {
        try {
            Map<String, Long> statistics = informService.getInformStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            log.error("Error getting inform statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get statistics: " + e.getMessage()));
        }
    }
}

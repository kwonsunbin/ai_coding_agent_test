package com.semiconductor.issuetracker.service;

import com.semiconductor.issuetracker.mapper.InformMapper;
import com.semiconductor.issuetracker.model.Inform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InformService {

    private final InformMapper informMapper;

    public List<Inform> getAllInforms() {
        log.debug("Getting all informs");
        return informMapper.findAll();
    }

    public List<Inform> getInformsByFilters(Map<String, Object> filters) {
        log.debug("Getting informs with filters: {}", filters);
        return informMapper.findByFilters(filters);
    }

    public Inform getInformById(Long id) {
        log.debug("Getting inform by id: {}", id);
        Inform inform = informMapper.findById(id);
        if (inform == null) {
            throw new RuntimeException("Inform not found with id: " + id);
        }
        return inform;
    }

    public Inform createInform(Inform inform) {
        log.debug("Creating new inform: {}", inform.getTitle());
        
        // Generate inform ID
        String informId = generateInformId();
        inform.setInformId(informId);
        
        // Set default values
        if (inform.getStatus() == null) {
            inform.setStatus("ACTIVE");
        }
        if (inform.getPriority() == null) {
            inform.setPriority("MEDIUM");
        }
        if (inform.getNotifyEmail() == null) {
            inform.setNotifyEmail(true);
        }
        if (inform.getNotifySms() == null) {
            inform.setNotifySms(false);
        }
        if (inform.getUrgent() == null) {
            inform.setUrgent(false);
        }
        
        int result = informMapper.insert(inform);
        if (result > 0) {
            log.info("Inform created successfully with id: {}", inform.getId());
            return inform;
        } else {
            throw new RuntimeException("Failed to create inform");
        }
    }

    public Inform updateInform(Inform inform) {
        log.debug("Updating inform with id: {}", inform.getId());
        
        // Check if inform exists
        Inform existing = informMapper.findById(inform.getId());
        if (existing == null) {
            throw new RuntimeException("Inform not found with id: " + inform.getId());
        }
        
        int result = informMapper.update(inform);
        if (result > 0) {
            log.info("Inform updated successfully with id: {}", inform.getId());
            return informMapper.findById(inform.getId());
        } else {
            throw new RuntimeException("Failed to update inform");
        }
    }

    public void deleteInform(Long id) {
        log.debug("Deleting inform with id: {}", id);
        
        // Check if inform exists
        Inform existing = informMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("Inform not found with id: " + id);
        }
        
        int result = informMapper.deleteById(id);
        if (result > 0) {
            log.info("Inform deleted successfully with id: {}", id);
        } else {
            throw new RuntimeException("Failed to delete inform");
        }
    }

    public int countInformsByFilters(Map<String, Object> filters) {
        log.debug("Counting informs with filters: {}", filters);
        return informMapper.countByFilters(filters);
    }

    public List<Inform> getInformsByStatus(String status) {
        log.debug("Getting informs by status: {}", status);
        return informMapper.findByStatus(status);
    }

    public List<Inform> getInformsByCategory(String category) {
        log.debug("Getting informs by category: {}", category);
        return informMapper.findByCategory(category);
    }

    public List<Inform> getInformsByPriority(String priority) {
        log.debug("Getting informs by priority: {}", priority);
        return informMapper.findByPriority(priority);
    }

    public Map<String, Long> getInformStatistics() {
        log.debug("Getting inform statistics");
        
        List<Inform> allInforms = informMapper.findAll();
        
        long totalCount = allInforms.size();
        long activeCount = allInforms.stream().filter(inform -> "ACTIVE".equals(inform.getStatus())).count();
        long resolvedCount = allInforms.stream().filter(inform -> "RESOLVED".equals(inform.getStatus())).count();
        long closedCount = allInforms.stream().filter(inform -> "CLOSED".equals(inform.getStatus())).count();
        long criticalCount = allInforms.stream().filter(inform -> "CRITICAL".equals(inform.getPriority())).count();
        long urgentCount = allInforms.stream().filter(inform -> Boolean.TRUE.equals(inform.getUrgent())).count();
        
        return Map.of(
            "total", totalCount,
            "active", activeCount,
            "resolved", resolvedCount,
            "closed", closedCount,
            "critical", criticalCount,
            "urgent", urgentCount
        );
    }

    private String generateInformId() {
        // Generate inform ID in format: INF-YYYY-NNN
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        List<Inform> existingInforms = informMapper.findAll();
        
        // Find the highest number for current year
        int maxNumber = existingInforms.stream()
            .filter(inform -> inform.getInformId() != null && inform.getInformId().startsWith("INF-" + year))
            .mapToInt(inform -> {
                try {
                    String numberPart = inform.getInformId().substring(9); // After "INF-YYYY-"
                    return Integer.parseInt(numberPart);
                } catch (Exception e) {
                    return 0;
                }
            })
            .max()
            .orElse(0);
        
        return String.format("INF-%s-%03d", year, maxNumber + 1);
    }
}

package com.semiconductor.issuetracker.mapper;

import com.semiconductor.issuetracker.model.ActionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActionItemMapper {
    
    List<ActionItem> findAll();
    
    List<ActionItem> findByFilters(@Param("filters") Map<String, Object> filters);
    
    ActionItem findById(@Param("id") Long id);
    
    int insert(ActionItem actionItem);
    
    int update(ActionItem actionItem);
    
    int deleteById(@Param("id") Long id);
    
    int countByFilters(@Param("filters") Map<String, Object> filters);
    
    List<ActionItem> findByAssignee(@Param("assignee") String assignee);
    
    List<ActionItem> findByStatus(@Param("status") String status);
    
    List<ActionItem> findByPriority(@Param("priority") String priority);
    
    List<ActionItem> findOverdue();
}

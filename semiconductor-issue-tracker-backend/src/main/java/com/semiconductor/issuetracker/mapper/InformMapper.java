package com.semiconductor.issuetracker.mapper;

import com.semiconductor.issuetracker.model.Inform;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InformMapper {
    
    List<Inform> findAll();
    
    List<Inform> findByFilters(@Param("filters") Map<String, Object> filters);
    
    Inform findById(@Param("id") Long id);
    
    int insert(Inform inform);
    
    int update(Inform inform);
    
    int deleteById(@Param("id") Long id);
    
    int countByFilters(@Param("filters") Map<String, Object> filters);
    
    List<Inform> findByStatus(@Param("status") String status);
    
    List<Inform> findByCategory(@Param("category") String category);
    
    List<Inform> findByPriority(@Param("priority") String priority);
}

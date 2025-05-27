package com.semiconductor.issuetracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActionItem {
    private Long id;
    private String title;
    private String description;
    private String issueType;
    private String status;
    private String priority;
    private String assignee;
    private String reporter;
    private String gbm;
    private String area;
    private String line;
    private String sdwt;
    private String eqpModel;
    private String prcGroup;
    private String eqpId;
    private String fdcModel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
}

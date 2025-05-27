package com.semiconductor.issuetracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inform {
    private Long id;
    private String informId;
    private String title;
    private String description;
    private String author;
    private String priority;
    private String category;
    private String targetDepartment;
    private String gbm;
    private String area;
    private String line;
    private String equipmentId;
    private String actionTaken;
    private String impactAssessment;
    private String status;
    private Boolean notifyEmail;
    private Boolean notifySms;
    private Boolean urgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InformAttachment> attachments;
}

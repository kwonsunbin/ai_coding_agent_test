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
public class CommentAttachment {
    private Long id;
    private Long commentId;
    private String fileName;
    private String fileSize;
    private String filePath;
    private LocalDateTime createdAt;
}

-- 액션 아이템 테이블
CREATE TABLE IF NOT EXISTS action_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    issue_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    assignee VARCHAR(100),
    reporter VARCHAR(100),
    gbm VARCHAR(50),
    area VARCHAR(50),
    line VARCHAR(50),
    sdwt VARCHAR(50),
    eqp_model VARCHAR(100),
    prc_group VARCHAR(50),
    eqp_id VARCHAR(100),
    fdc_model VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    due_date TIMESTAMP,
    resolved_at TIMESTAMP
);

-- 댓글 테이블
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_item_id BIGINT NOT NULL,
    author VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (action_item_id) REFERENCES action_items(id) ON DELETE CASCADE
);

-- 댓글 첨부파일 테이블
CREATE TABLE IF NOT EXISTS comment_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size VARCHAR(20),
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

-- PM/BM 이력 테이블
CREATE TABLE IF NOT EXISTS pm_bm_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id VARCHAR(100) NOT NULL,
    maintenance_type VARCHAR(20) NOT NULL, -- PM, BM
    work_content TEXT,
    assignee VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Down 이력 테이블
CREATE TABLE IF NOT EXISTS down_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id VARCHAR(100) NOT NULL,
    down_reason TEXT,
    down_type VARCHAR(50),
    impact_level VARCHAR(20),
    started_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    duration_minutes INT,
    recovery_action TEXT,
    responsible_person VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- RACB 이력 테이블
CREATE TABLE IF NOT EXISTS racb_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    racb_id VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    requester VARCHAR(100) NOT NULL,
    approver VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approval_date TIMESTAMP,
    implementation_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인폼 테이블
CREATE TABLE IF NOT EXISTS informs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inform_id VARCHAR(50) UNIQUE NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    category VARCHAR(50) NOT NULL,
    target_department VARCHAR(50),
    gbm VARCHAR(50),
    area VARCHAR(50),
    line VARCHAR(50),
    equipment_id VARCHAR(100),
    action_taken TEXT,
    impact_assessment TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    notify_email BOOLEAN DEFAULT TRUE,
    notify_sms BOOLEAN DEFAULT FALSE,
    urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인폼 첨부파일 테이블
CREATE TABLE IF NOT EXISTS inform_attachments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inform_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size VARCHAR(20),
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inform_id) REFERENCES informs(id) ON DELETE CASCADE
);

-- 설비 정보 테이블 (참조용)
CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    equipment_id VARCHAR(100) UNIQUE NOT NULL,
    equipment_name VARCHAR(255),
    gbm VARCHAR(50),
    area VARCHAR(50),
    line VARCHAR(50),
    sdwt VARCHAR(50),
    eqp_model VARCHAR(100),
    prc_group VARCHAR(50),
    fdc_model VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_action_items_status ON action_items(status);
CREATE INDEX IF NOT EXISTS idx_action_items_assignee ON action_items(assignee);
CREATE INDEX IF NOT EXISTS idx_action_items_created_at ON action_items(created_at);
CREATE INDEX IF NOT EXISTS idx_comments_action_item_id ON comments(action_item_id);
CREATE INDEX IF NOT EXISTS idx_pm_bm_history_equipment_id ON pm_bm_history(equipment_id);
CREATE INDEX IF NOT EXISTS idx_down_history_equipment_id ON down_history(equipment_id);
CREATE INDEX IF NOT EXISTS idx_down_history_started_at ON down_history(started_at);
CREATE INDEX IF NOT EXISTS idx_racb_history_status ON racb_history(status);
CREATE INDEX IF NOT EXISTS idx_informs_status ON informs(status);
CREATE INDEX IF NOT EXISTS idx_informs_category ON informs(category);
CREATE INDEX IF NOT EXISTS idx_informs_created_at ON informs(created_at);
CREATE INDEX IF NOT EXISTS idx_equipment_equipment_id ON equipment(equipment_id);

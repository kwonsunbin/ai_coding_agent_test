-- 설비 정보 샘플 데이터
INSERT INTO equipment (equipment_id, equipment_name, gbm, area, line, sdwt, eqp_model, prc_group, fdc_model) VALUES
('EQP001', 'CVD Chamber 1', 'GBM1', 'FAB1', 'LINE1', 'SDWT1', 'CVD', 'PRC1', 'FDC1'),
('EQP002', 'Etch Chamber 1', 'GBM1', 'FAB1', 'LINE1', 'SDWT1', 'ETCH', 'PRC2', 'FDC2'),
('EQP003', 'Litho Tool 1', 'GBM2', 'FAB2', 'LINE2', 'SDWT2', 'LITHO', 'PRC3', 'FDC3'),
('EQP004', 'CVD Chamber 2', 'GBM2', 'FAB2', 'LINE2', 'SDWT2', 'CVD', 'PRC1', 'FDC1'),
('EQP005', 'Etch Chamber 2', 'GBM3', 'FAB3', 'LINE3', 'SDWT3', 'ETCH', 'PRC2', 'FDC2');

-- 액션 아이템 샘플 데이터
INSERT INTO action_items (title, description, issue_type, status, priority, assignee, reporter, gbm, area, line, eqp_id, due_date) VALUES
('CVD 챔버 온도 이상 현상 조치', 'CVD 챔버의 온도가 설정값보다 높게 측정되는 현상 발생', 'EQUIPMENT', 'IN_PROGRESS', 'HIGH', '김설비', '이모니터', 'GBM1', 'FAB1', 'LINE1', 'EQP001', '2024-02-01 18:00:00'),
('Etch 공정 균일도 개선', 'Etch 공정에서 웨이퍼 전체 균일도가 기준치를 벗어남', 'PROCESS', 'OPEN', 'MEDIUM', '이공정', '박품질', 'GBM1', 'FAB1', 'LINE1', 'EQP002', '2024-02-05 17:00:00'),
('FDC 알람 임계값 조정', 'FDC 시스템의 알람 임계값이 너무 민감하게 설정되어 있음', 'FDC', 'RESOLVED', 'LOW', '박모니터', '최엔지니어', 'GBM2', 'FAB2', 'LINE2', 'EQP003', '2024-01-25 16:00:00'),
('정기 유지보수 스케줄 조정', '설비 유지보수 일정 재조정 필요', 'MAINTENANCE', 'PENDING', 'MEDIUM', '최유지', '한관리', 'GBM2', 'FAB2', 'LINE2', 'EQP004', '2024-02-10 14:00:00');

-- 댓글 샘플 데이터
INSERT INTO comments (action_item_id, author, content) VALUES
(1, '김설비', '온도 센서를 점검한 결과 센서 자체에는 이상이 없었습니다.\n\n히터 컨트롤러 설정을 확인해보겠습니다.'),
(1, '이엔지니어', '히터 컨트롤러 펌웨어 버전을 확인해보시기 바랍니다.\n\n최근 업데이트 후 유사한 이슈가 다른 챔버에서도 발생했었습니다.'),
(2, '이공정', '웨이퍼 매핑 데이터를 분석한 결과 가장자리 영역에서 에칭 속도가 느린 것을 확인했습니다.\n\n가스 플로우 패턴을 조정해보겠습니다.'),
(3, '박모니터', 'FDC 임계값을 기존 대비 20% 완화하여 설정했습니다. 모니터링 결과 정상 동작 확인되었습니다.');

-- 댓글 첨부파일 샘플 데이터
INSERT INTO comment_attachments (comment_id, file_name, file_size) VALUES
(1, 'temperature_log.xlsx', '2.3MB'),
(1, 'sensor_check.pdf', '1.1MB'),
(3, 'wafer_map.png', '856KB');

-- PM/BM 이력 샘플 데이터
INSERT INTO pm_bm_history (equipment_id, maintenance_type, work_content, assignee, status, scheduled_date, started_at, completed_at) VALUES
('EQP001', 'PM', 'CVD 챔버 정기 점검 및 부품 교체', '김유지', 'COMPLETED', '2024-01-15 09:00:00', '2024-01-15 09:00:00', '2024-01-15 17:00:00'),
('EQP002', 'BM', 'Etch 챔버 플라즈마 소스 교체', '이수리', 'IN_PROGRESS', '2024-01-20 10:00:00', '2024-01-20 10:00:00', NULL),
('EQP003', 'PM', 'Litho 툴 렌즈 청소 및 캘리브레이션', '박정비', 'SCHEDULED', '2024-02-01 08:00:00', NULL, NULL),
('EQP004', 'PM', 'CVD 챔버 가스 라인 점검', '최점검', 'COMPLETED', '2024-01-10 14:00:00', '2024-01-10 14:00:00', '2024-01-10 18:00:00');

-- Down 이력 샘플 데이터
INSERT INTO down_history (equipment_id, down_reason, down_type, impact_level, started_at, ended_at, duration_minutes, recovery_action, responsible_person) VALUES
('EQP001', 'CVD 챔버 온도 센서 오류', 'UNPLANNED', 'HIGH', '2024-01-18 14:30:00', '2024-01-18 16:45:00', 135, '온도 센서 교체 및 캘리브레이션', '김설비'),
('EQP002', 'Etch 가스 공급 라인 누설', 'UNPLANNED', 'CRITICAL', '2024-01-22 11:20:00', '2024-01-22 15:10:00', 230, '가스 라인 밸브 교체', '이수리'),
('EQP003', '정기 유지보수', 'PLANNED', 'LOW', '2024-01-25 08:00:00', '2024-01-25 17:00:00', 540, '정기 PM 작업 완료', '박정비'),
('EQP004', 'RF 파워 서플라이 고장', 'UNPLANNED', 'MEDIUM', '2024-01-28 13:15:00', '2024-01-28 14:30:00', 75, 'RF 파워 서플라이 교체', '최수리');

-- RACB 이력 샘플 데이터
INSERT INTO racb_history (racb_id, title, description, requester, approver, status, priority, request_date, approval_date, implementation_date) VALUES
('RACB-2024-001', '설비 A 공정 파라미터 변경 요청', 'CVD 설비의 온도 파라미터 조정이 필요합니다.', '김설비', '이관리', 'APPROVED', 'HIGH', '2024-01-15 09:30:00', '2024-01-15 14:20:00', '2024-01-16 10:00:00'),
('RACB-2024-002', '새로운 레시피 등록 요청', '신제품 대응을 위한 새로운 공정 레시피 등록', '박공정', NULL, 'PENDING', 'MEDIUM', '2024-01-16 11:45:00', NULL, NULL),
('RACB-2024-003', '설비 B 유지보수 스케줄 변경', '정기 유지보수 일정 조정 요청', '최유지', '정승인', 'IMPLEMENTED', 'LOW', '2024-01-17 08:20:00', '2024-01-17 16:30:00', '2024-01-18 09:00:00'),
('RACB-2024-004', '긴급 설비 수리 승인 요청', '설비 고장으로 인한 긴급 수리 필요', '한긴급', '이관리', 'REJECTED', 'CRITICAL', '2024-01-18 15:10:00', '2024-01-18 16:45:00', NULL);

-- 인폼 샘플 데이터
INSERT INTO informs (inform_id, title, description, author, priority, category, target_department, gbm, area, line, equipment_id, action_taken, impact_assessment, status, notify_email, notify_sms, urgent) VALUES
('INF-2024-001', 'CVD 챔버 온도 이상 발생', 'CVD 챔버의 온도가 설정값보다 10도 높게 측정되고 있습니다. 즉시 점검이 필요합니다.', '김설비', 'CRITICAL', 'EQUIPMENT', 'FAB', 'GBM1', 'FAB1', 'LINE1', 'EQP001', '온도 센서 점검 및 히터 컨트롤러 확인 예정', '생산 중단 위험, 품질 영향 가능성', 'ACTIVE', true, true, true),
('INF-2024-002', 'Etch 공정 균일도 저하', '웨이퍼 가장자리 영역에서 에칭 속도가 중앙 대비 15% 느린 현상이 관찰됩니다.', '이공정', 'HIGH', 'PROCESS', 'ENGINEERING', 'GBM1', 'FAB1', 'LINE1', 'EQP002', '가스 플로우 패턴 조정 및 플라즈마 파워 최적화 진행', '제품 품질 저하, 수율 감소', 'ACTIVE', true, false, false),
('INF-2024-003', '정기 유지보수 완료 보고', 'LINE2 설비들의 정기 유지보수가 계획대로 완료되었습니다.', '박유지', 'LOW', 'MAINTENANCE', '', 'GBM2', 'FAB2', 'LINE2', '', '모든 설비 정상 동작 확인 완료', '생산성 향상 기대', 'RESOLVED', true, false, false),
('INF-2024-004', '품질 검사 결과 이상', '최근 생산된 제품에서 미세한 결함이 발견되었습니다. 원인 분석이 필요합니다.', '최품질', 'MEDIUM', 'QUALITY', 'QUALITY', 'GBM3', 'FAB3', 'LINE3', '', 'SEM 분석 및 공정 파라미터 재검토 예정', '고객 클레임 가능성, 출하 지연', 'ACTIVE', true, false, false),
('INF-2024-005', '안전 교육 실시 안내', '다음 주 화요일 오후 2시에 전 직원 대상 안전 교육을 실시합니다.', '한안전', 'MEDIUM', 'SAFETY', '', '', '', '', '', '교육 자료 준비 및 참석자 명단 작성 완료', '안전 의식 향상', 'ACTIVE', true, false, false);

-- 인폼 첨부파일 샘플 데이터
INSERT INTO inform_attachments (inform_id, file_name, file_size) VALUES
(1, 'temperature_trend.xlsx', '1.5MB'),
(1, 'chamber_photo.jpg', '2.1MB'),
(2, 'uniformity_data.csv', '850KB'),
(4, 'defect_analysis.pdf', '3.2MB');

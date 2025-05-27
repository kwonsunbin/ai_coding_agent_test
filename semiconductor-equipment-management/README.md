# 반도체 설비 관리 시스템

Vue.js 기반의 반도체 설비 고장 예측 및 관리 웹 애플리케이션입니다.

## 🎯 주요 기능

### 📊 대시보드
- 실시간 설비 현황 모니터링
- 주요 지표 카드 (총 설비 수, 정상 가동, 경고 상태, 점검 필요)
- 설비 상태 분포 차트
- 예측 알림 트렌드 분석
- AI 기반 고장 예측 결과 표시

### 🏭 설비 관리
- 전체 설비 목록 및 상세 정보
- 실시간 센서 데이터 시각화
- 설비별 성능 트렌드 차트
- 가동률 및 효율성 모니터링
- 정비 이력 관리

### 📝 변경점 추적
- 설비 변경 이력 타임라인
- 변경 유형별 필터링 (설정 변경, 정비 작업, 업그레이드, 수리)
- 변경점 상세 정보 및 영향 분석
- 작업 체크리스트 관리
- 첨부 파일 지원

### 🚨 알림 시스템
- 실시간 알림 관리
- 심각도별 분류 (긴급, 경고, 정보)
- AI 예측 분석 결과 포함
- 권장 조치 사항 제공
- 알림 상태 관리 (읽지 않음, 읽음, 확인됨, 해결됨)

## 🎨 디자인 특징

### 신뢰감 있는 전문적 디자인
- 깔끔하고 직관적인 UI/UX
- 일관된 색상 체계 (Primary Blue, Success Green, Warning Orange, Danger Red)
- 명확한 상태 표시 및 배지 시스템

### 반응형 디자인
- 데스크탑 및 모바일 최적화
- 모바일용 하단 네비게이션
- 유연한 그리드 레이아웃

### 데이터 시각화
- Chart.js 기반 차트 시스템
- 실시간 데이터 업데이트
- 직관적인 상태 표시

## 🛠 기술 스택

- **Frontend**: Vue.js 3 (Composition API)
- **Routing**: Vue Router 4
- **Styling**: Tailwind CSS
- **Charts**: Chart.js + Vue-ChartJS
- **Icons**: Heroicons
- **Build Tool**: Vite
- **Date Handling**: date-fns

## 📁 프로젝트 구조

```
semiconductor-equipment-management/
├── src/
│   ├── views/
│   │   ├── Dashboard.vue          # 메인 대시보드
│   │   ├── EquipmentList.vue      # 설비 목록
│   │   ├── EquipmentDetail.vue    # 설비 상세 정보
│   │   ├── ChangeTracking.vue     # 변경점 추적
│   │   ├── ChangeDetail.vue       # 변경점 상세
│   │   └── Alerts.vue             # 알림 관리
│   ├── App.vue                    # 메인 앱 컴포넌트
│   ├── main.js                    # 앱 진입점
│   └── style.css                  # 글로벌 스타일
├── demo.html                      # 데모 페이지
├── package.json
├── vite.config.js
├── tailwind.config.js
└── README.md
```

## 🚀 설치 및 실행

### 필요 조건
- Node.js 16.0 이상
- npm 또는 yarn

### 설치
```bash
# 의존성 설치
npm install

# 개발 서버 실행
npm run dev

# 빌드
npm run build
```

### 데모 확인
npm이 설치되어 있지 않은 경우, `demo.html` 파일을 브라우저에서 직접 열어 디자인을 확인할 수 있습니다.

```bash
# 데모 페이지 열기
open demo.html
```

## 👥 대상 사용자

### 공장 관리자
- 전체 설비 현황 모니터링
- 생산성 및 효율성 분석
- 의사결정 지원 정보 제공

### 유지보수 엔지니어
- 설비별 상세 정보 확인
- 정비 일정 및 이력 관리
- 고장 예측 및 예방 정비

## 🔮 AI 예측 기능

### 고장 예측
- 센서 데이터 패턴 분석
- 머신러닝 기반 고장 시점 예측
- 신뢰도 및 예상 시점 제공

### 정비 권장
- 예방 정비 일정 최적화
- 부품 교체 시기 예측
- 비용 효율적 정비 계획

### 효율성 개선
- 공정 파라미터 최적화 제안
- 에너지 효율성 개선 방안
- 생산성 향상 기회 식별

## 📱 모바일 지원

- 터치 친화적 인터페이스
- 하단 네비게이션 바
- 스와이프 제스처 지원
- 반응형 테이블 및 차트

## 🔒 보안 고려사항

- 사용자 인증 및 권한 관리
- 데이터 암호화
- 감사 로그 기록
- 접근 제어

## 📈 향후 개발 계획

- [ ] 실시간 WebSocket 연결
- [ ] 고급 분석 대시보드
- [ ] 모바일 앱 개발
- [ ] API 통합
- [ ] 다국어 지원
- [ ] 고급 보고서 기능

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

프로젝트 관련 문의사항이 있으시면 언제든지 연락주세요.

---

**반도체 설비 관리 시스템** - 스마트 팩토리를 위한 지능형 설비 관리 솔루션

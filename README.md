# Android PPOS MVP Skeleton

Kotlin + Compose + Room 기반으로 다음 MVP 흐름을 구현한 초기 코드입니다.

- TableHome: 구역별 테이블 상태(EMPTY/OCCUPIED/BILLING), 금액, 경과시간 표시
- FoodCourtHome: 카테고리/메뉴 그리드, 장바구니, 주문 생성
- Room 트랜잭션: 주문 생성, 테이블 이동, 합석(아이템 병합)
- Driver 추상화: `ReceiptDriver` + `FakeReceiptDriver`

## 아키텍처

UI(Compose) ↔ ViewModel(UiState/Action) ↔ Repository ↔ DAO(Room)

## 주의

- 현재는 초기 데이터(areas/tables/menu) 시딩 로직이 없으므로 DB가 비어있으면 화면 데이터가 없습니다.
- 실제 장치 SDK 연동은 `TODO(VENDOR_SDK)`로 인터페이스만 유지해야 합니다.

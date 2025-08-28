# IT BookStore Clean Architecture

IT 도서 정보를 제공하는 안드로이드 애플리케이션으로, Clean Architecture 패턴을 적용하여 개발되었습니다. **Gemini AI 채팅 기능**과 **다크테마 지원**을 포함한 현대적인 UI/UX를 제공합니다.

## 📱 프로젝트 개요

이 프로젝트는 IT 도서 검색, 북마크, 메모 기능을 제공하는 안드로이드 애플리케이션입니다. **Clean Architecture** 원칙을 따라 계층별로 명확히 분리되어 있으며, **Jetpack Compose**와 **Material Design 3**을 활용한 현대적인 UI를 제공합니다. 

### ✨ 주요 특징
- 🤖 **Gemini AI 채팅**: IT 도서 추천 및 상담 기능
- 🌙 **다크테마 지원**: Material Design 3 기반 라이트/다크테마
- 📱 **현대적 UI**: Jetpack Compose 기반 반응형 디자인
- 📝 **마크다운 렌더링**: ChatGPT 스타일의 메시지 표시
- ⚡ **병렬 처리**: AI 요약 생성 최적화
- 🎨 **Preview 지원**: Compose UI 실시간 미리보기

## 🏗️ 아키텍처

### Clean Architecture 적용

이 프로젝트는 Clean Architecture의 3계층 구조를 따릅니다:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (Activities, Fragments, ViewModels)│
│  (Jetpack Compose UI Components)    │
├─────────────────────────────────────┤
│            Domain Layer             │
│     (Use Cases, Entities, Models)   │
├─────────────────────────────────────┤
│             Data Layer              │
│  (Repositories, Data Sources, APIs) │
│  (Gemini AI Service, Firebase AI)   │
└─────────────────────────────────────┘
```

### 계층별 구조

#### 1. Presentation Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/screen/`
- **구성요소**:
  - `BaseActivity`, `BaseFragment`, `BaseViewModel`: 기본 UI 컴포넌트
  - `MainActivity`: 메인 화면 관리 (BottomNavigationView)
  - `BookDetailActivity`: 도서 상세 정보 화면
  - 각 탭별 Fragment들 (New, Bookmark, Search, **Chat**)
  - **ChatTabFragment**: Jetpack Compose 기반 AI 채팅 화면
  - ViewModels: UI 상태 관리

#### 2. Data Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/data/`
- **구성요소**:
  - **Repository**: `BookSearchRepository`, `BookStoreRepository`, `BookMemoRepository`
  - **API**: `BooksApiService` - 외부 API 통신
  - **AI Service**: `GeminiService` - Firebase AI Logic SDK 기반 Gemini API
  - **Database**: Room을 사용한 로컬 데이터베이스
  - **Entity**: 데이터베이스 엔티티들
  - **Response**: API 응답 모델들

#### 3. Domain Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/model/`
- **구성요소**:
  - `BookModel`: 도서 정보 모델
  - `SearchHistoryModel`: 검색 기록 모델
  - `CellType`: UI 셀 타입 정의

## 기술 스택

### 주요 라이브러리
- **의존성 주입**: Hilt
- **네트워킹**: Retrofit + OkHttp
- **데이터베이스**: Room
- **비동기 처리**: Kotlin Coroutines + Flow
- **이미지 로딩**: Glide, **Coil (Compose)**
- **JSON 파싱**: Gson
- **UI**: ViewBinding, **Jetpack Compose**
- **AI 서비스**: **Firebase AI Logic SDK (Gemini)**
- **마크다운**: **compose-markdown**``
- **테마**: **Material Design 3**

### 개발 도구
- **언어**: Kotlin
- **최소 SDK**: API 24 (Android 7.0)
- **타겟 SDK**: API 34 (Android 14)
- **빌드 도구**: Gradle
- **Compose Compiler**: Kotlin 2.2.0

## 📁 프로젝트 구조

```
app/src/main/java/com/soda1127/itbookstorecleanarchitecture/
├── data/                           # 데이터 계층
│   ├── db/                        # 데이터베이스 관련
│   │   ├── BookStoreDatabase.kt   # Room 데이터베이스
│   │   └── dao/                   # Data Access Objects
│   ├── di/                        # 의존성 주입 모듈
│   │   ├── ApiModule.kt           # API 관련 의존성
│   │   ├── DatabaseModule.kt      # 데이터베이스 의존성
│   │   └── RepositoryModule.kt    # Repository 의존성
│   ├── entity/                    # 데이터베이스 엔티티
│   ├── repository/                # Repository 구현체
│   ├── remote/                    # 원격 데이터 소스
│   │   └── GeminiService.kt       # Gemini AI 서비스
│   └── response/                  # API 응답 모델
├── extensions/                     # Kotlin 확장 함수
├── model/                         # 도메인 모델
├── screen/                        # UI 계층
│   ├── base/                      # 기본 UI 컴포넌트
│   ├── detail/                    # 도서 상세 화면
│   └── main/                      # 메인 화면
│       ├── chat/                  # AI 채팅 화면 (Compose)
│       ├── search/                # 검색 화면
│       ├── bookmark/              # 북마크 화면
│       └── newtab/                # 신간 도서 화면
├── url/                           # URL 관리
└── widget/                        # 커스텀 위젯
    ├── adapter/                   # RecyclerView 어댑터
    └── viewholder/                # ViewHolder들
```

## 🎨 UI/UX 특징

### Material Design 3 적용
- **다크테마 지원**: 시스템 설정에 따른 자동 테마 전환
- **색상 시스템**: Material Design 3 색상 팔레트 적용
- **타이포그래피**: 일관된 폰트 스타일
- **모션**: 자연스러운 애니메이션과 전환 효과

### Jetpack Compose 활용
- **ChatTabFragment**: 완전한 Compose 기반 채팅 UI
- **반응형 레이아웃**: 다양한 화면 크기 지원
- **Preview 지원**: 실시간 UI 미리보기
- **상태 관리**: StateFlow 기반 반응형 상태 관리

## 🤖 Gemini AI 채팅 기능

### 주요 기능
- **IT 도서 추천**: 자연어로 도서 추천 요청
- **키워드 추출**: AI가 사용자 메시지에서 검색 키워드 자동 추출
- **책 요약 생성**: 각 추천 도서에 대한 AI 생성 요약
- **병렬 처리**: 여러 책의 요약을 동시에 생성하여 성능 최적화
- **마크다운 렌더링**: ChatGPT 스타일의 메시지 표시

### 사용 예시
```
사용자: "안드로이드 개발 관련 책을 추천해주세요"

AI 응답:
🔍 **'android' 관련 추천 도서**

📚 **Learn Android Studio 3 with Kotlin**
이 책은 Android 개발을 처음 시작하는 개발자들에게 적합합니다...

📚 **Android Programming: The Big Nerd Ranch Guide**
Android 개발의 핵심 개념과 실무 적용 방법을 체계적으로 학습할 수 있는 책입니다...

[시각적 책 목록]
```

### 기술적 구현
- **Firebase AI Logic SDK**: Gemini 2.5 Flash 모델 사용
- **병렬 처리**: `coroutineScope` + `async` + `awaitAll()` 활용
- **상태 관리**: StateFlow 기반 채팅 상태 관리
- **에러 처리**: 개별 요약 실패 시에도 다른 책들 정상 처리

## 🧪 테스트 코드

### 테스트 구조

프로젝트는 JUnit 5를 사용하여 체계적인 테스트 코드를 작성하고 있습니다.

#### 테스트 디렉토리 구조
```
app/src/test/java/com/soda1127/itbookstorecleanarchitecture/
├── testbase/                      # 테스트 기본 클래스
│   ├── JUnit5Test.kt             # JUnit 5 테스트 베이스
│   └── InstantExecutorExtension.kt # 쓰레드 초기화 및 설정
├── screen/                        # UI State 테스트
│   ├── main/                      # 메인 화면 테스트
│   └── detail/                    # 상세 화면 테스트
└── data/                          # 데이터 계층 테스트
    ├── json/                      # JSON 테스트 데이터
    └── repository/                # Repository 테스트
```

#### 테스트 특징

1. **JUnit 5 사용**: 최신 JUnit 5를 사용하여 테스트 작성
2. **MockK**: Mocking 라이브러리로 MockK 사용
3. **Coroutines 테스트**: `UnconfinedTestDispatcher`를 사용한 코루틴 테스트
4. **Flow 테스트**: `flow-test-observer`를 사용한 Flow 테스트
5. **Hilt 테스트**: Hilt 컴포넌트 테스트 지원

#### 테스트 예시

```kotlin
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
internal class MainViewModelTest: JUnit5Test() {

    private lateinit var sut: MainViewModel

    @BeforeEach
    override fun setup() {
        super.setup()
        sut = MainViewModel()
    }

    @Test
    fun `Test main tab navigation changed`() = runTest(UnconfinedTestDispatcher()) {
        val first = MainNavigation(R.id.menu_new)
        val second = MainNavigation(R.id.menu_bookmark)
        sut.navigationItemStateFlow.test(this) {
            assertValues(
                null,
                first,
                second
            )
        }
        sut.changeNavigation(first)
        sut.changeNavigation(second)
    }
}
```

### 테스트 실행 방법

```bash
# 단위 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests MainViewModelTest

# 커버리지 리포트 생성
./gradlew testDebugUnitTestCoverage
```

## 🔧 주요 기능

### 1. 도서 검색
- IT 도서 검색 기능
- 검색 기록 저장 및 관리
- 실시간 검색 결과 표시
- **AI 기반 키워드 추천**

### 2. 북마크 관리
- 관심 도서 북마크 추가/제거
- 북마크 목록 조회
- 북마크 상태 동기화

### 3. 도서 상세 정보
- 도서 상세 정보 표시
- 도서 메모 작성 및 관리
- PDF 다운로드 링크 제공

### 4. 검색 기록
- 최근 검색어 저장
- 검색 기록 삭제
- 검색 기록 기반 빠른 검색

### 5. 🤖 AI 채팅 (NEW!)
- **자연어 도서 추천**: "안드로이드 개발 책 추천해줘"
- **키워드 자동 추출**: AI가 메시지에서 검색 키워드 추출
- **책 요약 생성**: 각 추천 도서에 대한 AI 생성 요약
- **시각적 책 목록**: 클릭 가능한 책 카드 표시
- **마크다운 렌더링**: ChatGPT 스타일 메시지 표시
- **시간 표시**: 각 메시지의 전송 시간 표시

### 6. 🌙 다크테마 지원 (NEW!)
- **자동 테마 전환**: 시스템 설정에 따른 자동 전환
- **Material Design 3**: 최신 디자인 시스템 적용
- **일관된 색상**: 모든 UI 요소의 테마 색상 적용
- **가독성 최적화**: 다크테마에서도 최적의 가독성

## 🚀 빌드 및 실행

### 환경 설정
1. Android Studio 최신 버전 설치
2. JDK 21 설치
3. Android SDK API 24 이상 설치
4. **Firebase 프로젝트 설정** (Gemini AI 기능 사용 시)

### Firebase 설정 (Gemini AI 기능)
1. Firebase Console에서 새 프로젝트 생성
2. Android 앱 등록
3. `google-services.json` 파일 다운로드 후 `app/` 디렉토리에 배치
4. Firebase AI Logic SDK 활성화

### 빌드 방법
```bash
# 프로젝트 클론
git clone [repository-url]
cd ItBookStoreCleanArchitecture

# 의존성 다운로드
./gradlew build

# 앱 실행
./gradlew installDebug
```

### 개발 환경 설정
```bash
# 개발용 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# Compose Preview 확인
# Android Studio에서 Design 패널 열기
```

## 📋 개발 가이드라인

### 코드 스타일
- Kotlin 코딩 컨벤션 준수
- Clean Architecture 원칙 적용
- 의존성 주입 패턴 사용
- 반응형 프로그래밍 (Flow) 활용
- **Jetpack Compose 베스트 프랙티스 적용**

### 아키텍처 원칙
1. **의존성 역전**: 고수준 모듈이 저수준 모듈에 의존하지 않음
2. **단일 책임**: 각 클래스는 하나의 책임만 가짐
3. **개방-폐쇄**: 확장에는 열려있고 수정에는 닫혀있음
4. **테스트 가능성**: 모든 비즈니스 로직은 테스트 가능
5. **성능 최적화**: 병렬 처리 및 비동기 작업 활용

### Compose 개발 가이드라인
- **Preview 활용**: 모든 Composable에 Preview 작성
- **상태 호이스팅**: 상태를 적절한 레벨에서 관리
- **재사용성**: 작은 단위의 Composable로 분리
- **테마 활용**: MaterialTheme 색상 시스템 활용

## 🎯 성능 최적화

### 병렬 처리
- **책 요약 생성**: `coroutineScope` + `async` + `awaitAll()` 활용
- **이미지 로딩**: Coil을 사용한 효율적인 이미지 캐싱
- **네트워크 요청**: Retrofit + OkHttp 최적화

### 메모리 관리
- **이미지 캐싱**: Glide/Coil을 통한 메모리 효율적 이미지 관리
- **데이터베이스**: Room을 통한 효율적인 로컬 데이터 관리
- **코루틴**: 적절한 스코프와 취소 처리

## 🤝 기여 방법

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 기여 가이드라인
- **코드 스타일**: Kotlin 코딩 컨벤션 준수
- **테스트**: 새로운 기능에 대한 테스트 코드 작성
- **문서화**: README 및 코드 주석 업데이트
- **Preview**: Compose UI 변경 시 Preview 추가

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 🙏 감사의 말

- **Firebase AI Logic SDK**: Gemini AI 기능 제공
- **Jetpack Compose**: 현대적인 UI 개발 도구
- **Material Design 3**: 디자인 시스템 가이드라인
- **Clean Architecture**: 아키텍처 패턴 가이드

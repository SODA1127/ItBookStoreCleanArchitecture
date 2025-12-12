[🇺🇸 English](README_en.md)

# IT BookStore Clean Architecture

IT 도서 정보를 제공하는 안드로이드 애플리케이션으로, Clean Architecture 패턴을 적용하여 개발되었습니다.

## 📱 프로젝트 개요

이 프로젝트는 IT 도서 검색, 북마크, 메모 기능을 제공하는 안드로이드 애플리케이션입니다. Clean Architecture 원칙을 따라 계층별로 명확히 분리되어 있으며, 테스트 가능하고 유지보수가 용이한 구조로 설계되었습니다.

## 🏗️ 아키텍처

### Clean Architecture 적용

``
이 프로젝트는 Clean Ar``chitecture의 3계층 구조를 따릅니다:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (Activities, Fragments, ViewModels)│
├─────────────────────────────────────┤
│            Domain Layer             │
│     (Use Cases, Entities, Models)   │
├─────────────────────────────────────┤``
│             Data Layer          ``    │
│  (Repositories, Data Sources, APIs) │
└─────────────────────────────────────┘
```

### 계층별 구조

#### 1. Presentation Layer

- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/screen/` & `navigation/`
- **구성요소**:
  - **Jetpack Compose**: 모든 UI는 Compose로 구현되어 있습니다.
  - **Single Activity**: `MainActivity`가 유일한 Activity이며, 모든 화면 전환은 Navigation Compose로 처리됩니다.
  - **Navigation**: `MainNavHost`를 중심으로 `BookNewGraph`, `BookSearchGraph`, `BookmarkGraph`, `BookDetailGraph`가 구성되어 있습니다.
  - **Screens**: `BookNewScreen`, `BookmarkScreen`, `SearchScreen`, `BookDetailScreen` (모두 Composable)
  - `BaseViewModel`: `StateFlow`를 기반으로 한 MVI 패턴의 상태 관리 공통 클래스

#### 2. Data Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/data/`
- **구성요소**:
  - **Repository**: `BookSearchRepository`, `BookStoreRepository`, `BookMemoRepository` - 데이터 소스 통합 및 비즈니스 로직 처리
  - **API**: `BooksApiService` - IT Bookstore API 통신
  - **Database**: Room을 사용한 로컬 캐싱 및 북마크 저장소
  - **Entity**: 로컬 DB 엔티티 (`BookEntity` 등)
  - **Response**: API 응답 DTO

#### 3. Domain Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/model/`
- **구성요소**:
  - `BookModel`: UI에서 사용하는 도서 데이터 모델 (좋아요 상태 포함)
  - `SearchHistoryModel`: 검색 기록 모델
  - `CellType`: UI 셀 타입 정의

## 기술 스택

### 주요 라이브러리

- **UI**: Jetpack Compose (Material3)
- **아키텍처**: Clean Architecture + MVVM + MVI
- **의존성 주입**: Hilt
- **네트워킹**: Retrofit + OkHttp
- **데이터베이스**: Room
- **비동기 처리**: Kotlin Coroutines + Flow + StateFlow
- **이미지 로딩**: Glide (Compose Integration)

### 개발 도구
- **언어**: Kotlin
- **최소 SDK**: API 24 (Android 7.0)
- **타겟 SDK**: API 34 (Android 14)
- **빌드 도구**: Gradle

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
│   └── response/                  # API 응답 모델
├── extensions/                     # Kotlin 확장 함수
├── model/                         # 도메인 모델
├── navigation/                    # Navigation Compose 그래프 및 라우트
├── screen/                        # UI 계층

│   ├── base/                      # 기본 UI 컴포넌트
│   ├── detail/                    # 도서 상세 화면 (Composable)
│   └── main/                      # 메인 화면

├── url/                           # URL 관리
└── widget/                        # 공통 UI 위젯
    └── item/                      # 재사용 가능한 Composable 아이템 (`BookItem` 등)
```

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
internal class BookNewTabViewModelTest : JUnit5Test() {

  private lateinit var sut: BookNewTabViewModel

  @MockK
  private lateinit var bookStoreRepository: BookStoreRepository

    @BeforeEach
    override fun setup() {
        super.setup()
      // Mocking required for BaseViewModel
      every { bookStoreRepository.observeBookmarkStatus() } returns flow {}
      sut = BookNewTabViewModel(bookStoreRepository)
    }

    @Test
    fun `fetch Book List succeed`() = runTest(UnconfinedTestDispatcher()) {
      // Given
      val books = listOf(BookModel(...))
      coEvery { bookStoreRepository.getNewBooks() } returns flowOf(books)

      // Then
      sut.stateFlow.test(TestScope()) {
            assertValues(
              NewTabState.Uninitialized,
              NewTabState.Loading,
              NewTabState.Success(books)
            )
        }

      // When
      sut.fetchData()
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

### 2. 북마크 관리
- 관심 도서 북마크 추가/제거
- 북마크 목록 조회
- 북마크 상태 동기화

### 3. 도서 상세 정보
- 도서 상세 정보 표시
- 도서 메모 작성 및 관리
- PDF 다운로드 링크 제공
- **좋아요(Like) 기능**: 하트 아이콘을 통해 관심 도서 등록/해제

### 4. 실시간 상태 동기화 (Like Feature)

- `NewBooks`, `Search`, `Bookmark`, `Detail` 전 화면에서 좋아요 상태가 실시간으로 동기화됩니다.
- `combine`을 활용하여 도서 리스트 데이터와 로컬 북마크 데이터를 결합하여 최신 상태를 유지합니다.
- `observeBookmarkStatus`를 통해 UI가 즉각적으로 반응합니다.

### 5. 검색 기록
- 최근 검색어 저장
- 검색 기록 삭제
- 검색 기록 기반 빠른 검색

## 🚀 빌드 및 실행

### 환경 설정
1. Android Studio 최신 버전 설치
2. JDK 21 설치
3. Android SDK API 24 이상 설치

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
```

## 📋 개발 가이드라인

### 코드 스타일
- Kotlin 코딩 컨벤션 준수
- Clean Architecture 원칙 적용
- 의존성 주입 패턴 사용
- 반응형 프로그래밍 (Flow) 활용

### 아키텍처 원칙
1. **의존성 역전**: 고수준 모듈이 저수준 모듈에 의존하지 않음
2. **단일 책임**: 각 클래스는 하나의 책임만 가짐
3. **개방-폐쇄**: 확장에는 열려있고 수정에는 닫혀있음
4. **테스트 가능성**: 모든 비즈니스 로직은 테스트 가능

## 🤝 기여 방법

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

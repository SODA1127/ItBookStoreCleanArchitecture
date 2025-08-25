# IT BookStore Clean Architecture

IT 도서 정보를 제공하는 안드로이드 애플리케이션으로, Clean Architecture 패턴을 적용하여 개발되었습니다.

## 📱 프로젝트 개요

이 프로젝트는 IT 도서 검색, 북마크, 메모 기능을 제공하는 안드로이드 애플리케이션입니다. Clean Architecture 원칙을 따라 계층별로 명확히 분리되어 있으며, 테스트 가능하고 유지보수가 용이한 구조로 설계되었습니다.

## 🏗️ 아키텍처

### Clean Architecture 적용

이 프로젝트는 Clean Architecture의 3계층 구조를 따릅니다:

```
┌─────────────────────────────────────┐
│           Presentation Layer        │
│  (Activities, Fragments, ViewModels)│
├─────────────────────────────────────┤
│            Domain Layer             │
│     (Use Cases, Entities, Models)   │
├─────────────────────────────────────┤
│             Data Layer              │
│  (Repositories, Data Sources, APIs) │
└─────────────────────────────────────┘
```

### 계층별 구조

#### 1. Presentation Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/screen/`
- **구성요소**:
  - `BaseActivity`, `BaseFragment`, `BaseViewModel`: 기본 UI 컴포넌트
  - `MainActivity`: 메인 화면 관리
  - `BookDetailActivity`: 도서 상세 정보 화면
  - 각 탭별 Fragment들 (New, Bookmark, Search)
  - ViewModels: UI 상태 관리

#### 2. Data Layer
- **위치**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/data/`
- **구성요소**:
  - **Repository**: `BookSearchRepository`, `BookStoreRepository`, `BookMemoRepository`
  - **API**: `BooksApiService` - 외부 API 통신
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
- **이미지 로딩**: Glide
- **JSON 파싱**: Gson
- **UI**: ViewBinding

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
├── screen/                        # UI 계층
│   ├── base/                      # 기본 UI 컴포넌트
│   ├── detail/                    # 도서 상세 화면
│   └── main/                      # 메인 화면
├── url/                           # URL 관리
└── widget/                        # 커스텀 위젯
    ├── adapter/                   # RecyclerView 어댑터
    └── viewholder/                # ViewHolder들
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

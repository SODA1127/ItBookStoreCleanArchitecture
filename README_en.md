[🇰🇷 Korean](README.md)

# IT BookStore Clean Architecture

An Android application that provides IT book information, developed using the Clean Architecture pattern.

## 📱 Project Overview

This project is an Android application that provides IT book search, bookmarking, and memo functions. It is designed with a structure that
is easy to test and maintain by clearly separating layers according to Clean Architecture principles.

## 🏗️ Architecture

### Clean Architecture Application

This project follows the 3-layer structure of Clean Architecture:

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

### Layer Structure

#### 1. Presentation Layer

- **Location**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/screen/` & `navigation/`
- **Components**:
    - **Jetpack Compose**: All UI is implemented with Compose.
    - **Single Activity**: `MainActivity` is the only Activity, and all screen transitions are handled by Navigation Compose.
    - **Navigation**: `MainNavHost` centers around `BookNewGraph`, `BookSearchGraph`, `BookmarkGraph`, and `BookDetailGraph`.
    - **Screens**: `BookNewScreen`, `BookmarkScreen`, `SearchScreen`, `BookDetailScreen` (All Composable)
    - `BaseViewModel`: Common class for state management based on MVI pattern using `StateFlow`

#### 2. Data Layer

- **Location**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/data/`
- **Components**:
    - **Repository**: `BookSearchRepository`, `BookStoreRepository`, `BookMemoRepository` - Integration of data sources and processing of
      business logic
    - **API**: `BooksApiService` - IT Bookstore API communication
    - **Database**: Local caching using Room and bookmark storage
    - **Entity**: Local DB entities (`BookEntity`, etc.)
    - **Response**: API response DTOs

#### 3. Domain Layer

- **Location**: `app/src/main/java/com/soda1127/itbookstorecleanarchitecture/model/`
- **Components**:
    - `BookModel`: Book data model used in UI (includes like status)
    - `SearchHistoryModel`: Search history model
    - `CellType`: UI cell type definition

## Tech Stack

### Main Libraries

- **UI**: Jetpack Compose (Material3)
- **Architecture**: Clean Architecture + MVVM + MVI
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Database**: Room
- **Async Processing**: Kotlin Coroutines + Flow + StateFlow
- **Image Loading**: Glide (Compose Integration)

### Development Tools

- **Language**: Kotlin
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Build Tool**: Gradle

## 📁 Project Structure

```
app/src/main/java/com/soda1127/itbookstorecleanarchitecture/
├── data/                           # Data Layer
│   ├── db/                        # Database related
│   │   ├── BookStoreDatabase.kt   # Room Database
│   │   └── dao/                   # Data Access Objects
│   ├── di/                        # Dependency Injection Modules
│   │   ├── ApiModule.kt           # API related dependencies
│   │   ├── DatabaseModule.kt      # Database dependencies
│   │   └── RepositoryModule.kt    # Repository dependencies
│   ├── entity/                    # Database Entities
│   ├── repository/                # Repository Implementation
│   └── response/                  # API Response Models
├── extensions/                     # Kotlin Extension Functions
├── model/                         # Domain Models
├── navigation/                    # Navigation Compose Graph & Routes
├── screen/                        # UI Layer
│   ├── base/                      # Base UI Components
│   ├── detail/                    # Book Detail Screen (Composable)
│   └── main/                      # Main Screen
├── url/                           # URL Management
└── widget/                        # Common UI Widgets
    └── item/                      # Reusable Composable Items (e.g., `BookItem`)
```

## 🧪 Test Code

### Test Structure

The project writes systematic test codes using JUnit 5.

#### Test Directory Structure

```
app/src/test/java/com/soda1127/itbookstorecleanarchitecture/
├── testbase/                      # Test Base Classes
│   ├── JUnit5Test.kt             # JUnit 5 Test Base
│   └── InstantExecutorExtension.kt # Thread Initialization & Setup
├── screen/                        # UI State Tests
│   ├── main/                      # Main Screen Tests
│   └── detail/                    # Detail Screen Tests
└── data/                          # Data Layer Tests
    ├── json/                      # JSON Test Data
    └── repository/                # Repository Tests
```

#### Test Features

1. **Using JUnit 5**: Writing tests using the latest JUnit 5
2. **MockK**: Using MockK as a mocking library
3. **Coroutines Testing**: Coroutines testing using `UnconfinedTestDispatcher`
4. **Flow Testing**: Flow testing using `flow-test-observer`
5. **Hilt Testing**: Hilt component test support

#### Test Example

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

### How to Run Tests

```bash
# Run Unit Tests
./gradlew test

# Run Specific Test Class
./gradlew test --tests MainViewModelTest

# Generate Coverage Report
./gradlew testDebugUnitTestCoverage
```

## 🔧 Key Features

### 1. Book Search

- IT book search function
- Save and manage search history
- Real-time search result display

### 2. Bookmark Management

- Add/Remove bookmarks for interested books
- View bookmark list
- Bookmark status synchronization

### 3. Book Detail Info

- Display book detail information
- Write and manage book memos
- Provide PDF download link
- **Like Feature**: Register/Unregister interested books via heart icon

### 4. Real-time Status Synchronization (Like Feature)

- Like status is synchronized in real-time across `NewBooks`, `Search`, `Bookmark`, and `Detail` screens.
- Combines book list data and local bookmark data using `combine` to maintain the latest status.
- UI reacts immediately via `observeBookmarkStatus`.

### 5. Search History

- Save recent search terms
- Delete search history
- Quick search based on search history

## 🚀 Build & Run

### Environment Setup

1. Install latest version of Android Studio
2. Install JDK 21
3. Install Android SDK API 24 or higher

### How to Build

```bash
# Clone Project
git clone [repository-url]
cd ItBookStoreCleanArchitecture

# Download Dependencies
./gradlew build

# Run App
./gradlew installDebug
```

### Development Environment Setup

```bash
# Debug Build
./gradlew assembleDebug

# Release Build
./gradlew assembleRelease
```

## 📋 Development Guidelines

### Code Style

- Follow Kotlin Coding Conventions
- Apply Clean Architecture Principles
- Use Dependency Injection Pattern
- Utilize Reactive Programming (Flow)

### Architecture Principles

1. **Dependency Inversion**: High-level modules do not depend on low-level modules
2. **Single Responsibility**: Each class has only one responsibility
3. **Open-Closed**: Open for extension, closed for modification
4. **Testability**: All business logic is testable

## 🤝 How to Contribute

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is distributed under the MIT License. See `LICENSE` file for details.

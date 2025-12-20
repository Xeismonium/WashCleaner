# WashCleaner - Laundry Management System

WashCleaner is a modern Android application designed to streamline the operations of a laundry business. Built with the latest Android technologies, it provides a comprehensive solution for managing transactions, services, customers, and financial reports.

## 📱 Features

*   **Dashboard**: Real-time overview of business performance, including today's transactions, processing status, and revenue stats.
*   **Transaction Management**: 
    *   Create, update, and track laundry transactions.
    *   Support for multiple services per transaction.
    *   Status tracking (New, Processing, Ready, Completed).
    *   Payment status tracking.
*   **Service Management**: Maintain a catalog of laundry services with customizable pricing and units (kg, pcs, etc.).
*   **Customer Management**: Store customer details for quick access during transaction creation.
*   **Reports & Analytics**:
    *   Visual charts for revenue trends (Daily, Weekly, Monthly).
    *   Detailed statistics on service popularity and top customers.
    *   Export reports to CSV format.
*   **Settings**:
    *   Dark/Light mode support.
    *   Data Backup & Restore (JSON/Database).

## 🛠 Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
*   **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
*   **Local Database**: [Room](https://developer.android.com/training/data-storage/room)
*   **Preferences**: [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
*   **Asynchronous Programming**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Navigation**: [Compose Navigation](https://developer.android.com/guide/navigation/navigation-compose)

## 📂 Project Structure

```
com.xeismonium.washcleaner
├── data                # Data layer (Room entities, DAOs, Repositories, DataStore)
├── di                  # Dependency Injection modules (Hilt)
├── ui                  # UI layer (Screens, Components, ViewModels, Theme)
│   ├── components      # Reusable UI components
│   ├── screen          # Feature-specific screens (Dashboard, Transaction, etc.)
│   └── theme           # App theme definitions
└── util                # Utility classes (Currency formatting, etc.)
```

## 🚀 Getting Started

### Prerequisites

*   Android Studio Ladybug | 2024.2.1 or newer.
*   JDK 17 or higher.
*   Android SDK API Level 24 (Min) to 35 (Target).

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/Xeismonium4/WashCleaner.git
    ```

2.  **Open in Android Studio**
    *   Launch Android Studio.
    *   Select "Open" and navigate to the cloned directory.

3.  **Build the project**
    *   Let Gradle sync the dependencies.
    *   Run the app on an emulator or physical device.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1.  Fork the project
2.  Create your feature branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

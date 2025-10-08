# Essence Togo 🛣️⛽

A modern Android mobile application for locating gas stations in Togo, built with Jetpack Compose and Firebase.

## 📖 Description

Essence Togo is a mobile application designed specifically for drivers in Togo who need to quickly find nearby gas stations. The app combines real-time geolocation with Firebase cloud database to provide accurate, up-to-date information about fuel stations across the country.

Built with modern Android development technologies including Jetpack Compose for a smooth, reactive user interface and Firebase Realtime Database for instant data synchronization, the app offers an intuitive experience that helps users locate stations, calculate distances from their current position, search by name or address, and maintain a history of visited stations.

Whether you're traveling in an unfamiliar area or simply looking for the closest station, Essence Togo provides a reliable solution with its clean Material Design 3 interface and efficient geolocation features. The app automatically sorts stations by proximity, updates distances in real-time as you move, and integrates seamlessly with Google Maps for turn-by-turn navigation to your selected station.

## 📱 Features

- **🗺️ Station Locator**: Find gas stations closest to your current position
- **📍 Geolocation**: Automatic distance calculation from your current location
- **🔍 Advanced Search**: Search by station name or address
- **📋 History**: View your recently visited stations
- **🎨 Modern Interface**: Modern design with Material Design 3
- **⚡ Real-time**: Data synchronized in real-time with Firebase

## 🏗️ Architecture

The application follows an MVVM (Model-View-ViewModel) architecture with the following components:

### 📂 Project Structure

```
app/src/main/java/com/example/essence_togo/
├── data/
│   ├── local/
│   │   └── PreferencesManager.kt          # Local preferences management
│   ├── model/
│   │   └── Station.kt                     # Station data model
│   └── repository/
│       └── StationRepository.kt           # Data access layer
├── presentation/
│   └── ui/
│       ├── navigation/                    # App navigation
│       └── theme/                         # Material Design theme
├── utils/
│   └── LocationManager.kt                 # Geolocation manager
└── MainActivity.kt                        # Main activity
```

### 🛠️ Technologies Used

- **Kotlin** - Main programming language
- **Jetpack Compose** - Modern, declarative user interface
- **Firebase Realtime Database** - Real-time database
- **Material Design 3** - Google's modern design system
- **Navigation Compose** - Navigation between screens
- **Coroutines & Flow** - Asynchronous and reactive programming
- **Location Services** - Android geolocation services

## 📷 Screenshots

### Home Screen
![Home Screen](screenshots/accueil1.png)
*Main view with list of nearby stations*

### Station Search
![Search](screenshots/filter1.png)
*Search functionality by name or address*

### Station Details
![Station Details](screenshots/detail1.png)
*Detailed station information with navigation*

### History
![History](screenshots/history1.png)
*List of recently visited stations*

## 🚀 Installation and Setup

### Prerequisites
- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 8 or higher
- Android SDK 21+ (Android 5.0)
- Firebase account with configured project

### 1. Clone the Project
```bash
git clone https://github.com/Rahim10020/essence-togo.git
cd essence-togo
```

### 2. Firebase Configuration
1. Create a new project on [Firebase Console](https://console.firebase.google.com/)
2. Enable **Realtime Database**
3. Download the `google-services.json` file
4. Place it in the `app/` folder

### 3. Firebase Database Structure
```json
{
  "stations": {
    "station_1": {
      "id": 1,
      "nom": "Total Station Centre",
      "address": "Avenue du 24 Janvier, Lomé",
      "latitude": 6.1319,
      "longitude": 1.2228,
      "imageUrl": "https://example.com/image.jpg"
    },
    "station_2": {
      "id": 2,
      "nom": "Shell Station Tokoin",
      "address": "Quartier Tokoin, Lomé",
      "latitude": 6.1375,
      "longitude": 1.2123,
      "imageUrl": "https://example.com/image2.jpg"
    }
  }
}
```

### 4. Permissions
The application requires the following permissions (already configured in the manifest):
- `ACCESS_FINE_LOCATION` - Precise location
- `ACCESS_COARSE_LOCATION` - Approximate location
- `INTERNET` - Internet connection
- `ACCESS_NETWORK_STATE` - Network state

## 🔧 Usage

1. **First Launch**: The app requests location permissions
2. **Navigation**: Use the bottom navigation bar to navigate
3. **Search**: Type in the search bar to filter stations
4. **Details**: Touch a station to see its details and navigate
5. **Navigation**: Touch the navigation button to open Google Maps

## 📊 Technical Features

### Distance Calculation
The application uses the Haversine formula to calculate the distance between your position and the stations:

```kotlin
fun calculateDistance(userLat: Double, userLong: Double): Double {
    val earthRadius = 6371.0 // Earth's radius in km
    // ... Haversine calculation
}
```

### State Management
- **StateFlow** for reactive data
- **SharedPreferences** for local persistence
- **Firebase Realtime Database** for real-time data

### Performance
- Caching of visited stations
- History limited to maximum 50 stations
- Optimized distance calculation

## 🗺️ Future Enhancements
- [ ] Fuel price comparison
- [ ] Station reviews and ratings
- [ ] Favorite stations
- [ ] Route planning with multiple stops
- [ ] Push notifications for nearby stations
- [ ] Payment integration
- [ ] Loyalty program integration
- [ ] Offline maps
- [ ] Multi-language support (French, English, Ewe)
- [ ] Station amenities filtering (restrooms, car wash, etc.)

---

Made with ❤️ for the Togolese community

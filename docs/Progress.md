# Fortschritt

## Meilenstein 1 - bis 29.04.2022
- [ ] Multiplatform Compose ready
- [ ] Ktor Client + Server aufbauen
- [ ] Ui/Ux Design
    - [ ] LoFi wurde erstellt
    - [ ] HiFi wurde erstellt
    - [ ] Usertest wurden durchgeführt

### Multiplatform Compose ready
**Ziel:** Compose Multiplatform ist auf allen Endgeräten **(Desktop, Web, Android, iOS)** verfügbar mit einer einfachen **Hello World** Anwendung.

#### 04.04.2022
Es wurde angefangen, die Projektstruktur aufzubauen. Dabei ist ein Hauptprojekt (`budget-binder`) mit zwei Unterprojekten (`budget-binder-common` und `budget-binder-multiplatform-app`) entstanden. Die Anwendung laufen aktuell, breits schon mit Compose, auf **Desktop** und **Android**.

| Desktop                                            | Android                                            |
|----------------------------------------------------|----------------------------------------------------|
| ![Desktop](img/progress/desktop_01_helloworld.png) | ![Android](img/progress/android_01_helloworld.png) |

#### 05.04.2022
Die Webanwendung wurde hinzugefügt und mit Compose ein anderes **"Hello World"** Design eingeführt. Zudem wurden Platform Abhängige eigenschaften (wie z.B. der Name der Platform) mittels der Logik `expect` und `actual` eingeführt.

**budget-binder-common/src/commonMain/...**
```kotlin
expect class Platform() {
    val platform: String
}
```

**budget-binder-common/src/androidMain/...**
```kotlin
actual class Platform actual constructor() {
    actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}
```

| Desktop                                            | Android                                            | Web                                        |
|----------------------------------------------------|----------------------------------------------------|--------------------------------------------|
| ![Desktop](img/progress/desktop_02_helloworld.png) | ![Android](img/progress/android_02_helloworld.png) | ![Web](img/progress/web_02_helloworld.png) |

#### 06.04.2022
Das Projekt ist verfügbar für die Endgeräte **Desktop, Android, Web**, mit **iOS** bestehen noch Probleme. Es wurde sich mit Compose (theoretisch auch mit `swiftUI` für das Frontend für iOS) genauer auseinandergesetzt und versucht Xcode mit Kotlin Multiplatform zu verbinden. Für Compose für Desktop und Android wurden Animationen erstellt und Bilder in die UI geladen.

| Desktop                                                     | Android                                                     | iOS (nur UI)                                        |
|-------------------------------------------------------------|-------------------------------------------------------------|-----------------------------------------------------|
| ![Desktop](img/progress/desktop_03_animated_helloworld.gif) | ![Android](img/progress/android_03_animated_helloworld.gif) | ![iOS](img/progress/ios_03_animated_helloworld.gif) |

### Ui/Ux Design

#### LoFi
**Ziel:** Der Lofi wurde erstellt

#### HiFi
**Ziel:** Der HiFi wurde erstellt

#### Usertest
**Ziel:** Usertests wurden durchgeführt

## Meileinstein 2 - bis 20.05.2022
- [ ] Datenmodelle definieren
- [ ] Schnittstelle definieren
- [ ] REST API bauen
    - [ ] REST API testen
- [ ] Fontend Design entwicklung starten
- [ ] Dokumentation starten

### Datenmodelle definieren
**Ziel:** 

### Schnittstelle definieren
**Ziel:** 

### REST API bauen
**Ziel:** 

#### REST API Testen
**Ziel:** 

### Frontend Design entwicklung starten
**Ziel:** 

### Dokumentation starten
**Ziel:** 

## Meilenstein 3 - bis 20.06.2022
- [ ] Web Frontend fertigstellen
- [ ] Desktop Frontend fertigstellen
- [ ] Android Frontend fertigstellen
- [ ] iOS Frontend fertigstellen
- [ ] Frontend Usertests
- [ ] Dokumentation fertigstellen

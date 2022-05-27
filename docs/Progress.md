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
- [x] Datenmodelle definieren
- [x] Schnittstelle definieren
- [ ] REST API bauen
    - [ ] REST API testen
- [ ] Fontend Design entwicklung starten
- [ ] Dokumentation starten

### Datenmodelle definieren
**Ziel:** Ein UML-Diagramm mit dem Datenmodell erstellen

### 02.05.
Es wurde in der gesamten Gruppe diskutiert, wie unser Datenmodell aussehen soll.
Zuerst haben wir die grundsätzlichen Felder besprochen. Dann haben wir definiert, dass es wiederholende Einträge gibt. 
Es wurde entschieden, das man in unserer App in die Vergangenheit gucken kann und demnach brauchen wir eine Versionierung von wiederholenden Einträgen und Kategorien, was wir durch eine Parent-Child Beziehung abgebildet haben.
Nach der Besprechung wurde folgendes UML-Diagram abgefertigt.
![](img/progress/db_uml.png)

### Schnittstelle definieren
**Ziel:** 

### 02.05.
Es wurde in der gesamten Gruppe diskutiert, wie unsere Schnittstellen aussehen sollen.
Und wir sind zu folgendem Ergebnis gekommen:
```
# All Query Params are optional
GET /entries?current=true&perdiod="MM-YYYY"
POST /entries

GET /entries/{entryId}
PATCH /entries/{entryId}
DELETE /entries/{entryId}

GET /categories?current=true&perdiod="MM-YYYY"
POST /categories

GET /categories{categoryID}
PATCH /categories{categoryID}
DELETE /categories{categoryID}

GET /categories{categoryID}/entries

GET /me
PATCH /me
DELETE /me

POST /register
POST /login
```

### REST API bauen
**Ziel:** Budget-Binder-Server als RESTApi implementieren, die Datenmodelle verwenden und die Schnittstellen als Endpunkte erstellen.

### 15.04.
Das Serverprojekt wurde angelegt und eine erste Implementation des Ktor-Servers erstellt.
Dann wurde ein Dockerfile erstellt, um den Server so auf jeglichen Linux-Servern starten zu können. 

### 21.04.
Es wurde eine erste Projektstruktur aufgebaut. Zudem wurde Jetbrains/Exposed hinzugefügt und eine erste Version eines Users als Schema definiert.

### 22.04.
Man kann sich nun zwischen einer SQLITE, MYSQL und POSTGRES Datenbank entscheiden, indem man die nötigen ENV-Variablen setzt.
Passwörter werden jetzt mit bcrypt gehasht.
Weiterhin wurde die Möglichkeit geschaffen sich mit Username und Passwort zu authentifizieren, indem die Daten mit der Datenbank verglichen werden 
oder mit JWT (JSON-Web-Tokens), welchen man beim Login erhält. 
Es wurden zwei DTOs (Data-Transfer-Objects) in budget-binder-common definiert, die zwischen Server und Client ausgetauscht werden.
Der Endpunkt zum Einloggen wurde definiert der einen JWT für die Authentifizierung schickt, welcher 15min hält, und ein Refresh-Cookie, um sich beim Endpunkt /refresh_token einen neuen Access-Token zu erhalten.

### 23.04.
Eine erste README für den Server erstellt und einige Zeit damit verbracht mithilfe einer TestWebseite CORS richtig zu definieren.
Zudem wurde die Möglichkeit geschaffen über die Config zu definieren, wie lange die jeweiligen JWT für Access und Refresh gültig sind.
Zum Schluss wurde die token-Version dem User als neues Feld hinzugefügt, um zu überprüfen, ob der aktuelle Token noch gültig ist, den der User mitgeschickt hat.

### 24.04.


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

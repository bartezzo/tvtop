# tvtop

Aplikacja Compose Multiplatform na Androida i iOS.

## Struktura

- `composeApp/` – wspólny kod UI (`commonMain`) oraz kod platformowy (`androidMain`, `iosMain`)
- `iosApp/` – źródła Swift hosta iOS

## Android

```sh
./gradlew :composeApp:assembleDebug
```

## iOS

Wymaga macOS i Xcode. Projekt Xcode (`iosApp.xcodeproj`) nie jest jeszcze dodany –
utwórz go w Xcode (App, SwiftUI, nazwa `iosApp`, w katalogu `iosApp/`), podepnij istniejące
pliki Swift i dodaj fazę Run Script:

```sh
cd "$SRCROOT/.."
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```

## Dystrybucja dla testerów (Firebase App Distribution)

Workflow `.github/workflows/firebase-distribution.yml` po każdym pushu do `main` buduje APK debug
i wysyła go do grupy `testers` w Firebase App Distribution. Wymagane sekrety repozytorium
(Settings → Secrets and variables → Actions):

- `FIREBASE_APP_ID` – ID aplikacji Android z Firebase (Project settings → Your apps, format `1:123…:android:abc…`)
- `FIREBASE_SERVICE_ACCOUNT` – zawartość JSON klucza konta serwisowego z rolą *Firebase App Distribution Admin*

Bez `FIREBASE_APP_ID` krok wysyłki jest pomijany. Grupę `testers` utwórz w Firebase (App Distribution → Testers & Groups).

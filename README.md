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

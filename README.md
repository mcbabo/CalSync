# CalSync

Sync your .ics files from HTTP or local file

Built using **Kotlin** and **Jetpack Compose** — Android's modern toolkit for
building native UIs. This app demonstrates best practices with a clean architecture, modern
libraries, and Compose UI components.

## Features

- Sync .ics files from HTTP or local file
- Material You design with light and dark themes

## Download

- Download the latest APK from the [Releases](https://github.com/mcbabo/CalSync/releases/latest) page.

## Contributing

Contributions are welcome!

## Star History

<a href="https://www.star-history.com/#mcbabo/CalSync&Timeline">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=mcbabo/CalSync&type=Timeline&theme=dark" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=mcbabo/CalSync&type=Timeline" />
   <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=mcbabo/CalSync&type=Timeline" />
 </picture>
</a>

## Project Structure

```
at.mcbabo.calsync/
├── data                # Data layer
│     ├── dao
│     ├── store
│     ├── entities
│     ├── models
│     ├── repositories
│     └── viewmodels
├── di                  # Dagger / Hilt
├── internal            # Internal utilities and helpers
├── navigation          # Navigation components
├── ui                  # UI layer
│     ├── component
│     ├── screen
│     └── theme
├── util                # Utility classes and extensions
├── worker              # WorkManager tasks
└── MainActivity.kt     # Entry point
```

## Author

**mcbabo**  
[GitHub](https://github.com/mcbabo)

## Credits

Huge shoutout to the [Seal](https://github.com/seal) team!

The app is mostly inspired by [Seal](https://github.com/seal) and other Material 3 apps

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
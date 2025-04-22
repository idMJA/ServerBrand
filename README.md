# ServerBrand

A Minecraft plugin that allows you to customize the server brand shown in the F3 debug screen.

## Features

- Customize the server brand text
- Support for Minecraft 1.13 and above
- Easy configuration through config.yml

## Requirements

- Java 17 or higher
- Spigot/Paper 1.13 or higher
- Bukkit/Spigot API

## Installation

1. Download the latest release from the [Releases](https://github.com/idMJA/ServerBrand/releases) page
2. Place the JAR file in your server's `plugins` folder
3. Restart your server

## Configuration

The plugin creates a `config.yml` file in the plugins/ServerBrand folder with the following default configuration:

```yaml
brand: "Your Custom Brand"
```

You can modify this value to change the server brand text.

## Building from Source

1. Clone the repository:

```bash
git clone https://github.com/idMJA/ServerBrand.git
```

2. Build the project using Gradle:

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

The compiled JAR will be available in `build/libs/`

## Contributing

1. Fork the repository
2. Create a new branch for your feature
3. Commit your changes
4. Push to your fork
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please open an issue on the GitHub repository. 
# TrNightmare Ego Auto Clicker

A client-side NeoForge 1.21.1 mod that automatically clicks silver and gold nodes in the Ego Converse minigame from the trnightmare mod.

## Features

- Automatically detects and clicks SILVER and GOLD nodes in the Ego Converse minigame
- Configurable click cooldown (default 100ms)
- Debug overlay showing detected nodes (green = target, red = other)
- Reflection-based - no hard dependencies on trnightmare

## Building

```bash
./gradlew build
```

The output JAR will be in `build/libs/`.

## Installation

1. Build the mod or download the release JAR
2. Place in your `mods/` folder alongside trnightmare
3. Launch with NeoForge 1.21.1

## Configuration

Currently configured via constants in `EgoMinigameAutoClicker.java`:

- `EGO_SCREEN_CLASS_NAME` - The fully qualified class name of the Ego Converse screen
- `NODE_CLASS_NAME` - The node inner class
- `CLICK_COOLDOWN_MS` - Minimum time between clicks

You may need to adjust these based on the actual trnightmare version you're using. Use the debug overlay to verify node detection.

## How it Works

The mod uses reflection to:
1. Detect when the Ego Converse screen is open
2. Access the internal node list
3. Identify nodes with type SILVER or GOLD
4. Simulate clicks on those nodes

## License

MIT
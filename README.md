# AASTU Path Finder

AASTU Path Finder is a Java-based application that allows users to interact with a map, select two points, and calculate the shortest path between them. The application uses OpenStreetMap data for map rendering and provides a graphical user interface for user interaction.

## Table of Contents

1. [Features](#features)
2. [Project Structure](#project-structure)
3. [Dependencies](#dependencies)
4. [Installation](#installation)
5. [Usage](#usage)
6. [How It Works](#how-it-works)
7. [Contributing](#contributing)
8. [License](#license)

---

## Features

- Interactive map using OpenStreetMap tiles.
- Ability to select two points on the map:
  - Right-click to set the start and end points.
- Calculates and displays the shortest path between the selected points.
- Simple and intuitive graphical user interface.

---

## Demo

![Demo](/src/main/resources/Demo.gif)

---

## Project Structure

```bash

lib/
   | javafx-sdk-17/
   |  |   lib/
   |  |   |    javafx.base.jar
   |  |   |    javafx.fxml.jar
   |  |   |    javafx.controls.jar
   |  |   |    javafx.graphics.jar
   |  |   |    javafx.web.jar
src/
    main/
      |  java/
      |  |    com/
      |  |    |   /pathfinder
      |  |    |      /model
      |  |    |    |    Graph.java
      |  |    |    |    Point.java
      |  |    |    |    WightedEdge.java
      |  |    |    |  Main.java
      |  resources
      |  |    MapData.geojson

build.gradle
.gitignore
README.md
```

## Dependencies

The project relies on the following libraries:

- JavaFx
- GluonMaps

---

### Prerequisites

- Java Development Kit (JDK) 17 and above.
- Gradle.

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/BEKI77/Path-Finder.git
   cd Path-Finder
   ```
2. Ensure the required JAR files are present in the lib/ directory:

3. Compile the project:

   - it's preferable if you use gradle for development

   ```bash
   gradle build
   gradle run

   ```

### Usage

    1. Launch the application.
    2. Follow the on-screen instructions:
    3. Right-click on the map to set the start point.
    4. Right-click on the map to set the end point.
    5. The application will calculate and display the shortest path between the two points.

### How It Works

Core Components

1. Graph Representation:

   - The graph is represented using the Graph class, which contains Point (Point objects) and WightedEdges (connections between nodes with weights).

2. Map Interaction:

   - The MapView class renders the map and allows users to interact with it.
   - It listens for mouse events to capture user input and determine the selected points.

3. Shortest Path Calculation:

   - The Graph class calculates the shortest path between the start and end nodes using the graph's WightedEdges.

### Workflow

- The Main class initializes the application, creating the map panel and graph.
- When the user selects two points, the application calculates the shortest path and displays it.

### Contributing

1. Fork the repository.
2. Create a new branch for your feature or bug fix:
   ```bash
   git checkout -b feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add feature-name"
   ```
4. Push to your branch:
   ```bash
   git push origin feature-name
   ```
5. Open a pull request.

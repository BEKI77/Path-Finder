# OpenStreetMap Path Finder

OpenStreetMap Path Finder is a Java-based application that allows users to interact with a map, select two points, and calculate the shortest path between them. The application uses OpenStreetMap data for map rendering and provides a graphical user interface for user interaction.

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
  - Left-click to set the start point.
  - Right-click to set the end point.
- Calculates and displays the shortest path between the selected points.
- Simple and intuitive graphical user interface.

---

## Project Structure

```bash
build.gradle
README.md
.gitignore
.gradle/
.vscode/
build/
lib/
src/
    main/
        java/
            controller/
                PathController.java
            model/
                Graph.java
                Node.java
                Edge.java
            view/
                MapPanel.java
            Main.java
```

### Key Files

- **`Main.java`**: The entry point of the application. Sets up the GUI and initializes the map and graph.
- **`PathController.java`**: Handles user interactions with the map, such as selecting points and finding paths.
- **`Graph.java`**: Represents the graph structure, including nodes and edges.
- **`Node.java`**: Represents a node in the graph with latitude, longitude, and an identifier.
- **`MapPanel.java`**: A custom Swing component for rendering the map using JMapViewer.

---

## Dependencies

The project relies on the following libraries:

1. **JMapViewer**: A Java Swing component for rendering OpenStreetMap tiles.
   - Location: `lib/JMapViewer.jar`
2. **osm4j-core**: A library for working with OpenStreetMap data.
   - Location: `lib/osm4j-core-1.4.0.jar`
3. **osm4j-pbf**: A library for parsing OpenStreetMap PBF files.
   - Location: `lib/osm4j-pbf-1.4.0.jar`

---

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher.
- Gradle (optional, for building the project).

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/your-repo.git
   cd your-repo
   ```
2. Ensure the required JAR files are present in the lib/ directory:
   JMapViewer.jar

3. Compile the project:
   - it's preferable if you use gradle for development
   ```bash
   gradle build
   gradle run
   ```

### Usage

    1. Launch the application.
    2. Follow the on-screen instructions:
    3. Left-click on the map to set the start point.
    4. Right-click on the map to set the end point.
    5. The application will calculate and display the shortest path between the two points.

### How It Works

Core Components

1. Graph Representation:

   - The graph is represented using the Graph class, which contains nodes (Node objects) and edges (connections between nodes with weights).

2. Map Interaction:

   - The MapPanel class (based on JMapViewer) renders the map and allows users to interact with it.
   - The PathController listens for mouse events to capture user input and determine the selected points.

3. Shortest Path Calculation:

   - The PathController calculates the shortest path between the start and end nodes using the graph's edges and nodes.

### Workflow

- The Main class initializes the application, creating the map panel and graph.
- The PathController is instantiated to handle user interactions.
  = When the user selects two points, the application calculates the shortest path and displays it.

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

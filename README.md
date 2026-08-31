![GitHub Repo Banner](https://ghrb.waren.build/banner?header=Time+Planning&subheader=Schedule+Generation+Simplified&bg=3AF8B9-9231A8&color=FFFFFF&subheadercolor=FFFFFF&headerfont=JetBrains+Mono&subheaderfont=Dancing+Script&watermarkpos=bottom-right)
<!-- Created with GitHub Repo Banner by Waren Gonzaga: https://ghrb.waren.build -->

> **A constraint-solving engine that transforms an unordered list of tasks into a structured timetable.**
> Stop deciding what to do on the spot — let the machine handle the backtracking so you can just do what you do best — being productive.

---

## Overview

**Time Planning** is a desktop application built for people who prefer a structured planning of their week over spontaneity.

While you *could* manually backtrack through hundreds of tasks to fit them into a schedule, it is tedious and you could end up violating your own set rules. 
You **could** ask AI, but LLMs often lose track of complex constraints halfway through, and they tend to lie straight to your face when claiming their solution is correct...

This tool tries to dig at these inconveniences by leveraging **Google's CP-SAT Solver**, to find a solution if one exists.

## Features
All features are laid out for a weekly schedule generation.
- **Task Management** – Define tasks with time windows in which they must be scheduled (e.g. "Go to the gym on Sunday").
- **Group Constraints** – Enforce minimum or maximum gaps between tasks belonging to specific groups (e.g. "Meetings must be at least 1 hour apart").
- **Multiple Scheduling Strategies** – Generate timetables based on different goals:
    - **Early Finish** – Pack tasks as early as possible to free up the afternoon and evening.
    - **Flow State** – Cluster tasks from the same group together to minimize context switching.
    - **Grouped Leisure** – Get the most out of your free time by having less interruptions.    
    - **Memorizable Schedule** – Adapt more quickly to the schedule using similar start times every day.
- **Feasibility Guarantee** – Eventually tells you if your constraints are feasible, saving you from building a broken schedule.

## Demo & Screenshots

The following clips show how the application usage described in the below 'Getting Started' guide should look like.

*(Coming soon! The current UI is built with Swing/FlatLaf. While functional, we are working on capturing the best visual examples of the generated timetables.)*

## Getting Started

### Prerequisites
- **Java 17** installed on your machine.
- An IDE or terminal with **Gradle** support (the wrapper is included).
- All other dependencies (Spring Boot, OR-Tools, FlatLaf) are managed automatically via `build.gradle`.

### Installation
Simply clone the repository and sync the Gradle project.

### Running the Application

The application runs as a client-server model locally:

Start the Server: Run the TimePlanningApplication Class main method.

Start the Client: Run the Main Class main method.

### Usage
Login using the default credentials (for demo purposes):

- Username: admin
- Password: pass

Once logged in, a pre-loaded demo setup (tasks + constraints) will be available.

Adjust the provided data as you see fit and hit the "Generate" button. Choose how many solutions you want to receive and how long you want to wait as well as the strategy according to which the solutions should be ranked.

View the output: The application will render a visual timetable. Make sure that your task durations/start or end dates are aligned to the slot length you can find in Settings.
Otherwise your timetable will look buggy. If your constraints are too tight, you will either be notified that no solution was found or receive timetables that violate specified GroupConstraints.

If you want to build an example yourself, feel free to create a random account and login with those credentials.
(Since everything runs locally, no data will be leaked.)

## Testing
To verify the core scheduling logic without dealing with the Swing interface, we provide two entry points:

**ServerSanityCheck** – Runs the backend logic standalone to test constraint loading.

**ClientSanityCheck** – Located in the Client package, this runs a series of integration tests for endpoint handling without rendering the UI.

Use these to ensure the engine works before you hook it up to the frontend.
Note: As the name suggests, these are more simple checks than a sophisticated testing suite.
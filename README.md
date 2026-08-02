# IMindustry

Experimental Mindustry mod project scaffold.

Structure:
MyMod/
├── assets/
│   ├── content/
│   ├── sprites/
│   ├── bundles/
│   ├── sounds/
│   └── music/
│
├── src/
│   └── mymod/
│       └── MyMod.java
│
├── build.gradle
├── gradlew
├── gradle.properties
└── mod.json

How to build (basic):
1) Install Java 17+ and Gradle.
2) From repository root run:
   ./gradlew jar

The resulting jar will contain mod.json and assets and can be placed into Mindustry's mods/ folder.

Notes:
- The build.gradle contains a placeholder for a Mindustry dependency; you may need to add the proper dependency coordinates for your target Mindustry version.
- If you want me to push compiled artifacts (jar/zip) automatically via GitHub Actions, tell me and I'll add a workflow.

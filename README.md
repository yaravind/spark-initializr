# spark-multi-runtime-archetype

Maven archetype that generates a minimal Spark + Delta app template compatible with:

- Microsoft Fabric Runtime 1.3 (Spark 3.5.x / Delta 3.2.x / Scala 2.12 / Java 11)
- Microsoft Fabric Runtime 2.0 (Spark 4.0.x / Delta 4.0.x / Scala 2.13 / Java 21)
- Databricks Runtime 18.0 (Spark 4.0.0 / Delta 4.0.1 / Scala 2.13 / Java 21)
- Azure Synapse Spark 3.4 (Spark 3.4.1 / Delta 2.4.0 / Scala 2.12 / Java 11)

## Release

See [RELEASE.md](RELEASE.md) for the official release workflow order and flow diagram.

## Quick start

Build the archetype:

```bash
mvn -B -U clean verify
```

Generate a project:

```bash
mvn -B archetype:generate \
  -DarchetypeGroupId=io.github.yaravind \
  -DarchetypeArtifactId=spark-multi-runtime-archetype \
  -DarchetypeVersion=0.1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-spark-app \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.example.app
```

Build for a target runtime:

```bash
cd my-spark-app
mvn -B -Druntime=fabric13 test
```

Supported values for `runtime`: `fabric13`, `fabric20`, `databricks180`, `synapse34`.

## Step-by-step: usage by runtime

### 0) Prerequisites

- Maven 3.9+
- JDK per runtime:
  - `fabric13`: Java 11+
  - `fabric20`: Java 21+
  - `databricks180`: Java 21+
  - `synapse34`: Java 11+

### 1) Install the archetype locally (one-time)

Until the archetype is published to a remote Maven repository, install it to your local Maven repository:

```bash
git clone https://github.com/yaravind/spark-multi-runtime-archetype.git
cd spark-multi-runtime-archetype
mvn -B -U clean install
```

### 2) Generate a new project

From any folder, run:

```bash
mvn -B archetype:generate \
  -DarchetypeGroupId=io.github.yaravind \
  -DarchetypeArtifactId=spark-multi-runtime-archetype \
  -DarchetypeVersion=0.1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-spark-app \
  -Dversion=0.1.0-SNAPSHOT \
  -Dpackage=com.example.app
```

Then:

```bash
cd my-spark-app
```

### 3) Build / test for a target runtime

The generated app contains:

- common sources: `src/main/scala-common`
- runtime sources: `src/main/scala-<runtime>`

Pick a runtime at build time with `-Druntime=...`.

#### Fabric Runtime 1.3 (`fabric13`)

```bash
mvn -B -Druntime=fabric13 test
mvn -B -Druntime=fabric13 package
```

#### Fabric Runtime 2.0 (`fabric20`)

Make sure you are using Java 21+.

```bash
mvn -B -Druntime=fabric20 test
mvn -B -Druntime=fabric20 package
```

#### Databricks Runtime 18.0 (`databricks180`)

Make sure you are using Java 21+.

```bash
mvn -B -Druntime=databricks180 test
mvn -B -Druntime=databricks180 package
```

#### Synapse Spark 3.4 (`synapse34`)

```bash
mvn -B -Druntime=synapse34 test
mvn -B -Druntime=synapse34 package
```

### Notes

- Spark/Delta dependencies in the generated app are `provided` (they’re supplied by the cluster runtime). The `*-all.jar` produced by the shade plugin is mainly useful for bundling your own dependencies, not Spark itself.

## License

Apache-2.0. See [LICENSE](LICENSE).

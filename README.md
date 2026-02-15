# spark-multi-runtime-archetype

Maven archetype that generates a minimal Spark + Delta app template compatible with:

- Microsoft Fabric Runtime 1.3 (Spark 3.5.x / Delta 3.2.x / Scala 2.12 / Java 11)
- Microsoft Fabric Runtime 2.0 (Spark 4.0.x / Delta 4.0.x / Scala 2.13 / Java 21)
- Databricks Runtime 15.4 LTS (Spark 3.5.0 / Delta 3.2.0 / Scala 2.12 / Java 8)
- Azure Synapse Spark 3.4 (Spark 3.4.1 / Delta 2.4.0 / Scala 2.12 / Java 11)

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

Supported values for `runtime`: `fabric13`, `fabric20`, `databricks154`, `synapse34`.

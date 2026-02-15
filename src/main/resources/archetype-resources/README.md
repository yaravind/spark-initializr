# ${artifactId}

Generated Spark + Delta template that can be built against multiple runtimes.

## Build / test

Select a runtime with `-Druntime=...`.

- `fabric13` (Spark 3.5.5 / Delta 3.2.0 / Scala 2.12.18 / Java 11)
- `fabric20` (Spark 4.0.0 / Delta 4.0.0 / Scala 2.13.16 / Java 21)
- `databricks154` (Spark 3.5.0 / Delta 3.2.0 / Scala 2.12.15 / Java 8)
- `synapse34` (Spark 3.4.1 / Delta 2.4.0 / Scala 2.12.17 / Java 11)

Examples:

```bash
mvn -B -Druntime=fabric13 test
mvn -B -Druntime=synapse34 test
```

## Source sets

This template generates all runtime-specific source roots and uses Maven profiles to compile only the selected runtime.

- Common sources: `src/main/scala-common`
- Runtime sources: `src/main/scala-fabric13`, `src/main/scala-fabric20`, `src/main/scala-databricks154`, `src/main/scala-synapse34`

The common code depends on a runtime-provided `SparkRuntime` object.

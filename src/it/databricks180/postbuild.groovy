import groovy.xml.XmlSlurper

static int parseJavaSpecVersion(String spec) {
  if (spec == null || spec.trim().isEmpty()) {
    return 0
  }
  def s = spec.trim()
  if (s.startsWith("1.")) {
    return Integer.parseInt(s.substring(2))
  }
  return Integer.parseInt(s)
}

static void deleteRecursively(File file) {
  if (!file.exists()) {
    return
  }
  if (file.isDirectory()) {
    file.listFiles()?.each { deleteRecursively(it) }
  }
  if (!file.delete()) {
    throw new RuntimeException("Failed to delete: ${file}")
  }
}

static void runCmd(List cmd, File workDir) {
  def pb = new ProcessBuilder(cmd.collect { it.toString() })
  pb.directory(workDir)
  pb.redirectErrorStream(true)
  def proc = pb.start()
  proc.inputStream.withReader { reader ->
    reader.eachLine { line -> println(line) }
  }
  def code = proc.waitFor()
  if (code != 0) {
    throw new RuntimeException("Command failed (${code}): ${cmd.join(' ')}")
  }
}

def props = new Properties()
new File(basedir, "runtime.properties").withInputStream { props.load(it) }

def runtime = props.getProperty("runtime")
def requiredJava = Integer.parseInt(props.getProperty("requiredJava"))

def currentJava = parseJavaSpecVersion(System.getProperty("java.specification.version"))
if (currentJava < requiredJava) {
  println("Skipping IT for runtime '${runtime}': requires Java ${requiredJava}+ (current: ${currentJava})")
  return true
}

def root = new File(basedir, "../../..").canonicalFile

def settingsFile = new File(root, "src/it/mvn-settings.xml")
if (!settingsFile.exists()) {
  throw new RuntimeException("Expected settings file does not exist: ${settingsFile}")
}

def pom = new XmlSlurper(false, false).parse(new File(root, "pom.xml"))
def groupId = pom.groupId?.text() ?: pom.parent?.groupId?.text()
def artifactId = pom.artifactId.text()
def version = pom.version?.text() ?: pom.parent?.version?.text()

if (!groupId || !artifactId || !version) {
  throw new RuntimeException("Unable to resolve GAV from root pom.xml")
}

def jar = new File(root, "target/${artifactId}-${version}.jar")
if (!jar.exists()) {
  throw new RuntimeException("Expected archetype JAR does not exist: ${jar}")
}

def localRepo = new File(basedir, "local-repo")
localRepo.mkdirs()

def mvnRepoLocal = "-Dmaven.repo.local=${localRepo.canonicalPath}"

runCmd([
  "mvn", "-s", settingsFile.canonicalPath, "-B", "-q", mvnRepoLocal,
  "org.apache.maven.plugins:maven-install-plugin:3.1.2:install-file",
  "-Dfile=${jar.canonicalPath}",
  "-DgroupId=${groupId}",
  "-DartifactId=${artifactId}",
  "-Dversion=${version}",
  "-Dpackaging=jar"
], root)

def workDir = new File(basedir, "work")
workDir.mkdirs()

def generatedDir = new File(workDir, "it-app-${runtime}")
if (generatedDir.exists()) {
  deleteRecursively(generatedDir)
}

runCmd([
  "mvn", "-s", settingsFile.canonicalPath, "-B", "-q", mvnRepoLocal,
  "org.apache.maven.plugins:maven-archetype-plugin:3.3.1:generate",
  "-DinteractiveMode=false",
  "-DoutputDirectory=${workDir.canonicalPath}",
  "-DarchetypeGroupId=${groupId}",
  "-DarchetypeArtifactId=${artifactId}",
  "-DarchetypeVersion=${version}",
  "-DgroupId=com.example",
  "-DartifactId=it-app-${runtime}",
  "-Dversion=0.1.0-SNAPSHOT",
  "-Dpackage=com.example"
], workDir)

runCmd([
  "mvn", "-s", settingsFile.canonicalPath, "-B", "-q", mvnRepoLocal,
  "-Druntime=${runtime}",
  "test"
], generatedDir)

return true

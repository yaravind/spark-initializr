def root = new File(basedir, "../../..").canonicalFile
def shared = new File(root, "src/it/postbuild.shared.groovy")
if (!shared.exists()) {
  throw new RuntimeException("Expected shared postbuild script does not exist: ${shared}")
}
return new GroovyShell(this.binding).evaluate(shared)

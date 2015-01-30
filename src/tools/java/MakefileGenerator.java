import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Generates a makefile following the specification.
 * template: cs.swarthmore.edu/~newhall/unixhelp/javamakefiles.html
 */
public class MakefileGenerator {

  public static void main(String[] args) throws IOException {
    writeMakefile(getAllFiles());
  }

  private static boolean isJavaFile(File file) {
    String[] fileSplit = file.getName().split("\\.");
    return fileSplit.length >= 2  && fileSplit[fileSplit.length - 1].equals("java");
  }

  private static ArrayList<String> getAllFiles() {
    ArrayList<String> fileNames = new ArrayList<>();
    Queue<File> dirs = new LinkedList<>();
    File baseDirectory = new File("src/main/java/");
    dirs.add(baseDirectory);
    while (!dirs.isEmpty()) {
      for (File f : dirs.poll().listFiles()) {
        if (f.isDirectory()) {
          dirs.add(f);
        } else if (f.isFile() && isJavaFile(f)) {
          fileNames.add("src/main/java/" + baseDirectory.toURI().relativize(f.toURI()).getPath());
        }
      }
    }
    return fileNames;
  }


  public static void writeMakefile(ArrayList<String> files) throws IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(new File("Makefile")));

    writer.println("JFLAGS = -J-Xmx256M -cp");
    writer.println("JC = javac");
    writer.println("CLASSPATH = src/main/java:.");
    writer.println(".SUFFIXES: .java .class");
    writer.println(".java.class:");
    writer.println("\t$(JC) $(JFLAGS) src/main/java:. -d classes/ $*.java");
    writer.println("CLASSES = \\");
    for (int i = 0; i < files.size() - 1; ++i) {
      writer.println("  " + files.get(i) + " \\");
    }
    if (!files.isEmpty()) {
      writer.println("  " + files.get(files.size() - 1));
    }

    writer.println("default: clean classesdir classes");
    writer.println("classes: $(CLASSES:.java=.class)");
    writer.println("classesdir:");
    writer.println("\tmkdir classes");
    writer.println("clean:");
    writer.println("\trm -rf *.class classes");
    writer.close();
  }


}
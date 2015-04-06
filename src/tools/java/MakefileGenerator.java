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
    ArrayList<String> fileNames = new ArrayList<String>();
    Queue<File> dirs = new LinkedList<File>();
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
    writer.println("default: clean classesdir outputdir cs444");
    writer.println("cs444:");
    writer.print("\t$(JC) $(JFLAGS) src/main/java:. -d classes/");
    for (int i = 0; i < files.size(); ++i) {
      writer.print("  " + files.get(i));
    }
    writer.println();
    writer.println("classesdir:");
    writer.println("\tmkdir classes");
    writer.println();
    writer.println("outputdir:");
    writer.println("\tmkdir output");
    writer.println("clean:");
    writer.println("\trm -rf *.class main classes output");
    writer.close();
  }


}
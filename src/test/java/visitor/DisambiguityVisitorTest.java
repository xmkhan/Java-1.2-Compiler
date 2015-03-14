package visitor;

import exception.CompilerException;
import org.junit.Before;
import org.junit.Test;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for basic disambiguity functionality
 */
public class DisambiguityVisitorTest {

  private CompilationUnitGenerator.Bundle bundle;
  private DisambiguityVisitor visitor;

  @Before
  public void setUp() {
  }

  @Test
  public void testFieldVsType() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
       "src/test/resources/disambiguity/J1_5_AmbiguousName_FieldVsType.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void testProtectedAccess() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_ImplicitSuper/Main.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_ImplicitSuper/A.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_ImplicitSuper/B.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_ImplicitSuper/C.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_ImplicitSuper/D.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void testClosedConstructor() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_closestMatchConstructor1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void testInterfaceObject() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_InterfaceObject/Main.java",
        "src/test/resources/disambiguity/J1_InterfaceObject/java/util/Collection.java",
        "src/test/resources/disambiguity/J1_InterfaceObject/java/util/LinkedList.java",
        "src/test/resources/disambiguity/J1_InterfaceObject/java/util/List.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }
}

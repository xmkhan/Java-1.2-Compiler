package visitor;

import exception.CompilerException;
import org.junit.Before;
import org.junit.Test;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for basic type checking functionality
 */
public class TypeCheckingVisitorTest {

  private CompilationUnitGenerator.Bundle bundle;
  private TypeCheckingVisitor visitor;

  @Before
  public void setUp() {
  }

  @Test
  public void testFieldVsType() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/disambiguity/J1_5_AmbiguousName_FieldVsType.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testReachable() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/reachability/J1_Reachable1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }
}
package visitor;

import exception.CompilerException;
import org.junit.Before;
import org.junit.Test;
import util.CompilationUnitGenerator;

import java.io.IOException;
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
  public void testStdlib() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

}

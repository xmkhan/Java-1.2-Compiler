package visitor;

import exception.CompilerException;
import exception.ReachabilityVisitorException;
import org.junit.Before;
import org.junit.Test;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for basic reachability functionality
 */
public class ReachabilityVisitorTest {
  private CompilationUnitGenerator.Bundle bundle;
  private ReachabilityVisitor visitor;

  @Before
  public void setUp() {
    visitor = new ReachabilityVisitor();
  }

  @Test
  public void testReachable() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_Reachable1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor.checkReachability(bundle.units);
  }

  @Test
  public void testWhileTrue() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_whiletrue1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor.checkReachability(bundle.units);
  }

  @Test
  public void testReachableReturn() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_7_Reachability_IfAndWhile_Return.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor.checkReachability(bundle.units);
  }

  @Test(expected = ReachabilityVisitorException.class)
  public void testInvalidReachability() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/Je_7_Return_IfIfNot.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor.checkReachability(bundle.units);
  }

  @Test(expected = ReachabilityVisitorException.class)
  public void testInitToItself() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/Je_8_DefiniteAssignment_InitToItself.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor.checkReachability(bundle.units);
  }

}

package visitor;

import exception.CompilerException;
import exception.ReachabilityVisitorException;
import exception.SelfAssignmentVisitorException;
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
  private ReachabilityVisitor reachabilityVisitor;
  private SelfAssignmentVisitor selfAssignmentVisitor;

  @Before
  public void setUp() {
    reachabilityVisitor = new ReachabilityVisitor();
    selfAssignmentVisitor = new SelfAssignmentVisitor();
  }

  @Test
  public void testReachable() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_Reachable1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    reachabilityVisitor.checkReachability(bundle.units);
  }

  @Test
  public void testWhileTrue() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_whiletrue1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    reachabilityVisitor.checkReachability(bundle.units);
  }

  @Test
  public void testReachableReturn() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/J1_7_Reachability_IfAndWhile_Return.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    reachabilityVisitor.checkReachability(bundle.units);
  }

  @Test(expected = ReachabilityVisitorException.class)
  public void testInvalidReachability() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/Je_7_Return_IfIfNot.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    reachabilityVisitor.checkReachability(bundle.units);
  }

  @Test(expected = SelfAssignmentVisitorException.class)
  public void testInitToItself() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/reachability/Je_8_DefiniteAssignment_InitToItself.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    selfAssignmentVisitor.checkSelfAssignment(bundle.units);
  }
}

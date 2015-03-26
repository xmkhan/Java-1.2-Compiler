package visitor;

import exception.CompilerException;
import exception.TypeLinkingVisitorException;
import exception.VisitorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
  public void testWrappedWithBrackets() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_good_dot.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testStringAddition() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_intstringadd.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testAddition() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_typecheck_plus.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidPrimary() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_1_MethodInvocation_Primitive.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidMethodParameter() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_MethodPresent_ArgumentTypeMismatch.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testRandom() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/reachability/J1_Reachable1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }
}
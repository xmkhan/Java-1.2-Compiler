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

  @Test
  public void testInstanceOf() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_typecheck_instanceof1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testObjectArrayCast() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_6_Assignable_Object_ObjectArray.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testAmbiguousInvoke() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_ambiguousInvoke/foo/Foo.java",
            "src/test/resources/typechecking/J1_ambiguousInvoke/Main.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testArraySubclasses() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_ArrayCast3/A.java",
            "src/test/resources/typechecking/J1_ArrayCast3/Main.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testInterfacesAssignment() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_interfaceassignable/java/util/Collection.java",
            "src/test/resources/typechecking/J1_interfaceassignable/java/util/LinkedList.java",
            "src/test/resources/typechecking/J1_interfaceassignable/java/util/List.java",
            "src/test/resources/typechecking/J1_interfaceassignable/Main.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testNullInstanceOf() throws IOException, CompilerException {
    java.util.List t = null;
    java.lang.Object o = t;

    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_nullinstanceof1.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test
  public void testArrays() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/J1_typecheck_array.java"
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
  public void testInvalidForCondition() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_For_NullInCondition.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidReturn() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_Assignable_ReturnInElse.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidReturnVoid() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_Assignable_Return_VoidInVoidMethod.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidConstructorReturn() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_Assignable_ValueReturn_InConstructor.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToDisambiguity(files);
    visitor = new TypeCheckingVisitor(bundle.symbolTable, bundle.graph, bundle.compilationUnitToNode);
    visitor.typeCheckUnits(bundle.units);
  }

  @Test(expected = VisitorException.class)
  public void testInvalidSuper() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_ConstructorPresent_Super_NoDefault/Foo.java",
            "src/test/resources/typechecking/Je_6_ConstructorPresent_Super_NoDefault/Main.java"
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

  @Test(expected = VisitorException.class)
  public void testIfBoolean() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
            "src/test/resources/typechecking/Je_6_Assignable_Condition.java"
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
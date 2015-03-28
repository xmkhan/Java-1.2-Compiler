package visitor;

import exception.CompilerException;
import exception.DisambiguityVisitorException;
import exception.TypeHierarchyException;
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
  public void testForwardField() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_forwardfield2.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void testNestedProtectedAccess() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_forwardfield2.java"
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

  @Test
  public void test1() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_5_AmbiguousName_DefaultPackageNotVisible/Main.java",
        "src/test/resources/disambiguity/J1_5_AmbiguousName_DefaultPackageNotVisible/Bar.java",
        "src/test/resources/disambiguity/J1_5_AmbiguousName_DefaultPackageNotVisible/Main/Bar.java",
        "src/test/resources/disambiguity/J1_5_AmbiguousName_DefaultPackageNotVisible/foo/Bar.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void test2() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_5_ForwardReference_EqualInfix.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void test3() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_InstanceField_SubVar/Main.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_InstanceField_SubVar/A.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_InstanceField_SubVar/B.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_InstanceField_SubVar/C.java",
        "src/test/resources/disambiguity/J1_6_ProtectedAccess_InstanceField_SubVar/D.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void test4() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_accessstaticfield/Main.java",
        "src/test/resources/disambiguity/J1_accessstaticfield/java/util/Calendar.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test
  public void test5() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_supermethod_override1/Main.java",
        "src/test/resources/disambiguity/J1_supermethod_override1/CompA.java",
        "src/test/resources/disambiguity/J1_supermethod_override1/CompB.java",
        "src/test/resources/disambiguity/J1_supermethod_override1/java/lang/Comparable.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test(expected = TypeHierarchyException.class)
  public void test6() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/J1_supermethod_override11/Main.java",
        "src/test/resources/disambiguity/J1_supermethod_override11/CompA.java",
        "src/test/resources/disambiguity/J1_supermethod_override11/CompB.java",
        "src/test/resources/disambiguity/J1_supermethod_override11/CompC.java",
        "src/test/resources/disambiguity/J1_supermethod_override11/java/lang/Comparable.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test(expected = DisambiguityVisitorException.class)
  public void test7() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/Je_5_ForwardReference_ArrayLength.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test(expected = DisambiguityVisitorException.class)
  public void test8() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/Je_5_ForwardReference_MethodCall.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test(expected = DisambiguityVisitorException.class)
  public void test9() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/Je_5_ForwardReference_FieldDeclaredLater.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }

  @Test(expected = DisambiguityVisitorException.class)
  public void test10() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/disambiguity/Je_5_ForwardReference_FieldInOwnInitializer_ReadAfterAssignment.java"
    ));
    bundle = CompilationUnitGenerator.makeUpToTypeChecking(files);
    visitor = new DisambiguityVisitor(bundle.symbolTable, bundle.graph);
    visitor.disambiguateUnits(bundle.units);
  }


}

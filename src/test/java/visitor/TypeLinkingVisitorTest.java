package visitor;

import exception.CompilerException;
import exception.TypeLinkingVisitorException;
import org.junit.Before;
import org.junit.Test;
import symbol.SymbolTable;
import token.CompilationUnit;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests the basic functionality of TypeLinkingVisitor
 */
public class TypeLinkingVisitorTest {
  private SymbolTable table;
  private EnvironmentBuildingVisitor buildingVisitor;
  private TypeLinkingVisitor typeLinkingVisitor;

  @Before
  public void setUp() {
    table = new SymbolTable();
    buildingVisitor = new EnvironmentBuildingVisitor(table);
    typeLinkingVisitor = new TypeLinkingVisitor(table);
  }

  @Test(expected = TypeLinkingVisitorException.class)
  public void testImportNameClashWithClass() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/Je_3_SingleTypeImport_ClashWithInterface/Main.java",
        "src/test/resources/type_linking/Je_3_SingleTypeImport_ClashWithInterface/List.java",
        "src/test/resources/type_linking/Je_3_SingleTypeImport_ClashWithInterface/java/util/ArrayList.java",
        "src/test/resources/type_linking/Je_3_SingleTypeImport_ClashWithInterface/java/util/Collection.java",
        "src/test/resources/type_linking/Je_3_SingleTypeImport_ClashWithInterface/java/util/List.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test
  public void testSingleImportOnDemandImportClash() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/J1_3_SingleTypeImport_ClashWithOnDemand/Main.java",
        "src/test/resources/type_linking/J1_3_SingleTypeImport_ClashWithOnDemand/foo/List/Bar.java",
        "src/test/resources/type_linking/J1_3_SingleTypeImport_ClashWithOnDemand/java/util/Collection.java",
        "src/test/resources/type_linking/J1_3_SingleTypeImport_ClashWithOnDemand/java/util/List.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test(expected = TypeLinkingVisitorException.class)
  public void testOnDemandClashWithStdlib() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/Je_3_ImportOnDemand_ClashWithImplicitImport/Main.java",
        "src/test/resources/type_linking/Je_3_ImportOnDemand_ClashWithImplicitImport/Integer.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test(expected = TypeLinkingVisitorException.class)
  public void testPkgNameAsClassName() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/Je_3_PackageNameIsClassName/Main.java",
        "src/test/resources/type_linking/Je_3_PackageNameIsClassName/foo/bar.java",
        "src/test/resources/type_linking/Je_3_PackageNameIsClassName/foo/bar/baz.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test(expected = TypeLinkingVisitorException.class)
  public void testTypeNamePrefixAsType() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/Je_4_Resolve_DefaultPackage/Main.java",
        "src/test/resources/type_linking/Je_4_Resolve_DefaultPackage/foo/bar.java",
        "src/test/resources/type_linking/Je_4_Resolve_DefaultPackage/foo.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test(expected = TypeLinkingVisitorException.class)
  public void testImplicitJavaImport() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/Je_3_Resolve_ImplicitJavaIO/Main.java"

    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test
  public void testOnDemandImport() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/J1_3_OnDemandImport_NonAmbiguous_Default/Main.java",
        "src/test/resources/type_linking/J1_3_OnDemandImport_NonAmbiguous_Default/List.java",
        "src/test/resources/type_linking/J1_3_OnDemandImport_NonAmbiguous_Default/java/awt/List.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test
  public void testDefaultPkgNotResolve() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/J1_3_InfixResolvesToType/Main.java",
        "src/test/resources/type_linking/J1_3_InfixResolvesToType/foo/String/bar/foo.java"
    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }

  @Test
  public void testImport() throws IOException, CompilerException {
    List<String> files = CompilationUnitGenerator.getStdlibFiles();
    files.addAll(Arrays.asList(
        "src/test/resources/type_linking/J1_importName9.java"

    ));
    List<CompilationUnit> units = CompilationUnitGenerator.make(files);
    buildingVisitor.buildGlobalScope(units);
    typeLinkingVisitor.typeLink(units);
  }
}

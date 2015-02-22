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

  @Test(expected = TypeLinkingVisitorException.class)
  public void testImportNameClashWithImports() throws IOException, CompilerException {
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
}

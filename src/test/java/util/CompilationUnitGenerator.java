package util;

import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.CompilerException;
import lexer.Lexer;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.Token;
import type.hierarchy.CompilationUnitsToHierarchyGraphConverter;
import type.hierarchy.HierarchyChecker;
import type.hierarchy.HierarchyGraph;
import visitor.EnvironmentBuildingVisitor;
import visitor.TypeLinkingVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstracts the part of Main.java that reads in a list of files and produces the CompilationUnits.
 */
public class CompilationUnitGenerator {

  public static List<CompilationUnit> make(String[] filePaths) throws IOException, CompilerException {
    return make(Arrays.asList(filePaths));
  }

  /**
   * Every time this function is called, we do the same work as Main.java to construct the
   * CompilationUnits, even though the LR(1) machine could be cached (so every test is fully independent).
   */
  public static List<CompilationUnit> make(List<String> filePaths) throws IOException, CompilerException {
    Lexer lexer = new Lexer();
    InputStreamReader lr1Reader = new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE));
    ShiftReduceAlgorithm shiftReduceAlgorithm = new ShiftReduceAlgorithm(lr1Reader);
    List<CompilationUnit> units = new ArrayList<CompilationUnit>(filePaths.size());
    for (String file : filePaths) {
      lexer.resetDFAs();
      shiftReduceAlgorithm.reset();
      InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "US-ASCII");
      ArrayList<Token> tokens = lexer.parse(reader);
      units.add(shiftReduceAlgorithm.constructAST(tokens));
    }
    return units;
  }

  public static Bundle makeUpToTypeChecking(List<String> filePaths) throws IOException, CompilerException {
    Bundle bundle = new Bundle();
    bundle.units = make(filePaths);
    bundle.symbolTable = new SymbolTable();

    EnvironmentBuildingVisitor environmentVisitor = new EnvironmentBuildingVisitor(bundle.symbolTable);
    environmentVisitor.buildGlobalScope(bundle.units);

    TypeLinkingVisitor typeLinkingVisitor = new TypeLinkingVisitor(bundle.symbolTable);
    typeLinkingVisitor.typeLink(bundle.units);

    CompilationUnitsToHierarchyGraphConverter converter = new CompilationUnitsToHierarchyGraphConverter();
    bundle.graph = converter.convert(bundle.units);
    HierarchyChecker.verifyHierarchyGraph(bundle.graph);

    return bundle;
  }

  public static List<String> getStdlibFiles() {
    return new ArrayList<String>(Arrays.asList(
      "src/test/resources/stdlib/java/io/OutputStream.java",
      "src/test/resources/stdlib/java/io/PrintStream.java",
      "src/test/resources/stdlib/java/io/Serializable.java",
      "src/test/resources/stdlib/java/lang/Boolean.java",
      "src/test/resources/stdlib/java/lang/Byte.java",
      "src/test/resources/stdlib/java/lang/Character.java",
      "src/test/resources/stdlib/java/lang/Class.java",
      "src/test/resources/stdlib/java/lang/Cloneable.java",
      "src/test/resources/stdlib/java/lang/Integer.java",
      "src/test/resources/stdlib/java/lang/Number.java",
      "src/test/resources/stdlib/java/lang/Object.java",
      "src/test/resources/stdlib/java/lang/Short.java",
      "src/test/resources/stdlib/java/lang/String.java",
      "src/test/resources/stdlib/java/lang/System.java",
      "src/test/resources/stdlib/java/util/Arrays.java"
    ));
  }

  public static class Bundle {
    public List<CompilationUnit> units;
    public SymbolTable symbolTable;
    public HierarchyGraph graph;
  }
}

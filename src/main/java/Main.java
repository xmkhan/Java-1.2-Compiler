import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.CompilerException;
import lexer.Lexer;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.Token;
import type.hierarchy.CompilationUnitsToHierarchyGraphConverter;
import type.hierarchy.HierarchyChecker;
import type.hierarchy.HierarchyGraph;
import visitor.DisambiguityVisitor;
import visitor.EnvironmentBuildingVisitor;
import visitor.GenericCheckVisitor;
import visitor.ReachabilityVisitor;
import visitor.SelfAssignmentVisitor;
import visitor.TypeLinkingVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Main executable for CS444
 */
public class Main {

  public static void main(String[] args) throws IOException {
    try {
      InputStreamReader lr1Reader = new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE));

      Lexer lexer = new Lexer();
      ShiftReduceAlgorithm shiftReduceAlgorithm = new ShiftReduceAlgorithm(lr1Reader);
      List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>(args.length);
      // 1. Phase 1: Construct the AST per CompilationUnit (per class), and do basic static checks.
      for (String arg : args) {
        InputStreamReader reader = new InputStreamReader(new FileInputStream(arg), "US-ASCII");
        lexer.resetDFAs();
        shiftReduceAlgorithm.reset();
        ArrayList<Token> tokens = lexer.parse(reader);
        CompilationUnit compilationUnit = shiftReduceAlgorithm.constructAST(tokens);
        compilationUnit.accept(new GenericCheckVisitor(new File(arg).getName()));
        compilationUnits.add(compilationUnit);
      }
      // 2. Phase 2: Construct SymbolTable, handle name resolution, and do type hierarchy checks.
      SymbolTable table = new SymbolTable();
      EnvironmentBuildingVisitor environmentVisitor = new EnvironmentBuildingVisitor(table);
      environmentVisitor.buildGlobalScope(compilationUnits);

      TypeLinkingVisitor typeLinkingVisitor = new TypeLinkingVisitor(table);
      typeLinkingVisitor.typeLink(compilationUnits);

      CompilationUnitsToHierarchyGraphConverter converter = new CompilationUnitsToHierarchyGraphConverter();
      HierarchyGraph graph = converter.convert(compilationUnits);
      HierarchyChecker.verifyHierarchyGraph(graph);

      // 3. Phase 3: Disambiguate types, and perform type checking.
      DisambiguityVisitor disambiguityVisitor = new DisambiguityVisitor(table, graph);
      disambiguityVisitor.disambiguateUnits(compilationUnits);

      // 4. Phase 4: Static analysis
      ReachabilityVisitor reachabilityVisitor = new ReachabilityVisitor();
      reachabilityVisitor.checkReachability(compilationUnits);

      SelfAssignmentVisitor selfAssignmentVisitor = new SelfAssignmentVisitor(table);
      selfAssignmentVisitor.checkSelfAssignment(compilationUnits);

    } catch (CompilerException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
      System.exit(42);
    }
    System.exit(0);
  }
}

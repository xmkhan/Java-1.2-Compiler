package visitor;

import symbol.SymbolTable;
import type.hierarchy.CompilationUnitsToHierarchyGraphConverter;
import type.hierarchy.HierarchyGraph;

/**
 * Tests for basic disambiguity functionality
 */
public class DisambiguityVisitorTest {
  private SymbolTable table;

  private CompilationUnitsToHierarchyGraphConverter converter;
  private HierarchyGraph graph;
  private EnvironmentBuildingVisitor environmentBuildingVisitor;

  private DisambiguityVisitor visitor;
}

package visitor;

import symbol.SymbolTable;

/**
 * A visitor responsible for using a SymbolTable and checking for type linking errors.
 */
public class TypeLinkingVisitor extends BaseVisitor {
  private SymbolTable table;

  public TypeLinkingVisitor(SymbolTable table) {
    this.table = table;
  }
}

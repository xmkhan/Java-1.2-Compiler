package symbol;

import token.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that encapsulates a scoped-based stack for resolving symbol definitions.
 */
public class SymbolTable {
  private LinkedList<Scope<String, Symbol>> table;

  public SymbolTable() {
    table = new LinkedList<Scope<String, Symbol>>();
  }

  public SymbolTable newStack() {
    table.push(new Scope<String, Symbol>());
    return this;
  }

  public SymbolTable deleteStack() {
    table.pop();
    return this;
  }

  public SymbolTable addDecl(String identifier, Token token) {
    table.peek().add(identifier, new Symbol(token));
    return this;
  }

  public Symbol find(String identifier) {
    for (Scope<String, Symbol> scope : table) {
      if (scope.contains(identifier)) {
        return scope.find(identifier);
      }
    }
    return null;
  }

  public List<Symbol> findAll(String identifier) {
    List<Symbol> results = new ArrayList<Symbol>();
    for (Scope<String, Symbol> scope : table) {
      if (scope.contains(identifier)) {
        results.add(scope.find(identifier));
      }
    }
    return results;
  }

  public boolean contains(String identifier) {
    for (Scope<String, Symbol> scope : table) {
      if (scope.contains(identifier)) {
        return true;
      }
    }
    return false;
  }
}

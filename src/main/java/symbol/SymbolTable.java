package symbol;

import token.Declaration;

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

  public SymbolTable newScope() {
    table.push(new Scope<String, Symbol>());
    return this;
  }

  public SymbolTable deleteScope() {
    table.pop();
    return this;
  }

  public SymbolTable addDecl(String identifier, Declaration decl) {
    table.peek().add(identifier, new Symbol(decl));
    return this;
  }

  public List<Symbol> find(String identifier) {
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
        results.addAll(scope.find(identifier));
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

  public boolean containsAnyOfType(String identifier, Class clazz) {
    for (Scope<String, Symbol> scope: table) {
      if (scope.contains(identifier)) {
        for (Symbol symbol : scope.find(identifier)) {
          if (clazz.isInstance(symbol.getToken())) {
            return true;
          }
        }
      }
    }

    return false;
  }
}

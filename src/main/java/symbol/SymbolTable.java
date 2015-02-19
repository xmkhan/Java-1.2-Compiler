package symbol;

import token.Declaration;
import token.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that encapsulates a scoped-based stack for resolving symbol definitions.
 */
public class SymbolTable {
  private LinkedList<Scope<String, Token>> table;

  public SymbolTable() {
    table = new LinkedList<Scope<String, Token>>();
  }

  public SymbolTable newScope() {
    table.push(new Scope<String, Token>());
    return this;
  }

  public SymbolTable deleteScope() {
    table.pop();
    return this;
  }

  public SymbolTable addDecl(String identifier, Declaration decl) {
    table.peek().add(identifier, decl);
    return this;
  }

  public SymbolTable removeDecl(String identifier, Declaration decl) {
    table.peek().remove(identifier, decl);
    return this;
  }

  public List<Token> find(String identifier) {
    for (Scope<String, Token> scope : table) {
      if (scope.contains(identifier)) {
        return scope.find(identifier);
      }
    }
    return null;
  }

  public List<Token> findAll(String identifier) {
    List<Token> results = new ArrayList<Token>();
    for (Scope<String, Token> scope : table) {
      if (scope.contains(identifier)) {
        results.addAll(scope.find(identifier));
      }
    }
    return results;
  }

  public boolean contains(String identifier) {
    for (Scope<String, Token> scope : table) {
      if (scope.contains(identifier)) {
        return true;
      }
    }
    return false;
  }

  public boolean containsAnyOfType(String identifier, Class clazz) {
    for (Scope<String, Token> scope: table) {
      if (scope.contains(identifier)) {
        for (Token symbol : scope.find(identifier)) {
          if (clazz.isInstance(symbol)) {
            return true;
          }
        }
      }
    }

    return false;
  }
}

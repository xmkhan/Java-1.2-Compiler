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

  public void newScope() {
    table.push(new Scope<String, Token>());
  }

  public void deleteScope() {
    table.pop();
  }

  public void addDecl(String identifier, Declaration decl) {
    table.peek().add(identifier, decl);
  }

  public void removeDecl(String identifier, Declaration decl) {
    table.peek().remove(identifier, decl);
    if(table.peek().size() == 0) {
      table.pop();
    }
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
    for (Scope<String, Token> scope : table) {
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

  public boolean containsPrefix(String identifier) {
    for (Scope<String, Token> scope : table) {
      if (scope.containsPrefix(identifier)) return true;
    }
    return false;
  }

  public List<Token> findWithPrefix(String prefix) {
    List<Token> tokens = new ArrayList<Token>();
    for (Scope<String, Token> scope : table) {
      tokens.addAll(scope.findWithPrefix(prefix));
    }
    return tokens;
  }
}

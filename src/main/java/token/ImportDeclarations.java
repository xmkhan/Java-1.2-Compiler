package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ImportDeclarations extends Token {

  public ImportDeclarations(ArrayList<Token> children) {
    super("", TokenType.ImportDeclarations, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}

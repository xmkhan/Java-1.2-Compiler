package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ImportDeclarations extends Token {

  public ArrayList<Token> children;

  public ImportDeclarations(ArrayList<Token> children) {
    super("", TokenType.ImportDeclarations);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}

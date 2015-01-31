package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class CompilationUnit extends Token {

  public CompilationUnit(ArrayList<Token> children) {
    super("", TokenType.CompilationUnit, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}

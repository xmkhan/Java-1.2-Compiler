package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ForStatementNoShortIf extends BaseStatement {

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf, children);
    // To handle implicit scopes, we explicitly add the scope.
    children.add(0, new Token("{", TokenType.LEFT_BRACE));
    children.add(new Token("}", TokenType.RIGHT_BRACE));
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
}

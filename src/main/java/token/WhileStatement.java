package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class WhileStatement extends BaseWhileStatement {

  public WhileStatement(ArrayList<Token> children) {
    super("", TokenType.WhileStatement, children);
  }

}

package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public abstract class BaseWhileStatement extends BaseStatement {
  public BaseWhileStatement(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }
}

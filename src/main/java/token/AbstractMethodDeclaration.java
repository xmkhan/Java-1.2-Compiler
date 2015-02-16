package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AbstractMethodDeclaration extends Token {
  public MethodHeader methodHeader;

  public AbstractMethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.AbstractMethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
  }

  public void accept(Visitor v) throws VisitorException {
    methodHeader.accept(v);
    v.visit(this);
  }
}

package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class AbstractMethodDeclaration extends Token {

  public MethodHeader methodHeader;

  public AbstractMethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.AbstractMethodDeclaration);
    methodHeader = (MethodHeader) children.get(0);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(methodHeader);
    v.visit(this);
  }
}

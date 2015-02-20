package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarator extends Token {

  public FormalParameterList paramList;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.ConstructorDeclarator, children);
    if (children.get(2) instanceof FormalParameterList) {
      paramList = (FormalParameterList) children.get(2);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (paramList != null) paramList.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (paramList != null) paramList.acceptReverse(v);
  }
}

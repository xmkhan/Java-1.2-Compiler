package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class FormalParameterList extends Token {

  public List<FormalParameter> params;

  public List<FormalParameter> getFormalParameters() {
    return params;
  }

  public FormalParameterList(ArrayList<Token> children) {
    super("", TokenType.FormalParameterList, children);
    params = new ArrayList<FormalParameter>();
    if (children.get(0) instanceof FormalParameterList) {
      params.addAll(((FormalParameterList)children.get(0)).params);
      params.add((FormalParameter) children.get(2));
    } else {
      params.add((FormalParameter) children.get(0));
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : params) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : params) {
      token.acceptReverse(v);
    }
  }
}

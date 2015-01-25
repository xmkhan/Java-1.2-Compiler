package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InterfaceMemberDeclarations extends Token implements Visitee {

  public ArrayList<Token> children;

  public InterfaceMemberDeclarations(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclarations);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}

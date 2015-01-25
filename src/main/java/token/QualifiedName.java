package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class QualifiedName extends Token implements Visitee {

  public ArrayList<Token> children;

  public QualifiedName(ArrayList<Token> children) {
    super("", TokenType.QualifiedName);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}

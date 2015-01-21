import lexer.Lexer;
import token.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Main executable for CS444
 */
public class Main {

  public static void main(String[] args) {
    try {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(args[1]));
      Lexer lexer = new Lexer();
      ArrayList<Token> tokens = lexer.parse(reader);
    } catch (IOException | Lexer.LexerException e) {
      e.printStackTrace();
    }
  }
}

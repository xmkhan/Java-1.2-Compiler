package lexer;

import org.junit.Before;
import org.junit.Test;
import token.Token;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the Lexer
 */
public class LexerTest {
  private Lexer lexer;

  @Before
  public void setUp() {
    lexer = new Lexer();
  }

  @Test
  public void testInput1() throws IOException, Lexer.LexerException {
    testInputOutput("src/test/resources/input1", "src/test/resources/output1");
  }
  @Test
  public void testInput2() throws IOException, Lexer.LexerException {
    testInputOutput("src/test/resources/input2", "src/test/resources/output2");
  }

  private void testInputOutput(String inputFile, String outputFile) throws IOException, Lexer.LexerException {
    InputStreamReader inputReader = new InputStreamReader(new FileInputStream(inputFile), "US-ASCII");
    InputStreamReader outputReader = new InputStreamReader(new FileInputStream(outputFile), "US-ASCII");
    ArrayList<Token> tokens = lexer.parse(inputReader);
    int i = 0;
    BufferedReader bufferedReader = new BufferedReader(outputReader);
    String s;
    while ((s = bufferedReader.readLine()) != null) {
      assertEquals(s, tokens.get(i++).getLexeme());
    }

  }
}

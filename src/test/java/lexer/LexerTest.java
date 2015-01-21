package lexer;

import org.junit.Before;
import org.junit.Test;
import token.Token;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
  public void testReservedOnly() throws IOException, Lexer.LexerException {
    computeTest("src/test/resources/input1", "src/test/resources/output1");
  }

  private void computeTest(String inputFile, String outputFile) throws IOException, Lexer.LexerException {
    InputStreamReader inputReader = new InputStreamReader(new FileInputStream(inputFile));
    InputStreamReader outputReader = new InputStreamReader(new FileInputStream(outputFile));
    ArrayList<Token> tokens = lexer.parse(inputReader);
    // Skip BOF token.
    int i = 1;
    BufferedReader bufferedReader = new BufferedReader(outputReader);
    String s;
    while ((s = bufferedReader.readLine()) != null) {
      assertEquals(s, tokens.get(i++).getLexeme());
    }

  }
}

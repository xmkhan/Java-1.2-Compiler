import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InputFileGenerator
{
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
    Set<String> terminals = new HashSet<>(), nonTerminals = new HashSet<>();
    ArrayList<String> productionRules = new ArrayList<>();

    // Go through each line and determine the terminals and non terminals
    String line;
    while((line = reader.readLine()) != null) {
      if(line.equals(""))
        continue;

      productionRules.add(line);

      // Split up by space to get tokens
      String[] tokens = line.split(" ");

      // The first token is known to be a non-terminal as it is LHS
      // and assume all the other tokens which are RHS that are not in
      // /the set non-terminals are terminals even though this may not be true.
      // Tokens which are not terminals but are added as terminals will be
      // corrected later, when it is found.
      nonTerminals.add(tokens[0]);
      for (int a = 1; a < tokens.length; a++) {
        if(!nonTerminals.contains(tokens[a])) {
          terminals.add(tokens[a]);
        }
      }

      // As we learn about more tokens that are non-terminals
      // check if we added previously as a terminal, if so remove
      // them.
      if(terminals.contains(tokens[0])) {
        terminals.remove(tokens[0]);
      }
    }

    PrintWriter writer = new PrintWriter(new FileWriter(new File(args[1])));

    // Print terminals
    writer.println(terminals.size());
    for (String terminal : terminals) {
      writer.println(terminal);
    }

    // Print non-terminals
    writer.println(nonTerminals.size());
    for (String nonTerminal : nonTerminals) {
      writer.println(nonTerminal);
    }

    // Print Start symbol
    writer.println("Program");

    // Print production rule
    writer.println(productionRules.size());
    for (String productionRule : productionRules) {
      writer.println(productionRule);
    }

    writer.close();
    reader.close();
  }
}

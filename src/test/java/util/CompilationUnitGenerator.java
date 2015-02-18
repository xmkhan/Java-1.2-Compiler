package util;

import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.CompilerException;
import lexer.Lexer;
import token.CompilationUnit;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstracts the part of Main.java that reads in a list of files and produces the CompilationUnits.
 */
public class CompilationUnitGenerator {

  public static List<CompilationUnit> make(String[] filePaths) throws IOException, CompilerException {
    return make(Arrays.asList(filePaths));
  }

  public static List<CompilationUnit> make(List<String> filePaths) throws IOException, CompilerException {
    InputStreamReader lr1Reader = new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE));

    Lexer lexer = new Lexer();
    ShiftReduceAlgorithm shiftReduceAlgorithm = new ShiftReduceAlgorithm(lr1Reader);

    List<CompilationUnit> units = new ArrayList<CompilationUnit>(filePaths.size());


    return units;
  }
}

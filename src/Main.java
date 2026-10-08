import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

public class Main {
   public static void main(String[] args) {
      if (args.length != 1) {
        System.err.println("Please add the .sel pathfile in the Makefile.");
        System.exit(1);
      }

      try (Reader reader = new FileReader(args[0])) {
         LexicalAnalyzer scanner = new LexicalAnalyzer(reader);

         Map<String, Integer> variablesEncountered = new TreeMap<>();
         Symbol currentSymbol = scanner.nextToken();
         while (currentSymbol.getType() != LexicalUnit.EOS) {
            if (currentSymbol.getType() == LexicalUnit.VARNAME) {
               variablesEncountered.putIfAbsent(currentSymbol.getValue().toString(), currentSymbol.getLine());
            }
         System.out.println(currentSymbol); // Println use directly the toString() method overwriten, no need to call it
         currentSymbol = scanner.nextToken();
      }
      showVariables(variablesEncountered);
      
      }  catch (RuntimeException error) {
         System.err.println("Lexical error: " + error.getMessage());
         System.exit(1);
      }  catch (IOException error) {
         System.err.println("Can't read file: " + error.getMessage());
         System.exit(1);
      }
   }

   private static void showVariables(Map<String, Integer> variablesEncountered) {
      System.out.println("\nVariables");
      for (Map.Entry<String, Integer> var : variablesEncountered.entrySet()) {
         String line = var.getKey() + " " + var.getValue(); 
         System.out.println(line);
      }
   }  
}
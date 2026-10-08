import java.io.FileReader;
import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

public class Main {
   public static void main(String[] args) throws Exception {
      Reader reader = new FileReader(args[0]);
      LexicalAnalyzer scanner = new LexicalAnalyzer(reader);

      TreeMap<String, Integer> variablesEncountered = new TreeMap<>();
      Symbol currentSymbol = scanner.nextToken();
      while (currentSymbol.getType() != LexicalUnit.EOS) {
         if (currentSymbol.getType() == LexicalUnit.VARNAME && !variablesEncountered.containsKey(currentSymbol.getValue().toString())) {
            variablesEncountered.put(currentSymbol.getValue().toString(), currentSymbol.getLine());
         }
         System.out.println(currentSymbol.toString());
         currentSymbol = scanner.nextToken();
      }
      
      showVariables(variablesEncountered);

      reader.close();
   }

   private static void showVariables(TreeMap<String, Integer> variablesEncountered) {
      System.out.println("\nVariables:");
      for (Map.Entry<String, Integer> var : variablesEncountered.entrySet()) {
         String line = var.getKey() + " " + var.getValue();
         System.out.println(line);
      }
   }
      
}
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main {
   public static void main(String[] args) throws Exception {
      Reader reader = new FileReader(args[0]);
      LexicalAnalyzer scanner = new LexicalAnalyzer(reader);

      List<Symbol> variables = new ArrayList<>();
      Symbol currentSymbol = scanner.nextToken();
      while (currentSymbol.getType() != LexicalUnit.EOS) {
         if (currentSymbol.getType() == LexicalUnit.VARNAME) {
            variables.add(currentSymbol);
         }
         System.out.println(currentSymbol.toString());
         currentSymbol = scanner.nextToken();
      }
      
      showVariables(variables);

      reader.close();
   }

   private static void showVariables(List<Symbol> variables) {
      System.out.println("\nVariables:");
      for (Symbol var : variables) {
         String line = var.getValue().toString()+ " " + var.getLine();
         System.out.println(line);
      }
   }
      
}
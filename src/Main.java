import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Main class for the SEL compiler scanner.
 */
public class Main {
   /**
    * Program entry point.
    * @param args args[0] must be the sourceFile.sel path.
    * java -jar part2.jar sourceFile.sel                     → args = ["sourceFile.sel"]  si args.length =1 , args[0] -> sourceFile.sel
    * java -jar part2.jar -wt sourceFile.tex sourceFile.sel  → args = ["-wt", "sourceFile.tex", "sourceFile.sel"] si args.length =3 , args[2] -> sourceFile.sel
   */
   public static void main(String[] args) {
      int u = 0;
      if (args.length < 1 || args.length== 2) {
        System.err.println("Please add the .sel pathfile in the Makefile.");
        System.exit(1);
      }
      if (args.length >3){
         System.err.println("Too much File");
         System.exit(1);
      }
      if (args.length == 3){
         if (!args[0].equals("-wt")) {
            System.err.println("Not good option");
            System.exit(1);
         }

         u =2;
      }

      try (Reader reader = new FileReader(args[u])) {
         LexicalAnalyzer scanner = new LexicalAnalyzer(reader);
         Parser parser = new Parser(reader,scanner);

         ParseTree parseTree = parser.parse();
         List<Integer> rules = parser.getRules();

         for (int i=0 ; i< rules.size();i++) {
            System.out.print(rules.get(i) + " ");
         }
         System.out.println(); // retour a la ligne

         /// Seulement quand l'option -wt est donnée
      if (u ==2) {
         try (FileWriter writer = new FileWriter(args[1])) {
            writer.write(parseTree.toLaTeX());
         }
      }

      }  catch (RuntimeException error) { // Catching errors defined in .flex file
         System.err.println("Lexical error: " + error.getMessage());
         System.exit(1);
      }  catch (IOException error) {
         System.err.println("Can't read file: " + error.getMessage());
         System.exit(1);
      }
   }

   /**
   * Displays all variables encountered and their first line of appearance.
   * @param variablesEncountered a map of variable names to their first line number
   */
   private static void showVariables(Map<String, Integer> variablesEncountered) {
      System.out.println("\nVariables");
      for (Map.Entry<String, Integer> var : variablesEncountered.entrySet()) {
         String line = var.getKey() + " " + var.getValue(); // Forming the string to print required
         System.out.println(line);
      }
   }  
}
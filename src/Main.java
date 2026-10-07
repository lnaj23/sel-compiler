import java.io.FileReader;
import java.io.Reader;

public class Main {
    public static void main(String[] args) throws Exception {
        Reader reader = new FileReader(args[0]);
        LexicalAnalyzer scanner = new LexicalAnalyzer(reader);

        Symbol s = scanner.nextToken();
        while (s.getType() != LexicalUnit.EOS) {
            System.out.println(s.toString());
            s = scanner.nextToken();
        }
        reader.close();
    }
}
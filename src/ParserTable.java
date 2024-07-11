import java.util.Map;

public class ParserTable {

    Action[][] actionTable;
    int[][] gotoTable;

    Map<Terminal, Integer> terminalToIdx;
    Map<NonTerminal, Integer> nonTerminalToIdx;
    Grammar grammar;

    public ParserTable(Action[][] actionTable, int[][] gotoTable, Map<Terminal, Integer> terminalToIdx, Map<NonTerminal, Integer> nonTerminalToIdx, Grammar grammar){
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        this.terminalToIdx = terminalToIdx;
        this.nonTerminalToIdx = nonTerminalToIdx;
        this.grammar = grammar;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();

        int fixedWidth = 10; // Fixed width for each column

        // Create a format string for each row
        String format = "| %" + fixedWidth + "s ";
        String rowFormat = format.repeat(grammar.terminals.size() + grammar.nonTerminals.size() + 1) + "|\n";

        // Header row
        String[] headers = new String[grammar.terminals.size() + grammar.nonTerminals.size() + 1];
        headers[0] = ""; // The first cell is empty for the row headers
        for (int i = 0; i < grammar.terminals.size(); i++) {
            headers[i + 1] = grammar.terminals.get(i).toString();
        }
        for (int i = 0; i < grammar.nonTerminals.size(); i++) {
            headers[grammar.terminals.size() + i + 1] = grammar.nonTerminals.get(i).toString();
        }
        res.append(String.format(rowFormat, (Object[]) headers));

        // Generate the table rows
        for (int i = 0; i < actionTable.length; i++) {
            String[] row = new String[grammar.terminals.size() + grammar.nonTerminals.size() + 1];
            row[0] = String.valueOf(i);
            for (int j = 0; j < grammar.terminals.size(); j++) {
                Action action = actionTable[i][j];
                row[j + 1] = (action == null ? " " : action.toString());
            }
            for (int j = 0; j < grammar.nonTerminals.size(); j++) {
                row[grammar.terminals.size() + j + 1] = (gotoTable[i][j] == -1 ? " " : String.valueOf(gotoTable[i][j]));
            }
            res.append(String.format(rowFormat, (Object[]) row));
        }

        return res.toString();
    }
}

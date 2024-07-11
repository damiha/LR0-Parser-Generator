
// the actual parser generator

import java.util.*;

public class LRZeroTableGenerator {

    boolean printDebug = false;
    int stateCounter = 0;
    int maxIterations = 1_000;

    Map<ConfigurationSet, Integer> configurationToId;

    public ParserTable createActionAndGotoTable(Grammar grammar){

        // register each new configuration state with a new number
        configurationToId = new HashMap<>();

        log("add new start symbol S'");
        augmentGrammar(grammar);

        List<ConfigurationSet> configurationSets = new ArrayList<>();
        List<ConfigurationSet> workSet = new ArrayList<>();

        Set<Item> c0Set = new HashSet<>(Set.of(new Item(grammar.startSymbol, grammar.productions.getFirst().rHs, 0)));
        closure(c0Set, grammar);

        ConfigurationSet c0 = registerAndGetConfigurationSet(c0Set).get();
        workSet.add(c0);

        Map<Pair<ConfigurationSet, Symbol>, ConfigurationSet> successorFunction = new HashMap<>();

        int nIterations = 0;
        while(!workSet.isEmpty() && nIterations < maxIterations) {

            ConfigurationSet current = workSet.removeFirst();

            if(configurationSets.contains(current)){
                continue;
            }

            configurationSets.add(current);

            // assume elements of the work set already had the closure operation applied to them
            //closure(current, grammar);

            log(current.toString());

            for(Symbol symbol : grammar.getSymbols()) {

                Optional<ConfigurationSet> successor = getSuccessor(current, symbol, grammar);

                if(successor.isEmpty()){
                    continue;
                }

                Pair<ConfigurationSet, Symbol> key = new Pair<>(current, symbol);

                successorFunction.put(key, successor.get());

                if(!configurationSets.contains(successor.get()) && !workSet.contains(successor.get())) {
                    workSet.add(successor.get());
                }
            }

            nIterations++;
        }

        return createGotoAndActionTable(configurationSets, successorFunction, grammar);
    }

    private ParserTable createGotoAndActionTable(List<ConfigurationSet> configurationSets,
                                                               Map<Pair<ConfigurationSet, Symbol>, ConfigurationSet> successor, Grammar grammar){

        int nStates = configurationSets.size();

        // add the terminal $ for eof
        grammar.terminals.addLast(new Terminal("$"));

        // for the parser table
        Action[][] actionTable = new Action[nStates][grammar.terminals.size()];
        int[][] gotoTable = new int[nStates][grammar.nonTerminals.size()];
        Map<Terminal, Integer> terminalToIdx = new HashMap<>();
        Map<NonTerminal, Integer> nonTerminalToIdx = new HashMap<>();

        int idx = 0;
        for(Terminal terminal : grammar.terminals){
            terminalToIdx.put(terminal, idx);
            idx++;
        }

        idx = 0;
        for(NonTerminal nonTerminal : grammar.nonTerminals){
            nonTerminalToIdx.put(nonTerminal, idx);
            idx++;
        }

        for(int i = 0; i < configurationSets.size(); i++){

            ConfigurationSet stateI = configurationSets.get(i);

            for(Item item : stateI.items){

                // we have the case A -> u#
                // in the case A is not E', we know that for LR(0), this will only be one such configuration

                // so it can't be that A -> u# and A -> v# for LR(0), we would have a reduce-reduce conflict
                if(item.isDotAtEnd()){

                    if(item.lHs.equals(grammar.startSymbol)){

                        // we have parsed the entire (have S*) on the stack
                        // now we expect that there's no more input so eof
                        actionTable[i][grammar.terminals.size() - 1] = new Action.Accept();
                    }

                    else{
                        // we can assume we have no reduce-reduce conflicts or shift-reduce conflicts,
                        // so we can set the entire row to reduce

                        // we have to reduce with a production rule
                        int prodId = grammar.getProdId(item);

                        for(int j = 0; j < grammar.terminals.size(); j++){

                            // stateI is an item so is like a production rule with a dot
                            // we need to find the production rule number
                            actionTable[i][j] = new Action.Reduce(prodId);
                        }
                    }
                }
                else if(item.isTerminalAfterDot()){
                    // we aren't finished, so we can't reduce
                    // we need to shift further
                    Terminal toShift = item.terminalAfterDot();

                    int tIndex = terminalToIdx.get(toShift);

                    ConfigurationSet nextConfiguration = successor.get(new Pair<>(stateI, toShift));

                    // TODO: use hash tables for this
                    int nextConfigurationIdx = configurationSets.indexOf(nextConfiguration);

                    // shift needs to know the next state
                    actionTable[i][tIndex] = new Action.Shift(nextConfigurationIdx);
                }
                else{
                    // there must be a something after the dot because we are not done
                    // the something must be a Non-terminal
                    NonTerminal nonTerminal = item.nonTerminalAfterDot();
                    int nIndex = nonTerminalToIdx.get(nonTerminal);

                    ConfigurationSet nextConfiguration = successor.get(new Pair<>(stateI, nonTerminal));

                    // TODO: use hash tables for this
                    int nextConfigurationIdx = configurationSets.indexOf(nextConfiguration);

                    gotoTable[i][nIndex] = nextConfigurationIdx;
                }
            }
        }

        return new ParserTable(actionTable, gotoTable, terminalToIdx, nonTerminalToIdx, grammar);
    }

    private Optional<ConfigurationSet> registerAndGetConfigurationSet(Set<Item> items){

        // add and increment the counter only if the configuration set has items
        if(items.isEmpty()){
            return Optional.empty();
        }

        ConfigurationSet configurationSet = new ConfigurationSet(items);

        if(configurationToId.containsKey(configurationSet)){
            configurationSet.stateId = configurationToId.get(configurationSet);
        }
        else{
            configurationSet.stateId = stateCounter;
            configurationToId.put(configurationSet, stateCounter++);
        }
        return Optional.of(configurationSet);
    }

    private Optional<ConfigurationSet> getSuccessor(ConfigurationSet configurationSet, Symbol symbolAfterDot, Grammar grammar){

        log(String.format("--- SUCCESSOR <I_%d, %s> ---", configurationSet.stateId, symbolAfterDot));

        Set<Item> successorItems = new HashSet<>();

        for(Item item : configurationSet.items){
            if(symbolAfterDot.equals(item.symbolAfterDot())){

                Item afterAdvance = advanceDot(item);

                if(afterAdvance != null) {
                    successorItems.add(afterAdvance);
                }
            }
        }

        closure(successorItems, grammar);

        return registerAndGetConfigurationSet(successorItems);
    }

    // should create a deep copy
    // if it cannot be advanced, returns null
    private Item advanceDot(Item item){

        if(item.dotPosition == item.rHs.size()){
            return null;
        }

        // lHs and rHs are never modified so that's fine?
        return new Item(item.lHs, item.rHs, item.dotPosition + 1);
    }

    private void closure(Set<Item> items, Grammar grammar){

        log("--- CLOSURE ---");

        List<Item> workSet = new ArrayList<>(items);

        while(!workSet.isEmpty()){

            Item current = workSet.removeFirst();
            log(String.format("Working on item %s", current.toString()));

            if(dotBeforeNonTerminal(current)){

                List<Production> toAdd = getProductionsStartingWith(current.nonTerminalAfterDot(), grammar);

                List<Item> itemsToAdd = getAsStartingItems(toAdd);

                for(Item item : itemsToAdd){
                    if(!items.contains(item)){
                        log(String.format("Adding item %s", item.toString()));

                        items.add(item);
                        workSet.add(item);
                    }
                }
            }
        }
    }

    private List<Production> getProductionsStartingWith(NonTerminal nonTerminal, Grammar grammar){
        return grammar.productions.stream().filter(p -> p.lHs.equals(nonTerminal)).toList();
    }

    private List<Item> getAsStartingItems(List<Production> productions){
        return productions.stream().map(p -> new Item(p.lHs, p.rHs, 0)).toList();
    }

    private boolean dotBeforeNonTerminal(Item item){

        int dotPosition = item.dotPosition;
        return (dotPosition < item.rHs.size() && item.rHs.get(dotPosition) instanceof NonTerminal);
    }

    private void log(String message){
        if(printDebug){
            System.out.printf("[DEBUG]: %s\n", message);
        }
    }

    // be careful, this is in-place
    private void augmentGrammar(Grammar grammar){
        NonTerminal newStartSymbol = new NonTerminal("S'");

        NonTerminal oldStartSymbol = grammar.startSymbol;

        grammar.productions.addFirst(new Production(newStartSymbol, List.of(oldStartSymbol)));

        grammar.startSymbol = newStartSymbol;
    }
}

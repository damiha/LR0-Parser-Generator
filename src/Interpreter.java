public class Interpreter {

    int interpret(ASTNode node){

        if(node instanceof ASTNode.Leaf leaf){

            // id is interpreted as one for now
            return 1;
        }
        else if(node instanceof ASTNode.Inner inner){

            String prodString = inner.head.toString();

            if(prodString.equals("E -> E+T")){

                int resLeft = interpret(inner.children.get(0));
                int resRight = interpret(inner.children.get(1));

                return resLeft + resRight;
            }
            else if(prodString.equals("E -> T")){
                return interpret(inner.children.getFirst());
            }
            else if(prodString.equals("T -> (E)")){

                // 0 and 2 are left and right parentheses
                return interpret(inner.children.get(1));
            }
            else if(prodString.equals("T -> id")){
                return interpret(inner.children.getFirst());
            }
            return -1;
        }
        else{
            throw new RuntimeException("Node type unknown");
        }
    }
}

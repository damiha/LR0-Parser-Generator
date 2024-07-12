
# LR(0) Parser Generator

- constructs a parse table given a context-free grammar (CFG) that is in LR(0)
- the parse table is used by a table-driven [LR(0) parser](https://en.wikipedia.org/wiki/LR_parser)
- the result is an abstract syntax tree (AST) / derivation tree. Each AST node is annotated with the production that was used.
- implemented it more or less directly from [this](https://web.stanford.edu/class/archive/cs/cs143/cs143.1128/handouts/100%20Bottom-Up%20Parsing.pdf) awesome Stanford material

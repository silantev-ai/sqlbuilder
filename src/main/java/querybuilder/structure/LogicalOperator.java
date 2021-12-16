package querybuilder.structure;

import querybuilder.structure.enums.Logical;

public interface LogicalOperator extends WhereToken {
    Logical getOperator();

    // region API methods
    OpenBracket openBracket();
    Statement statement(Statement statement);
    // end region
}

package querybuilder.structure;

import querybuilder.structure.enums.Logical;

import java.util.function.BiFunction;

public interface LogicalOperator extends WhereToken {
    Logical getOperator();

    // region API methods
    OpenBracket openBracket();
    Statement statement(Statement statement);
    <T> Statement statement(BiFunction<Where, T, Statement> func, T value);
    // end region
}

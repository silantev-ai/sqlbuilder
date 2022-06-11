package querybuilder.structure;


import querybuilder.structure.enums.Logical;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface Where {
    // region API methods
    OpenBracket openBracket();
    Statement statement(Statement statement);
    QueryBuilder endWhere();
    default QueryBuilder endHaving() {
        return endWhere();
    }
    Statement join(List<Statement> statements, Logical operator);
    // endregion

    // region internal methods
    Deque<WhereToken> getElements();
    QueryBuilder getQueryBuilder();
    AtomicInteger getBracketsCounter();
    // endregion
}

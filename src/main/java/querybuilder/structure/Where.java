package querybuilder.structure;


import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

public interface Where {
    // region API methods
    OpenBracket openBracket();
    Statement statement(Statement statement);
    QueryBuilder endWhere();
    // endregion

    // region internal methods
    Deque<WhereToken> getElements();
    QueryBuilder getQueryBuilder();
    AtomicInteger getBracketsCounter();
    // endregion
}

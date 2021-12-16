package querybuilder.structure.Impl;


import querybuilder.structure.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class WhereImpl implements Where {
    private final Deque<WhereToken> whereTokens;
    private final QueryBuilder queryBuilder;
    private final AtomicInteger bracketsCounter;

    public WhereImpl(QueryBuilder queryBuilder) {
        whereTokens = new LinkedList<>();
        this.queryBuilder = queryBuilder;
        bracketsCounter = new AtomicInteger();
    }

    @Override
    public Deque<WhereToken> getElements() {
        return whereTokens;
    }

    @Override
    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    @Override
    public AtomicInteger getBracketsCounter() {
        return bracketsCounter;
    }

    @Override
    public OpenBracket openBracket() {
        return ExprFactory.openBracket(this);
    }

    @Override
    public Statement statement(Statement statement) {
        return ExprFactory.statement(this, statement);
    }

    @Override
    public QueryBuilder endWhere() {
        if (bracketsCounter.get() != 0) {
            throw new RuntimeException("Syntax error. Check opened/closed brackets");
        }

        if (whereTokens.size() == 1 && !(whereTokens.getFirst() instanceof Statement)) {
            throw new RuntimeException("Invalid where block");
        }
        return queryBuilder;
    }
}

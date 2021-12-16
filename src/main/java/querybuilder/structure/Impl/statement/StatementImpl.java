package querybuilder.structure.Impl.statement;

import querybuilder.structure.*;
import querybuilder.structure.Impl.ExprFactory;
import querybuilder.structure.enums.Logical;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public class StatementImpl implements Statement {
    private Where where;
    protected final String tableAlias;
    protected final String field;
    protected final Object value;

    public StatementImpl(String tableAlias, String field, Object value) {
        this.field = field;
        this.value = value;
        this.tableAlias = tableAlias;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
        // dummy expression if value is not present
        if (value == null) {
            return cb.equal(cb.literal(1), 1);
        }
        return null;
    }

    @Override
    public String getAlias() {
        return tableAlias;
    }

    @Override
    public void setWhere(Where where) {
        this.where = where;
    }

    @Override
    public LogicalOperator operator(Logical operator) {
        return ExprFactory.operator(where, operator);
    }

    @Override
    public CloseBracket closeBracket() {
        return ExprFactory.closeBracket(where);
    }

    @Override
    public QueryBuilder endWhere() {
        return where.endWhere();
    }
}

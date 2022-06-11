package querybuilder.structure.Impl.statement;

import querybuilder.structure.*;
import querybuilder.structure.Impl.SqlStructureFactory;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.enums.Logical;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.function.BiFunction;
import java.util.function.Function;

class StatementImpl implements Statement {
    private Where where;
    protected final BiFunction<CriteriaBuilder, Function<Table, From<?, ?>>, Predicate> predicateFunction;
    protected final boolean valueIsNull;

    public StatementImpl(BiFunction<CriteriaBuilder, Function<Table, From<?, ?>>, Predicate> predicateFunction, boolean valueIsNull) {
        this.predicateFunction = predicateFunction;
        this.valueIsNull = valueIsNull;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        // dummy expression if value is not present
        if (valueIsNull) {
            return cb.equal(cb.literal(1), 1);
        }
        return predicateFunction.apply(cb, sourceSupplier);
    }

    @Override
    public void setWhere(Where where) {
        this.where = where;
    }

    @Override
    public LogicalOperator operator(Logical operator) {
        return SqlStructureFactory.operator(where, operator);
    }

    @Override
    public CloseBracket closeBracket() {
        return SqlStructureFactory.closeBracket(where);
    }

    @Override
    public QueryBuilder endWhere() {
        return where.endWhere();
    }
}

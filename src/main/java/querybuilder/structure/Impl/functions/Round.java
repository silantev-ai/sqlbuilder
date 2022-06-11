package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.SqlSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.function.Function;

class Round implements SqlSelection, SqlExpression {
    protected final SqlExpression expression;
    protected final String alias;

    public Round(SqlExpression expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }
    @Override
    public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return (Expression<T>) cb.function("round", Integer.class, expression.getExpression(cb, sourceSupplier));
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier).alias(alias);
    }
}

package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.SqlSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.function.Function;

public abstract class AbstractOperation<X, Y> implements SqlSelection, SqlExpression {
    protected final X x;
    protected final Y y;
    protected final String alias;
    private final Class<? extends Number> castType;

    public AbstractOperation(X x, Y y, String alias, Class<? extends Number> castType) {
        this.x = x;
        this.y = y;
        this.alias = alias;
        this.castType = castType;
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier).alias(alias);
    }

    @SuppressWarnings("unchecked")
    protected Expression<? extends Number> getExpression(Object operand, CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        Expression<Number> expression;
        if (operand instanceof SqlSelection) {
            expression = (Expression<Number>) ((SqlSelection) operand).getSelection(cb, sourceSupplier);
        } else {
            expression = cb.literal((Number) operand);
        }
        return expression;
    }

    protected Expression<? extends Number> cast(Expression<Number> expr) {
        return castType == null ? expr : expr.as(castType);
    }
}

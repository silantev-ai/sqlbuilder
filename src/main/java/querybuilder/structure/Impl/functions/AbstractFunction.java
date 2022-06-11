package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import java.util.function.Function;

public abstract class AbstractFunction implements SqlExpression {
    protected Field field;

    public AbstractFunction(Field field) {
        this.field = field;
    }

    protected <Y> Path<Y> getPath(Function<Table, From<?, ?>> sourceSupplier) {
        return sourceSupplier.apply(field.getTable()).get(field.getField());
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier).alias(field.getFieldAlias());
    }
}

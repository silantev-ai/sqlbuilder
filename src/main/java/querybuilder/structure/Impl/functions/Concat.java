package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.SqlSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

class Concat implements SqlSelection, SqlExpression {
    private final Object[] parts;

    public Concat(Object...parts) {
        this.parts = parts;
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier);
    }

    @Override
    public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        List<Expression<String>> partExpressions = new ArrayList<>();
        for (Object part : parts) {
            if (part instanceof Field) {
                partExpressions.add((Expression<String>) ((Field) part).getSelection(cb, sourceSupplier));
            } else {
                partExpressions.add(cb.literal((String) part));
            }
        }
        return (Expression<T>) partExpressions.stream().reduce(cb.literal(""), cb::concat);
    }
}

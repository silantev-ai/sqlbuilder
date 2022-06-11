package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.SqlSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

class JsonbExtractPathFunction implements SqlSelection, SqlExpression {
    protected final Field field;
    private final String[] jsonPath;

    public JsonbExtractPathFunction(Field field, String... jsonPath) {
        this.field = field;
        this.jsonPath = jsonPath;
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier);
    }

    @Override
    public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return (Expression<T>) cb.function("jsonb_extract_path_text", String.class, getFunctionArgs(cb, sourceSupplier));
    }

    private Expression<?>[] getFunctionArgs(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return Stream.concat(
                Arrays.stream(new Expression[] { sourceSupplier.apply(field.getTable()).get(field.getField()) }),
                Arrays.stream(jsonPath).map(cb::literal)
        ).toArray(Expression[]::new);
    }
}

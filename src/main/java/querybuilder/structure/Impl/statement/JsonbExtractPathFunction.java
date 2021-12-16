package querybuilder.structure.Impl.statement;

import querybuilder.structure.enums.SqlOperator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.stream.Stream;

public class JsonbExtractPathFunction extends StatementImpl {
    private final String[] jsonPath;
    private final SqlOperator operator;

    public JsonbExtractPathFunction(String tableAlias, String field, String value, SqlOperator operator, String... jsonPath) {
        super(tableAlias, field, value);
        this.jsonPath = jsonPath;
        this.operator = operator;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
        Expression<String> jsonbExtractPathTextFunc = cb.function(
                "jsonb_extract_path_text", String.class, getFunctionArgs(cb, source));

        if (value == null) {
            return super.getPredicate(cb, source);
        }

        switch (operator) {
            case NOT_EQUAL:
                return cb.notEqual(jsonbExtractPathTextFunc, value);
            case LIKE:
                return cb.like(jsonbExtractPathTextFunc, "%" + value + "%");
            case STARTS_WITH:
                return cb.like(jsonbExtractPathTextFunc, value + "%");
            case ENDS_WITH:
                return cb.like(jsonbExtractPathTextFunc, "%" + value);
            default:
                return cb.equal(jsonbExtractPathTextFunc, value);
        }
    }

    private Expression<?>[] getFunctionArgs(CriteriaBuilder cb, From<?, ?> source) {
        return Stream.concat(
                Arrays.stream(new Expression[] { source.get(field) }),
                Arrays.stream(jsonPath).map(cb::literal)
        ).toArray(Expression[]::new);
    }
}

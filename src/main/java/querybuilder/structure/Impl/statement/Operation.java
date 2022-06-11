package querybuilder.structure.Impl.statement;

import dto.OperatorAndValue;
import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.functions.SqlFunc;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.Statement;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Operation {
    // region comparison

    public static Statement reference(Field lft, Field rght) {
        return new StatementImpl((cb, sourceSupplier) -> cb.equal(lft.getExpression(cb, sourceSupplier), rght.getExpression(cb, sourceSupplier)), false);
    }

    public static Statement equal(SqlExpression sqlExpr, Object value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.equal(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement notEqual(SqlExpression sqlExpr, Object value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.notEqual(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement gt(SqlExpression sqlExpr, Number value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.gt(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement gte(SqlExpression sqlExpr, Number value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.ge(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement lt(SqlExpression sqlExpr, Number value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.lt(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement lte(SqlExpression sqlExpr, Number value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.le(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement gt(SqlExpression sqlExpr, Date value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.greaterThan(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement gte(SqlExpression sqlExpr, Date value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.greaterThanOrEqualTo(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement lt(SqlExpression sqlExpr, Date value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.lessThan(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    public static Statement lte(SqlExpression sqlExpr, Date value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.lessThanOrEqualTo(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }
    // endregion

    // region string operation
    public static Statement like(SqlExpression sqlExpr, String value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.like(sqlExpr.getExpression(cb, sourceSupplier), value), value == null);
    }

    /**
     * Sql will be:
     * ... lower(table0.field0) like lower(?) ...
     */
    public static Statement ilike(SqlExpression sqlExpr, String value) {
        String formattedValue = iLikeParam(value);
        return new StatementImpl((cb, sourceSupplier) -> cb.like(cb.lower(sqlExpr.getExpression(cb, sourceSupplier)), cb.lower(cb.literal(formattedValue))), formattedValue == null);
    }

    public static Statement startsWith(SqlExpression sqlExpr, String value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.like(sqlExpr.getExpression(cb, sourceSupplier), value + "%"), value == null);
    }

    public static Statement endsWith(SqlExpression sqlExpr, String value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.like(sqlExpr.getExpression(cb, sourceSupplier), "%" + value), value == null);
    }

    public static Statement contains(SqlExpression sqlExpr, String value) {
        return new StatementImpl((cb, sourceSupplier) -> cb.like(sqlExpr.getExpression(cb, sourceSupplier), "%" + value + "%"), value == null);
    }
    // endregion

    // region null
    public static Statement isNull(SqlExpression sqlExpr) {
        return new StatementImpl((cb, sourceSupplier) -> cb.isNull(sqlExpr.getExpression(cb, sourceSupplier)), false);
    }

    public static Statement isNotNull(SqlExpression sqlExpr) {
        return new StatementImpl((cb, sourceSupplier) -> cb.isNotNull(sqlExpr.getExpression(cb, sourceSupplier)), false);
    }
    // endregion

    // region functions

    public static Statement in(SqlExpression sqlExpr, List<?> value) {
        return new StatementImpl((cb, sourceSupplier) -> sqlExpr.getExpression(cb, sourceSupplier).in(value), value == null);
    }

    public static Statement jsonbExtractPathFunction(Field field, OperatorAndValue<String> operatorAndValue, String... jsonPath) {
        SqlExpression jsonbFunc = SqlFunc.jsonbExtractPathFunction(field, jsonPath);
        return variantOperatorForString(jsonbFunc, operatorAndValue);
    }
    // endregion

    // region variant operators
    public static Statement variantOperatorForString(SqlExpression sqlExpr, OperatorAndValue<String> operatorAndValue) {

        if (operatorAndValue == null || operatorAndValue.getOperator() == null) return equal(sqlExpr, null);

        switch (operatorAndValue.getOperator()) {
            case STARTS_WITH:
                return startsWith(sqlExpr, operatorAndValue.getValue());
            case CONTAINS:
                return contains(sqlExpr, operatorAndValue.getValue());
            case ICONTAINS:
                return ilike(sqlExpr, operatorAndValue.getValue());
            case ENDS_WITH:
                return endsWith(sqlExpr, operatorAndValue.getValue());
            case NOT_EQUAL:
                return notEqual(sqlExpr, operatorAndValue.getValue());
            case IS_NULL:
                return isNull(sqlExpr);
            case IS_NOT_NULL:
                return isNotNull(sqlExpr);
            case IN:
                return in(sqlExpr, operatorAndValue.getValues());
            default:
                return equal(sqlExpr, operatorAndValue.getValue());
        }
    }

    public static <T extends Number> Statement variantOperatorForNumber(SqlExpression sqlExpr, OperatorAndValue<T> operatorAndValue) {

        if (operatorAndValue == null || operatorAndValue.getOperator() == null) return equal(sqlExpr, null);

        switch (operatorAndValue.getOperator()) {
            case LT:
                return lt(sqlExpr, operatorAndValue.getValue());
            case LTE:
                return lte(sqlExpr, operatorAndValue.getValue());
            case GT:
                return gt(sqlExpr, operatorAndValue.getValue());
            case GTE:
                return gte(sqlExpr, operatorAndValue.getValue());
            case NOT_EQUAL:
                return notEqual(sqlExpr, operatorAndValue.getValue());
            case IS_NULL:
                return isNull(sqlExpr);
            case IS_NOT_NULL:
                return isNotNull(sqlExpr);
            case IN:
                return in(sqlExpr, operatorAndValue.getValues());
            default:
                return equal(sqlExpr, operatorAndValue.getValue());
        }
    }

    public static Statement variantOperatorForDate(SqlExpression sqlExpr, OperatorAndValue<Date> operatorAndValue) {

        if (operatorAndValue == null || operatorAndValue.getOperator() == null) return equal(sqlExpr, null);

        switch (operatorAndValue.getOperator()) {
            case LT:
                return lt(sqlExpr, operatorAndValue.getValue());
            case LTE:
                return lte(sqlExpr, operatorAndValue.getValue());
            case GT:
                return gt(sqlExpr, operatorAndValue.getValue());
            case GTE:
                return gte(sqlExpr, operatorAndValue.getValue());
            case IS_NULL:
                return isNull(sqlExpr);
            case IS_NOT_NULL:
                return isNotNull(sqlExpr);
            case NOT_EQUAL:
                return notEqual(sqlExpr, operatorAndValue.getValue());
            default:
                return equal(sqlExpr, operatorAndValue.getValue());
        }
    }
    // endregion

    public static String iLikeParam(String value) {
        if (null == value || value.isBlank()) {
            return null;
        }
        if ("%".equals(value)) {
            return value;
        }
        return '%' + value.toLowerCase(Locale.ROOT) + '%';
    }
}

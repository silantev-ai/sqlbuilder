package querybuilder.structure.Impl.functions;

import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.SqlSelection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.util.function.Function;


public class SqlFunc {
    public static SqlExpression count(String attr) {
        return count(Field.of(attr));
    }

    public static SqlExpression countDistinct(String attr) {
        return countDistinct(Field.of(attr));
    }

    public static SqlExpression avg(String attr) {
        return avg(Field.of(attr));
    }

    public static SqlExpression max(String attr) {
        return max(Field.of(attr));
    }

    public static SqlExpression min(String attr) {
        return min(Field.of(attr));
    }

    public static SqlExpression sum(String attr) {
        return sum(Field.of(attr));
    }

    public static <X, Y> SqlSelection add(X x, Y y) {
        return add(x, y, null, null);
    }

    public static <X, Y> SqlSelection sub(X x, Y y) {
        return sub(x, y, null, null);
    }

    public static <X, Y> SqlSelection multiply(X x, Y y) {
        return multiply(x, y, null, null);
    }

    public static <X, Y> SqlSelection div(X x, Y y) {
        return div(x, y, null, null);
    }


    public static SqlExpression count(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.count(getPath(sourceSupplier));
            }
        };
    }

    public static SqlExpression countDistinct(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.countDistinct(getPath(sourceSupplier));
            }
        };
    }

    public static SqlExpression avg(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.avg(getPath(sourceSupplier));
            }
        };
    }

    public static SqlExpression max(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.max(getPath(sourceSupplier));
            }
        };
    }

    public static SqlExpression min(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.min(getPath(sourceSupplier));
            }
        };
    }

    public static SqlExpression sum(Field field) {
        return new AbstractFunction(field) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.sum(getPath(sourceSupplier));
            }
        };
    }

    public static <X, Y> SqlSelection add(X x, Y y, String alias, Class<? extends Number> castType) {
        return new AbstractOperation<>(x, y, alias, castType) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.sum(
                        getExpression(x, cb, sourceSupplier),
                        getExpression(y, cb, sourceSupplier)
                );
            }
        };
    }

    public static <X, Y> SqlSelection sub(X x, Y y, String alias, Class<? extends Number> castType) {
        return new AbstractOperation<>(x, y, alias, castType) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cb.diff(
                        getExpression(x, cb, sourceSupplier),
                        getExpression(y, cb, sourceSupplier)
                );
            }
        };
    }

    public static <X, Y> SqlSelection multiply(X x, Y y, String alias, Class<? extends Number> castType) {
        return new AbstractOperation<>(x, y, alias, castType) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cast(cb.prod(
                        getExpression(x, cb, sourceSupplier),
                        getExpression(y, cb, sourceSupplier)
                ));
            }
        };
    }

    public static <X, Y> SqlSelection div(X x, Y y, String alias, Class<? extends Number> castType) {
        return new AbstractOperation<>(x, y, alias, castType) {
            @Override
            public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
                return (Expression<T>) cast(cb.quot(
                        getExpression(x, cb, sourceSupplier),
                        getExpression(y, cb, sourceSupplier)
                ));
            }
        };
    }

    public static SqlExpression concat(Object... parts) {
        return new Concat(parts);
    }

    public static SqlExpression jsonbExtractPathFunction(Field field, String... jsonPath) {
        return new JsonbExtractPathFunction(field, jsonPath);
    }

    public static Round round(SqlExpression expression, String alias) {
        return new Round(expression, alias);
    }
}

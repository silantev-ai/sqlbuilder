package querybuilder.structure.Impl;

import lombok.Data;
import querybuilder.structure.SqlExpression;
import querybuilder.structure.enums.SortDirection;

import javax.persistence.criteria.*;
import java.util.function.Function;

@Data
public class Field implements SqlExpression {
    protected final Table table;
    protected final String field;
    protected final SortDirection sortDirection;
    protected final Class<?> castType;

    public Field(Table table, String field, SortDirection sortDirection, Class<?> castType) {
        this.table = table;
        this.field = field;
        this.sortDirection = sortDirection;
        this.castType = castType;
    }

    public static Field of(String field) {
        return new Field(null, field, null, null);
    }

    public static Field of(Table table, String field) {
        return new Field(table, field, null, null);
    }

    public static Field of(Table table, String field, SortDirection sortDirection) {
        return new Field(table, field, sortDirection, null);
    }

    public String getFieldAlias() {
        return table == null ? field : table.getAlias() + "." + field;
    }

    @Override
    public <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        Path<T> path = sourceSupplier.apply(getTable()).get(field);
        return (Expression<T>) (castType == null ? path : path.as(castType));
    }

    @Override
    public Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier) {
        return getExpression(cb, sourceSupplier).alias(getFieldAlias());
    }
}

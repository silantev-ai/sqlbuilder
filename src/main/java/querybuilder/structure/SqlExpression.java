package querybuilder.structure;

import querybuilder.structure.Impl.Table;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import java.util.function.Function;

public interface SqlExpression extends SqlSelection {

    <T> Expression<T> getExpression(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier);
}

package querybuilder.structure;

import querybuilder.structure.Impl.Table;
import querybuilder.structure.enums.Logical;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.function.Function;

public interface Statement extends WhereToken {
    // region API methods
    LogicalOperator operator(Logical operator);
    CloseBracket closeBracket();
    QueryBuilder endWhere();
    default QueryBuilder endHaving() {
        return endWhere();
    }
    // end region

    // region internal methods
    Predicate getPredicate(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier);
    void setWhere(Where where);
    // endregion
}

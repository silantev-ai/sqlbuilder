package querybuilder.structure;

import querybuilder.structure.enums.Logical;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

public interface Statement extends WhereToken {
    // region API methods
    LogicalOperator operator(Logical operator);
    CloseBracket closeBracket();
    QueryBuilder endWhere();
    // end region

    // region internal methods
    Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source);
    String getAlias();
    void setWhere(Where where);
    // endregion
}

package querybuilder.structure;

import querybuilder.structure.Impl.JoinTable;
import querybuilder.structure.Impl.QueryBuilderImpl;
import querybuilder.structure.enums.AggregateFunction;
import querybuilder.structure.enums.SortDirection;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;

public interface QueryBuilder {
    static QueryBuilder create() {
        return new QueryBuilderImpl();
    }

    QueryBuilder from(Class<?> model);
    QueryBuilder join(JoinTable joinTable);
    QueryBuilder select(String tableAlias, String field, AggregateFunction aggregateFunc);

    Where startWhere();
    QueryBuilder groupBy(String field);
    QueryBuilder groupBy(String tableAlias, String field);
    QueryBuilder orderBy(String field, SortDirection direction);
    QueryBuilder orderBy(String tableAlias, String field, SortDirection direction);
    QueryBuilder offset(Integer offset);
    QueryBuilder limit(Integer limit);

    List<Tuple> execute(EntityManager entityManager);
    long getTotal(EntityManager entityManager);
}

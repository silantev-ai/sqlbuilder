package querybuilder.structure.Impl;

import querybuilder.CustomCriteriaBuilder;
import querybuilder.structure.QueryBuilder;
import querybuilder.structure.Where;
import querybuilder.structure.WhereToken;
import querybuilder.structure.enums.AggregateFunction;
import querybuilder.structure.enums.SortDirection;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.*;

public class QueryBuilderImpl implements QueryBuilder {
    private final Where where;
    private final List<Field> selections;
    private final List<Field> groupBy;
    private final List<Field> sortFields;
    private final Map<String, JoinTable> joins;
    private Class<?> model;
    private Integer offset;
    private Integer limit;

    public QueryBuilderImpl() {
        selections = new ArrayList<>();
        groupBy = new ArrayList<>();
        sortFields = new ArrayList<>();
        joins = new LinkedHashMap<>();
        where = new WhereImpl(this);
    }

    @Override
    public QueryBuilder from(Class<?> model) {
        this.model = model;
        return this;
    }

    @Override
    public QueryBuilder join(JoinTable joinTable) {
        joins.put(joinTable.getAlias(), joinTable);
        return this;
    }

    @Override
    public QueryBuilder select(String tableAlias, String field, AggregateFunction aggregateFunc) {
        selections.add(ExprFactory.createField(tableAlias, field, null, aggregateFunc));
        return this;
    }

    @Override
    public Where startWhere() {
        return where;
    }

    @Override
    public QueryBuilder groupBy(String field) {
        groupBy.add(ExprFactory.createField(null, field, null, null));
        return this;
    }

    @Override
    public QueryBuilder groupBy(String tableAlias, String field) {
        groupBy.add(ExprFactory.createField(tableAlias, field, null, null));
        return this;
    }

    @Override
    public QueryBuilder orderBy(String field, SortDirection direction) {
        sortFields.add(ExprFactory.createField(null, field, direction, null));
        return this;
    }

    @Override
    public QueryBuilder orderBy(String tableAlias, String field, SortDirection direction) {
        sortFields.add(ExprFactory.createField(tableAlias, field, direction, null));
        return this;
    }

    @Override
    public QueryBuilder offset(Integer offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public QueryBuilder limit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public List<Tuple> execute(EntityManager entityManager) {
        return new CustomCriteriaBuilder<>(entityManager, model, offset, limit)
                .processJoins(joins)
                .processSelectionFields(selections)
                .processWhere(getCopyOfWhereTokens())
                .processGroupBy(groupBy)
                .processSorting(sortFields)
                .execute();
    }

    @Override
    public long getTotal(EntityManager entityManager) {
        Field field = ExprFactory.createField(null, "id", null, AggregateFunction.COUNT);

        List<Tuple> result = new CustomCriteriaBuilder<>(entityManager, model, null, null)
                .processJoins(joins)
                .processSelectionFields(List.of(field))
                .processWhere(getCopyOfWhereTokens())
                .execute();
        return (long) result.get(0).get("id");
    }

    @SuppressWarnings("unchecked")
    private Deque<WhereToken> getCopyOfWhereTokens() {
        return (Deque<WhereToken>)((LinkedList<WhereToken>) where.getElements()).clone();
    }
}

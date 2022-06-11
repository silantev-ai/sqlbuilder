package querybuilder.structure.Impl;

import querybuilder.CustomCriteriaBuilder;
import querybuilder.structure.Impl.functions.SqlFunc;
import querybuilder.structure.QueryBuilder;
import querybuilder.structure.SqlSelection;
import querybuilder.structure.Where;
import querybuilder.structure.WhereToken;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.*;

public class QueryBuilderImpl implements QueryBuilder {
    private final Where where;
    private final Where having;
    private boolean distinct;
    private final List<SqlSelection> selections;
    private final List<Field> groupBy;
    private final List<Field> sortFields;
    private final Map<String, JoinTable> joins;
    private final List<Table> tables;
    private Class<?> model;
    private Integer offset;
    private Integer limit;

    public QueryBuilderImpl() {
        selections = new ArrayList<>();
        groupBy = new ArrayList<>();
        sortFields = new ArrayList<>();
        joins = new LinkedHashMap<>();
        tables = new ArrayList<>();
        where = new WhereImpl(this);
        having = new WhereImpl(this);
    }

    @Override
    public QueryBuilder distinct() {
        this.distinct = true;
        return this;
    }

    @Override
    public QueryBuilder from(Class<?> model) {
        this.model = model;
        return this;
    }

    @Override
    public QueryBuilder from(Table... tables) {
        this.tables.addAll(Arrays.asList(tables));
        return this;
    }

    @Override
    public QueryBuilder join(JoinTable... joinTables) {
        Arrays.stream(joinTables).forEach(this::join);
        return this;
    }

    private void join(JoinTable joinTable) {
        joins.put(joinTable.getAlias(), joinTable);
    }

    @Override
    public QueryBuilder select(SqlSelection... selection) {
        selections.addAll(Arrays.asList(selection));
        return this;
    }

    @Override
    public QueryBuilder select(String... fields) {
        Arrays.stream(fields)
                .map(Field::of)
                .forEach(selections::add);
        return this;
    }

    @Override
    public Where startWhere() {
        return where;
    }

    @Override
    public QueryBuilder groupBy(Field field) {
        groupBy.add(field);
        return this;
    }

    @Override
    public QueryBuilder orderBy(Field field) {
        sortFields.add(field);
        return this;
    }

    @Override
    public Where startHaving() {
        return having;
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
                .processTables(tables)
                .processSelectionFields(selections, distinct)
                .processWhere(getCopyOfWhereTokens())
                .processGroupBy(groupBy)
                .processHaving(having.getElements())
                .processSorting(sortFields)
                .execute();
    }

    @Override
    public long getTotal(EntityManager entityManager) {
        SqlSelection field = distinct ? SqlFunc.countDistinct("id") : SqlFunc.count("id");
        List<Tuple> result = new CustomCriteriaBuilder<>(entityManager, model, null, null)
                .processJoins(joins)
                .processTables(tables)
                .processSelectionFields(List.of(field), false)
                .processWhere(getCopyOfWhereTokens())
                .processGroupBy(groupBy)
                .processHaving(having.getElements())
                .execute();
        return (long) result.get(0).get("id");
    }

    @SuppressWarnings("unchecked")
    private Deque<WhereToken> getCopyOfWhereTokens() {
        return (Deque<WhereToken>)((LinkedList<WhereToken>) where.getElements()).clone();
    }
}

package querybuilder.structure;

import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.JoinTable;
import querybuilder.structure.Impl.QueryBuilderImpl;
import querybuilder.structure.Impl.Table;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;

/**
 * What this class capable of:
 *
 * <p><strong>Example</strong> of aggregation functions
 * <p>Code:
 * <pre>
 * Field ID_FIELD = Field.of("id");
 * Field STATUS_FIELD = Field.of("status");
 *
 * QueryBuilder.create()
 *     .select(SqlFunc.count(ID_FIELD), STATUS_FIELD)
 *     .from(Job.class)
 *     .groupBy(STATUS_FIELD)
 *     .execute(entityManager);
 * </pre>
 *  SQL will be:
 * <pre>
 * SELECT Count(job0_.id) AS col_0_0_,
 *        job0_.status    AS col_1_0_
 * FROM   job job0_
 * GROUP  BY job0_.status
 * </pre>
 *
 *
 * <p><strong>Example</strong> of a concatenation function
 * <p>Code:
 * <pre>
 * QueryBuilder.create()
 *     .select(SqlFunc.concat("JobId is: ", Field.of("id")))
 *     .from(Job.class)
 *     .execute(entityManager);
 * </pre>
 * SQL will be:
 * <pre>
 * SELECT ( ( ''
 *        || 'JobId is: ' )
 *      || job0_.id ) AS col_0_0_
 * FROM   job job0_
 * </pre>
 *
 *
 * <p><strong>Example</strong> of arithmetic expressions in a <i>select</i> clause
 * <p>Code:
 * <pre>
 * JoinTable infoModelJoin = new JoinTable("infomodel");
 * Field attrValueCountField = Field.of("attributeValueCount");
 * Field attrCountField = Field.of(infoModelJoin, "attributeCount");
 *
 * // formula: (attributeValueCount / attributeCount) * 100
 * SqlSelection fillRate = SqlFunc.multiply(
 *     SqlFunc.div(attrValueCountField, attrCountField), 100, "fillRate",null);
 *
 * QueryBuilder.create()
 *     .select(fillRate)
 *     .from(Product.class)
 *     .join(infoModelJoin)
 *     .execute(entityManager);
 * </pre>
 * SQL will be:
 * <pre>
 * SELECT product0_.attribute_value_count / infomodel1_.attribute_count * 100 AS
 *    col_0_0_
 * FROM   PUBLIC.product product0_
 *    INNER JOIN PUBLIC.product_model infomodel1_
 *            ON product0_.model_id = infomodel1_.id
 * </pre>
 *
 *
 * <p><strong>Example</strong> of arithmetic expressions in a <i>where</i> clause
 * <p>Code:
 * <pre>
 * JoinTable infoModelJoin = new JoinTable("infomodel");
 * Field attrValueCountField = Field.of("attributeValueCount");
 * Field attrCountField = Field.of(infoModelJoin, "attributeCount");
 *
 * // formula: (attributeValueCount / attributeCount) * 100
 * SqlSelection fillRate = SqlFunc.multiply(
 *         SqlFunc.div(attrValueCountField, attrCountField), 100, "fillRate",null);
 *
 * QueryBuilder.create()
 *     .select(Field.of("id"))
 *     .from(Product.class)
 *     .join(infoModelJoin)
 *     .startWhere()
 *         .statement(Operation.equal(fillRate, 70))
 *     .endWhere()
 *     .execute(entityManager);
 * </pre>
 * SQL will be:
 * <pre>
 * SELECT product0_.id AS col_0_0_
 * FROM   PUBLIC.product product0_
 *    INNER JOIN PUBLIC.product_model infomodel1_
 *            ON product0_.model_id = infomodel1_.id
 * WHERE  product0_.attribute_value_count / infomodel1_.attribute_count * 100 = 70
 * </pre>
 *
 *
 * <p><strong>Example</strong> of fetching pageable result data
 * <p>Code:
 * <pre>
 * Field ID_FIELD = Field.of("id");
 * Field NAME_FIELD = Field.of("name");
 * QueryBuilder queryBuilder = QueryBuilder.create()
 *         .select(ID_FIELD, NAME_FIELD)
 *         .from(Category.class);
 *
 * PageableQuery&#60;Category&#62; pageableQuery = new PageableQuery<>(queryBuilder) {
 *     &#64;Override
 *     protected Category mapObject(Tuple tuple) {
 *         Category category = new Category();
 *         category.setId((Integer) tuple.get(id_.getFieldAlias()));
 *         category.setName((String) tuple.get(name_.getFieldAlias()));
 *         return category;
 *     }
 * };
 *
 * Page<Category> result = pageableQuery.execute(entityManager, pageable);
 * </pre>
 */
public interface QueryBuilder {
    static QueryBuilder create() {
        return new QueryBuilderImpl();
    }

    QueryBuilder distinct();
    QueryBuilder from(Class<?> model);
    QueryBuilder from(Table... table);

    QueryBuilder join(JoinTable... joinTable);
    QueryBuilder select(SqlSelection... selection);
    QueryBuilder select(String... fieldName);

    Where startWhere();
    QueryBuilder groupBy(Field field);
    QueryBuilder orderBy(Field field);
    Where startHaving();
    QueryBuilder offset(Integer offset);
    QueryBuilder limit(Integer limit);

    List<Tuple> execute(EntityManager entityManager);
    long getTotal(EntityManager entityManager);
}

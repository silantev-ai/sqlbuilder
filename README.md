An example of aggregation functions:

```java
Field ID_FIELD = Field.of("id");
Field STATUS_FIELD = Field.of("status");

QueryBuilder.create()
    .select(SqlFunc.count(ID_FIELD), STATUS_FIELD)
    .from(Job.class)
    .groupBy(STATUS_FIELD)
    .execute(entityManager);
```
will be converted to sql:
```sql
SELECT Count(job0_.id) AS col_0_0_,
       job0_.status    AS col_1_0_
FROM   job job0_
GROUP  BY job0_.status
```

An example of a concatenation function:

```java
QueryBuilder.create()
    .select(SqlFunc.concat("JobId is: ", Field.of("id")))
    .from(Job.class)
    .execute(entityManager);
```
will be converted to sql:

```sql
SELECT ( ( ''
    || 'JobId is: ' )
    || job0_.id ) AS col_0_0_
FROM   job job0_
```

An example of variant operator:

```java
@Data
public class JobFilter {
    private OperatorAndValue<String> name;
}

public class JobCustomRepositoryImpl implements JobCustomRepository {
    
    public Page<Job> findAllByFilter(JobFilter jobFilter, Pageable pageable) {

        Field ID_FIELD = Field.of("id");
        Field NAME_FIELD = Field.of("name");

        QueryBuilder queryBuilder = QueryBuilder.create()
                .select(ID_FIELD, NAME_FIELD)
                .from(Job.class)
                .startWhere()
                .statement(variantOperatorForString(NAME_FIELD, jobFilter.getName()))
                .endWhere();
        
        return getJobPageableQuery(queryBuilder).execute(entityManager, pageable);
    }
    
    private static PageableQuery<Job> getJobPageableQuery(QueryBuilder queryBuilder) {
        return new PageableQuery<>(queryBuilder) {
            @Override
            protected Job mapObject(Tuple tuple) {
                Job job = new Job();
                job.setId((Integer) tuple.get(ID_FIELD.getFieldAlias()));
                job.setName((String) tuple.get(NAME_FIELD.getFieldAlias()));
                return job;
            }
        };
    }
}
```

if json is:
```json
{
  "name": {
    "operator": "EQUALS",
    "value": "string"
  }
}
```
sql will be:
```sql
SELECT job0_.id AS col_0_0_, job0_.name AS col_1_0_
FROM   job job0_
WHERE job0_.name =?
```

if json is:
```json
{
  "name": {
    "operator": "IS_NULL"
  }
}
```
sql will be:
```sql
SELECT job0_.id AS col_0_0_, job0_.name AS col_1_0_
FROM   job job0_
WHERE job0_.name IS NULL
```

if json is:
```json
{
  "name": {
    "operator": "NOT_EQUAL",
    "value": "string"
  }
}
```
sql will be:
```sql
SELECT job0_.id AS col_0_0_, job0_.name AS col_1_0_
FROM   job job0_
WHERE job0_.name <>?
```

if json is:
```json
{
  "name": {
    "operator": "STARTS_WITH",
    "value": "string"
  }
}
```
sql will be:
```sql
SELECT job0_.id AS col_0_0_, job0_.name AS col_1_0_
FROM   job job0_
WHERE job0_.name like ? -- where ? is 'string%'
```

if json is:
```json
{
  "name": {
    "operator": "IN",
    "values": ["string", "string1","string2"]
  }
}
```
sql will be:
```sql
SELECT job0_.id AS col_0_0_, job0_.name AS col_1_0_
FROM   job job0_
WHERE job0_.name in (? , ? , ?)
```

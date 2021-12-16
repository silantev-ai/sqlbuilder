An example of using aggregation functions:

```java
QueryBuilder.create()
    .select(null, "id", AggregateFunction.COUNT)
    .select(null, "status", null)

    .from(Job.class)
    
    .groupBy(null, "status")
    
    .execute(entityManager);
```
will be converted to sql:
```sql
SELECT Count(job0_.id) AS col_0_0_,
       job0_.status    AS col_1_0_
FROM   job job0_
GROUP  BY job0_.status 
```

An example of getting records from a table:

```java
JoinTable eventJoin = JoinTable.builder().field("event").model(Event.class).alias("e").build();
JoinTable eventTypeJoin = JoinTable.builder().field("eventType").model(EventType.class).alias("et").parentTableAlias("e").build();

QueryBuilder queryBuilder = QueryBuilder.create()
    .select(null, "id", null)
    .select(null, "status", null)
    .select("e", "source", null)
    .select("et", "id", null)

    .from(Job.class)
    .join(eventJoin)
    .join(eventTypeJoin)

    .startWhere()
        .statement(Factory.equal(null, "status", JobStatus.SUCCESS))

        .operator(AND)

        .openBracket()
            .statement(Factory.equal("e", "source", EventSource.MANUAL))
            .operator(OR)
            .statement(Factory.equal("et", "id", "EXPRD"))
        .closeBracket()
        
        .operator(OR)
        
        .statement(new InStatement(null, "status", List.of(JobStatus.SUCCESS)))
    .endWhere();

    PageableQuery<Job> pageableQuery = new PageableQuery<>(queryBuilder) {
        @Override
        protected Job mapObject(Tuple tuple) {
            Job job = new Job();
            job.setId((Long) tuple.get("id"));
            job.setStatus((JobStatus) tuple.get("status"));
    
            job.setEvent(new Event());
            job.getEvent().setSource((EventSource) tuple.get("e.source"));
    
            job.getEvent().setEventType(new EventType());
            job.getEvent().getEventType().setId((String) tuple.get("et.id"));
            return job;
        }
    };
    
    Pageable pageable = PageRequest.of(1, 100, Sort.by(Sort.Direction.ASC, "id"));
    Page<Job> result = pageableQuery.execute(entityManager, pageable);
```
will be converted to sql:

```sql
SELECT job0_.id       AS col_0_0_,
       job0_.status   AS col_1_0_,
       event1_.source AS col_2_0_,
       eventtype2_.id AS col_3_0_
FROM   job job0_
           INNER JOIN event event1_
                      ON job0_.event_id = event1_.id
           INNER JOIN event_type eventtype2_
                      ON event1_.event_type_id = eventtype2_.id
WHERE  job0_.status =?
    AND ( event1_.source =?
        OR eventtype2_.id =? )
   OR job0_.status IN ( ? )
ORDER  BY job0_.id ASC
    LIMIT  ? offset ? 
```

An example with nullable values:

```java
Object nullableValue = null;
QueryBuilder.create()
    .select(null, "id", null)
    .from(Job.class)
    .startWhere()
    .statement(Factory.equal(null, "status", nullableValue))
    .operator(OR)
    .statement(new InStatement(null, "status", null))
    .operator(OR)
    .statement(Factory.isNotNull(null, "id"))
    .endWhere()
.execute(entityManager);
```
will be converted to sql:

```sql
SELECT job0_.id AS col_0_0_
FROM   job job0_
WHERE  1 = 1
   OR 1 = 1
   OR job0_.id IS NOT NULL 
```

An example of using postgres functions:

```java
Statement postgresFunc =  new JsonbExtractPathFunction(null, "parameters", "196", SqlOperator.EQUAL, "eventPayload", "userId");
QueryBuilder.create()
    .select(null, "jobId", null)

    .from(JobParameters.class)
    
    .startWhere()
      .statement(postgresFunc)
    .endWhere()
    
    .execute(entityManager);
```
will be converted to sql:

```sql
SELECT jobparamet0_.job_id AS col_0_0_
FROM   job_parameters jobparamet0_
WHERE  Jsonb_extract_path_text(jobparamet0_.parameters, ?, ?) = ? 
```

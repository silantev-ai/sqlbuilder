package querybuilder;

import querybuilder.structure.*;
import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.JoinTable;
import querybuilder.structure.Impl.Table;
import querybuilder.structure.enums.SortDirection;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class CustomCriteriaBuilder<T> {
    private final EntityManager entityManager;
    private final CriteriaBuilder cb;
    private final CriteriaQuery<Tuple> cq;
    private final Root<T> root;
    private final Map<Table, Join<T, ?>> joins;
    private final Map<Table, From<?, ?>> tables;
    private final Integer offset;
    private final Integer limit;

    public CustomCriteriaBuilder(EntityManager entityManager, Class<T> model, Integer offset, Integer limit) {
        this.entityManager = entityManager;
        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(Tuple.class);
        joins = new HashMap<>();
        tables = new HashMap<>();
        root = cq.from(model);
        this.offset = offset;
        this.limit = limit;
    }

    public CustomCriteriaBuilder<T> processTables(Collection<Table> tables) {
        tables.forEach(table -> {
            From<?, ?> from = cq.from(table.getEntityCls());
            this.tables.put(table, from);
        });

        return this;
    }

    public CustomCriteriaBuilder<T> processJoins(Map<String, JoinTable> joinTables) {
        joinTables.values().forEach(this::processJoin);

        // apply conditions on joins if they exist
        joinTables.values().forEach(
              jt -> Optional.ofNullable(jt.getConditions())
                      .stream()
                      .flatMap(Arrays::stream)
                      .map(statement -> statement.getPredicate(cb, this::getSource))
                      .forEach(predicate -> joins.get(jt).on(predicate))
        );

        return this;
    }

    private void processJoin(JoinTable jt) {
        Join<T, ?> joinTable;
        JoinType joinType = Optional.ofNullable(jt.getJoinType()).orElse(JoinType.INNER);
        if (jt.getParent() == null) {
            joinTable = root.join(jt.getField(), joinType);
        } else {
            joinTable = getSource(jt.getParent()).join(jt.getField(), joinType);
        }
        joins.put(jt, joinTable);
    }

    public CustomCriteriaBuilder<T> processSelectionFields(List<SqlSelection> selectionFields, boolean distinct) {
        cq.multiselect(selectionFields.stream().map(this::getSelection).collect(Collectors.toList()))
                .distinct(distinct);
        return this;
    }

    private Selection<?> getSelection(SqlSelection selection) {
        return selection.getSelection(cb, this::getSource);
    }

    public CustomCriteriaBuilder<T> processWhere(Deque<WhereToken> elements) {
        if (!elements.isEmpty()) {
            cq.where(evaluateExpressions(elements));
        }
        return this;
    }

    public CustomCriteriaBuilder<T> processGroupBy(List<Field> groupBy) {
        cq.groupBy(groupBy.stream().map(f -> getSource(f.getTable()).get(f.getField())).collect(Collectors.toList()));
        return this;
    }

    public CustomCriteriaBuilder<T> processHaving(Deque<WhereToken> elements) {
        if (!elements.isEmpty()) {
            cq.having(evaluateExpressions(elements));
        }
        return this;
    }

    public CustomCriteriaBuilder<T> processSorting(List<Field> sortFields) {
        cq.orderBy(sortFields.stream().map(this::getOrder).collect(Collectors.toList()));
        return this;
    }

    private Order getOrder(Field field) {
        Expression<?> path = getSource(field.getTable()).get(field.getField());
        return field.getSortDirection() == SortDirection.ASC ? cb.asc(path) : cb.desc(path);
    }

    public List<Tuple> execute() {
        TypedQuery<Tuple> query = entityManager.createQuery(cq);
        Optional.ofNullable(offset).ifPresent(query::setFirstResult);
        Optional.ofNullable(limit).ifPresent(query::setMaxResults);

        return query.getResultList();
    }

    private Predicate evaluateExpressions(Deque<WhereToken> elements) {
        if (elements.size() == 1) {
            return combine((Statement) elements.getFirst());
        }
        Deque<Object> valStack = new LinkedList<>();
        Deque<WhereToken> operators = new LinkedList<>();
        while (!elements.isEmpty()) {
            WhereToken token = elements.poll();
            if (token instanceof OpenBracket || token instanceof LogicalOperator) {
                operators.push(token);
            } else if (token instanceof CloseBracket) {
                while(!(operators.peek() instanceof  OpenBracket)) {
                    LogicalOperator operator = (LogicalOperator) operators.pop();
                    Object right = valStack.pop();
                    Object left =  valStack.pop();
                    valStack.push(combine(left, operator, right));
                }
                operators.pop();
            } else if (token instanceof Statement) {
                valStack.push(token);
            }
        }

        while (!operators.isEmpty()) {
            valStack.addLast(combine(
                    valStack.pollLast(),
                    (LogicalOperator)  operators.pollLast(),
                    valStack.pollLast()
            ));
        }

        if (valStack.peek() instanceof Statement) {
            return combine((Statement) valStack.pop());
        }

        return (Predicate) valStack.pop();
    }

    private Predicate combine(Object left, LogicalOperator operator, Object right) {
        if (left instanceof Statement && right instanceof Statement) {
            return combine((Statement) left, operator, (Statement) right);
        } else if (left instanceof Statement && right instanceof Predicate) {
            return combine((Statement) left, operator, (Predicate) right);
        } else if (left instanceof Predicate && right instanceof Predicate) {
            return combine((Predicate) left, operator, (Predicate) right);
        } else if (left instanceof Predicate && right instanceof Statement) {
            return combine((Predicate) left, operator, (Statement) right);
        }
        throw new RuntimeException();
    }

    private Predicate combine(Statement left, LogicalOperator operator, Statement right) {
        if (operator.getOperator().toString().equals(Predicate.BooleanOperator.AND.toString())) {
            return cb.and(combine(left), combine(right));
        } else {
            return cb.or(combine(left), combine(right));
        }
    }

    private Predicate combine(Statement left, LogicalOperator operator, Predicate right) {
        if (operator.getOperator().toString().equals(Predicate.BooleanOperator.AND.toString())) {
            return cb.and(combine(left), right);
        } else {
            return cb.or(combine(left), right);
        }
    }

    private Predicate combine(Predicate left, LogicalOperator operator, Statement right) {
        if (operator.getOperator().toString().equals(Predicate.BooleanOperator.AND.toString())) {
            return cb.and(left, combine(right));
        } else {
            return cb.or(left, combine(right));
        }
    }

    private Predicate combine(Predicate left, LogicalOperator operator, Predicate right) {
        if (operator.getOperator().toString().equals(Predicate.BooleanOperator.AND.toString())) {
            return cb.and(left, right);
        } else {
            return cb.or(left, right);
        }
    }

    private Predicate combine(Statement statement) {
        return statement.getPredicate(cb, this::getSource);
    }

    private From<?, ?> getSource(Table table) {
        if (table == null) {
            return root;
        } else if (joins.containsKey(table)) {
            return joins.get(table);
        } else {
            return tables.get(table);
        }
    }
}

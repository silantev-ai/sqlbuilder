package querybuilder;

import querybuilder.structure.*;
import querybuilder.structure.Impl.Field;
import querybuilder.structure.Impl.JoinTable;
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
    private final Map<String, Join<T, ?>> joins;
    private final Integer offset;
    private final Integer limit;

    public CustomCriteriaBuilder(EntityManager entityManager, Class<T> model, Integer offset, Integer limit) {
        this.entityManager = entityManager;
        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(Tuple.class);
        joins = new HashMap<>();
        root = cq.from(model);
        this.offset = offset;
        this.limit = limit;
    }

    public CustomCriteriaBuilder<T> processJoins(Map<String, JoinTable> joinTables) {
        joinTables.values().forEach(this::processJoin);
        return this;
    }

    private void processJoin(JoinTable jt) {
        Join<T, ?> joinTable;
        JoinType joinType = Optional.ofNullable(jt.getJoinType()).orElse(JoinType.INNER);
        if (jt.getParentTableAlias() == null) {
            joinTable = root.join(jt.getField(), joinType);
        } else {
            joinTable = getSource(jt.getParentTableAlias()).join(jt.getField(), joinType);
        }
        joins.put(jt.getAlias(), joinTable);
    }

    public CustomCriteriaBuilder<T> processSelectionFields(List<Field> selectionFields) {
        cq.multiselect(selectionFields.stream().map(this::getSelection).collect(Collectors.toList()));
        return this;
    }

    private Selection<?> getSelection(Field field) {
        if (field.getAggregateFunc() == null) {
            return getSource(field.getTableAlias()).get(field.getField()).alias(field.getFieldAlias());
        } else {
            Path<Number> fieldPath = getSource(field.getTableAlias()).get(field.getField());
            switch (field.getAggregateFunc()) {
                case COUNT:
                    return cb.count(fieldPath).alias(field.getFieldAlias());
                case SUM:
                    return cb.sum(fieldPath).alias(field.getFieldAlias());
                case MIN:
                    return cb.min(fieldPath).alias(field.getFieldAlias());
                case MAX:
                    return cb.max(fieldPath).alias(field.getFieldAlias());
                case AVG:
                    return cb.avg(fieldPath).alias(field.getFieldAlias());
                default:
                    return getSource(field.getTableAlias()).get(field.getField()).alias(field.getFieldAlias());
            }
        }
    }

    public CustomCriteriaBuilder<T> processWhere(Deque<WhereToken> elements) {
        if (!elements.isEmpty()) {
            cq.where(evaluateExpressions(elements));
        }
        return this;
    }

    public CustomCriteriaBuilder<T> processGroupBy(List<Field> groupBy) {
        cq.groupBy(groupBy.stream().map(f -> getSource(f.getTableAlias()).get(f.getField())).collect(Collectors.toList()));
        return this;
    }

    public CustomCriteriaBuilder<T> processSorting(List<Field> sortFields) {
        cq.orderBy(sortFields.stream().map(this::getOrder).collect(Collectors.toList()));
        return this;
    }

    private Order getOrder(Field field) {
        Expression<?> path = getSource(field.getTableAlias()).get(field.getField());
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
        return statement.getPredicate(cb, getSource(statement.getAlias()));
    }


    private From<T, ?> getSource(String alias) {
        return alias == null ? root : joins.get(alias);
    }
}

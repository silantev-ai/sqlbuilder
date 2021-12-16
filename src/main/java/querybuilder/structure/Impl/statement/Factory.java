package querybuilder.structure.Impl.statement;

import querybuilder.structure.Statement;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.Optional;

public class Factory {

    public static Statement equal(String tableAlias, String field, Object value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.equal(source.get(field), value));
            }
        };
    }

    public static Statement notEqual(String tableAlias, String field, Object value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.notEqual(source.get(field), value));
            }
        };
    }

    public static Statement gt(String tableAlias, String field, Number value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.gt(source.get(field), (Number) value));
            }
        };
    }

    public static Statement gte(String tableAlias, String field, Number value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.ge(source.get(field), (Number) value));
            }
        };
    }

    public static Statement lt(String tableAlias, String field, Number value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.lt(source.get(field), (Number) value));
            }
        };
    }

    public static Statement lte(String tableAlias, String field, Number value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.le(source.get(field), (Number) value));
            }
        };
    }

    public static Statement gt(String tableAlias, String field, Date value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.greaterThan(source.get(field), (Date) value));
            }
        };
    }

    public static Statement gte(String tableAlias, String field, Date value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.greaterThanOrEqualTo(source.get(field), (Date) value));
            }
        };
    }

    public static Statement lt(String tableAlias, String field, Date value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.lessThan(source.get(field), (Date) value));
            }
        };
    }

    public static Statement lte(String tableAlias, String field, Date value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.lessThanOrEqualTo(source.get(field), (Date) value));
            }
        };
    }

    public static Statement like(String tableAlias, String field, String value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.like(source.get(field), (String) value));
            }
        };
    }

    public static Statement startsWith(String tableAlias, String field, String value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.like(source.get(field), value + "%"));
            }
        };
    }

    public static Statement endsWith(String tableAlias, String field, String value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.like(source.get(field), "%" + value));
            }
        };
    }

    public static Statement contains(String tableAlias, String field, String value) {
        return new StatementImpl(tableAlias, field, value) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return Optional.ofNullable(super.getPredicate(cb, source))
                        .orElseGet(() -> cb.like(source.get(field), "%" + value + "%"));
            }
        };
    }

    public static Statement isNull(String tableAlias, String field) {
        return new StatementImpl(tableAlias, field, null) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return cb.isNull(source.get(field));
            }
        };
    }

    public static Statement isNotNull(String tableAlias, String field) {
        return new StatementImpl(tableAlias, field, null) {
            @Override
            public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
                return cb.isNotNull(source.get(field));
            }
        };
    }
}

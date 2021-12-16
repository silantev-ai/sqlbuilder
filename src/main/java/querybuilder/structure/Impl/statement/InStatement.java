package querybuilder.structure.Impl.statement;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.util.List;

public class InStatement extends StatementImpl {
    private final List<?> values;
    public InStatement(String tableAlias, String field, List<?> values) {
        super(tableAlias, field, null);
        this.values = values;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder cb, From<?, ?> source) {
        return values == null ? cb.equal(cb.literal(1), 1) : source.get(field).in(values);
    }
}

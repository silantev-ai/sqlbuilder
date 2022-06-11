package querybuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import querybuilder.structure.Impl.Field;
import querybuilder.structure.QueryBuilder;
import querybuilder.structure.enums.SortDirection;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PageableQuery<T> {
    private final QueryBuilder queryBuilder;

    public PageableQuery(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public Page<T> execute(EntityManager em, Pageable pageable) {
        prepare(pageable);
        List<T> result = queryBuilder.execute(em).stream().map(this::mapObject).collect(Collectors.toList());
        long total = result.size() >= pageable.getPageSize() ? getTotal(em) : result.size();
        return new PageImpl<>(result, pageable, total);
    }

    protected long getTotal(EntityManager em) {
        return queryBuilder.getTotal(em);
    }

    protected abstract T mapObject(Tuple tuple);

    protected void prepare(Pageable pageable) {
        queryBuilder.offset((int) pageable.getOffset());
        queryBuilder.limit(pageable.getPageSize());

        pageable.getSort().forEach(o ->
                queryBuilder.orderBy(
                        Field.of(null, o.getProperty(), mapDirection(o.getDirection()))
                ));
    }

    private SortDirection mapDirection(Sort.Direction direction) {
        if (direction == Sort.Direction.DESC) {
            return SortDirection.DESC;
        }
        return SortDirection.ASC;
    }
}

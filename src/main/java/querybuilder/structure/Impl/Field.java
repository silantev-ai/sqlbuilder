package querybuilder.structure.Impl;

import lombok.Builder;
import lombok.Data;
import querybuilder.structure.enums.AggregateFunction;
import querybuilder.structure.enums.SortDirection;

@Data
@Builder
public class Field {
    private final String tableAlias;
    private final String field;
    private final SortDirection sortDirection;
    private final AggregateFunction aggregateFunc;

    public String getFieldAlias() {
        return tableAlias == null ? field : tableAlias + "." + field;
    }
}

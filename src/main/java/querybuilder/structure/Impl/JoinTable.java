package querybuilder.structure.Impl;

import lombok.Builder;
import lombok.Data;

import javax.persistence.criteria.JoinType;

@Data
@Builder
public class JoinTable {
    private final String alias;
    private final String field;
    private final Class<?> model;
    private final String parentTableAlias;
    // JoinType.INNER as default
    private JoinType joinType;
}

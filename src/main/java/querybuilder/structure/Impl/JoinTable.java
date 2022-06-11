package querybuilder.structure.Impl;

import lombok.Getter;
import lombok.Setter;
import querybuilder.structure.Statement;

import javax.persistence.criteria.JoinType;

@Getter
@Setter
public class JoinTable extends Table {

    private final String field;
    private JoinTable parent;
    private JoinType joinType; // JoinType.INNER as default
    private Statement[] conditions;

    public JoinTable(String field) {
        super(field);
        this.field = field;
    }

    public JoinTable(String alias, String field) {
        super(alias);
        this.field = field;
    }

    public JoinTable parent(JoinTable parent) {
        this.parent = parent;
        return this;
    }

    public JoinTable joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    public JoinTable conditions(Statement... conditions) {
        this.conditions = conditions;
        return this;
    }
}

package querybuilder.structure.Impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
public class Table {

    private final String alias;
    private Class<?> entityCls;

    public Table entity(Class<?> entityCls) {
        this.entityCls = entityCls;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return alias.equals(table.getAlias());
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias);
    }
}

package querybuilder.structure;

import querybuilder.structure.Impl.Table;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Selection;
import java.util.function.Function;

public interface SqlSelection {

    Selection<?> getSelection(CriteriaBuilder cb, Function<Table, From<?, ?>> sourceSupplier);
}

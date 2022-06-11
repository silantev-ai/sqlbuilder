package querybuilder.structure.Impl;

import querybuilder.structure.OpenBracket;
import querybuilder.structure.Statement;
import querybuilder.structure.Where;

public class OpenBracketImpl extends WhereTokenAbstract implements OpenBracket {

    public OpenBracketImpl(Where where) {
        super(where);
    }

    @Override
    public OpenBracket openBracket() {
        return SqlStructureFactory.openBracket(where);
    }

    @Override
    public Statement statement(Statement statement) {
        return SqlStructureFactory.statement(where, statement);
    }
}

package querybuilder.structure.Impl;


import querybuilder.structure.Where;
import querybuilder.structure.WhereToken;

public abstract class WhereTokenAbstract implements WhereToken {
    protected final Where where;

    public WhereTokenAbstract(Where where) {
        this.where = where;
    }

}

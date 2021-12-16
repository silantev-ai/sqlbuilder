package querybuilder.structure;

public interface OpenBracket extends WhereToken {
    OpenBracket openBracket();
    Statement statement(Statement statement);
}

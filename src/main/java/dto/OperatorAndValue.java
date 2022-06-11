package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorAndValue<T> implements Serializable {

    private EquationOperator operator;
    private T value;
    private List<T> values;

    public OperatorAndValue(EquationOperator operator, T value) {
        this.operator = operator;
        this.value = value;
    }
}

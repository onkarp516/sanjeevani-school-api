package in.truethics.ethics.ethicsapiv10.response;

import lombok.Data;

import java.util.List;

@Data
public class GenericDatatable<T> {
    private List<T> rows;
    private Integer totalRows;
}

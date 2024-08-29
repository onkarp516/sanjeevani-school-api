package in.truethics.ethics.ethicsapiv10.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseMessage {
    private static ResponseMessage instance = null;
    private int responseStatus;
    private Object responseObject;
    private String message = null;
    private String data;
    private Object response;

    public static ResponseMessage getInstance() {

        if (instance == null) {
            instance = new ResponseMessage();
        }
        return instance;
    }
}

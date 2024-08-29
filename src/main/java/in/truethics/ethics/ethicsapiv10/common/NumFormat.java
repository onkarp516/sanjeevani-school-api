package in.truethics.ethics.ethicsapiv10.common;

import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class NumFormat {
    public Double numFormat(Double input) {
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        Double result = Double.parseDouble(numberFormat.format(input));
        return result;
    }

    public String receiptNumFormat(Integer input) {
        String result = String.format("%03d", input);
        return result;
    }

    public String studentNumFormat(Integer input) {
        String result = String.format("%05d", input);
        return result;
    }

    public String twoDigitFormat(Integer input) {
        String result = String.format("%02d", input);
        return result;
    }
}

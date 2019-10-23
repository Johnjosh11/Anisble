package productapi.category.mappings.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtil {

    public static Timestamp getCurrentTime() {
        return new Timestamp(Calendar.getInstance().getTime().getTime());
    }
}

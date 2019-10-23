package productapi.category.mappings.dao;

import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.util.Calendar;
import java.time.ZonedDateTime;
import java.util.Date;


public class JooqDateComparison {

    public static Condition isNotNullAndBeforeNow(TableField<?, Timestamp> field) {
        return field.isNotNull().and(field.lessOrEqual(DSL.currentTimestamp()));
    }

    public static Condition isNullOrAfterNow(TableField<?, Timestamp> field) {
        return field.isNull().or(field.greaterOrEqual(DSL.currentTimestamp()));
    }

    public static Timestamp getCurrentTime() {
        return new Timestamp(Calendar.getInstance().getTime().getTime());
    }

    public static Condition isNullOrClosedInPreviousMonth(TableField<?, Timestamp> field) {
        Date date = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
        return field.isNull().or(field.greaterOrEqual(new Timestamp(date.getTime())));
    }

    public static Condition isNotNull(TableField<?, Timestamp> field) {
        return field.isNotNull();
    }

    public static Condition isNotNullAndWithin30Days(TableField<?, Timestamp> field) {
        return field.isNotNull().and(field.greaterOrEqual(getTimestampFromPast(30)));
    }

    private static Timestamp getTimestampFromPast(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);
        return new Timestamp(cal.getTime().getTime());
    }
}

package Helper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ReportHelper {
    String name();

    String[] arrayStatus() default  {"Active", "Suspended", "Decease", "Dropout", "Graduated"};

    boolean isStatus() default false;

    Class<?> clazz() default String.class;
}

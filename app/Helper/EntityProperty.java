package Helper;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
public @interface EntityProperty {
    String name();
    Class<?> ctrl() default String.class;
    String icon() default "fa fa-user";
    String type() default ".";
    int order() default 0;
    boolean addNew() default true;
    boolean noDelete() default false;
    boolean hasReport() default false;
}

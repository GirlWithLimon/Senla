package bookstore_app.config.annotation;

import bookstore_app.config.PropertyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {
    String configFileName() default "bookstore_app/config/bookstore.properties";
    String propertyName() default "";
    PropertyType type() default PropertyType.AUTO;
}
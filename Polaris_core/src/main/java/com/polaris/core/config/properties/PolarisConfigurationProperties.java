package com.polaris.core.config.properties;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * An annotation for Polaris configuration Properties for binding POJO as Properties Object.
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Import(ConfigurationPropertiesImport.class)
public @interface PolarisConfigurationProperties {

	/**
	 * The name prefix of the properties that are valid to bind to this object. Synonym
	 * for {@link #prefix()}. A valid prefix is defined by one or more words separated
	 * with dots (e.g. {@code "acme.system.feature"}).
	 * @return the name prefix of the properties to bind
	 */
	@AliasFor("prefix")
	String value() default "";

	/**
	 * The name prefix of the properties that are valid to bind to this object. Synonym
	 * for {@link #value()}. A valid prefix is defined by one or more words separated with
	 * dots (e.g. {@code "acme.system.feature"}).
	 * @return the name prefix of the properties to bind
	 */
	@AliasFor("value")
	String prefix() default "";
    
	/**
	 * Flag to indicate that when binding to this object invalid fields should be ignored.
	 * Invalid means invalid according to the binder that is used, and usually this means
	 * fields of the wrong type (or that cannot be coerced into the correct type).
	 * @return the flag value (default true)
	 */
	boolean ignoreInvalidFields() default true;
	
    /**
     * It indicates the properties of current doBind bean is auto-refreshed when Nacos configuration is changed.
     *
     * @return default value is <code>false</code>
     */
    boolean autoRefreshed() default false;

}
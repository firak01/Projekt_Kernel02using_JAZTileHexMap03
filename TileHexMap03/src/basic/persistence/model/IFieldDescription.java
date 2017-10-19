package basic.persistence.model;

	import java.lang.annotation.ElementType;
	import java.lang.annotation.Retention;
	import java.lang.annotation.RetentionPolicy;
	import java.lang.annotation.Target;

	/**
	 * Zur Benutzeradressierten Beschreibung von Feldern (Enum Werten). Auswertung erfolgt in generischem Editor.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface IFieldDescription {
	    /**
	     * Der beschreibende Text für den Anwender.
	     * @return eine Beschreibung des Feldes und was sein Zweck ist.
	     */
	    String description();
	}

package basic.persistence.model;

public interface IKeyEnum<T> {
	/*Gibt den Identifizierungsschlüssel eines Object zurück.
	 * Wid insbesondere bei Schlüsseltabellen des Modells verwendet, die Enum sind.
	 * */
	public T getKey();
}

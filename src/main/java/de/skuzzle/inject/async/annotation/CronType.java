package de.skuzzle.inject.async.annotation;


/**
 * Defines the cron format.
 * 
 * @author Simon
 */
public enum CronType {
	QUARTZ(com.cronutils.model.CronType.QUARTZ),
	UNIX(com.cronutils.model.CronType.UNIX),
	CRON4J(com.cronutils.model.CronType.CRON4J);
	
	private com.cronutils.model.CronType type;

	private CronType(com.cronutils.model.CronType type) {
		this.type = type;
	}
	
	/**
	 * Maps this cron type to the back end's type. (Intended for internal use only).
	 * 
	 * @return The back end's type.
	 */
	public com.cronutils.model.CronType getType() {
		return type;
	}
}

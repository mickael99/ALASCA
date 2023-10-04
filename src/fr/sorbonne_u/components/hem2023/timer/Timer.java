/**
 * 
 */
package fr.sorbonne_u.components.hem2023.timer;

/**
 * @author Yukhoi
 *
 */
public class Timer {

	/**
	 * 
	 */
	private int heure;
	private int minute;
	private int seconde;
	
	public Timer() {
		this.heure = 0;
		this.minute = 0;
		this.seconde = 30;
	}
	
	public Timer(int heure, int minute, int seconde) {
		this.heure = heure;
		this.minute = minute;
		this.seconde = seconde;
	}
	
	public int getHeure() {
		return heure;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getSeconde() {
		return seconde;
	}
	

}

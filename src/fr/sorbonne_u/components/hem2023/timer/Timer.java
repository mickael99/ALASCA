/**
 * 
 */
package fr.sorbonne_u.components.hem2023.timer;

/**
 * @author Yukhoi
 *
 */
public class Timer {
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
	
	public void remove() {
		heure = minute = seconde = 0;
	}
	
	public boolean isFinish() {
		if(heure == 0 && minute == 0 && seconde == 0)
			return true;
		return false;
	}
	
	private int convertTimerToSecond() {
		return heure * 3600 + minute * 60 + seconde;
	}
	
	private static Timer convertSecondToTimer(int seconde) {
		int h = seconde / 3600;
		int m = (seconde % 3600) / 60;
		int s = seconde % 60;
		
		return new Timer(h, m, s);
	}
	
	public Timer differenceBeetweenTwoTimer(Timer timer) {
		int differenceInSecond = Math.abs(this.convertTimerToSecond() - timer.convertTimerToSecond());
		return convertSecondToTimer(differenceInSecond);
	}
	
	public boolean equals(Timer timer) {
		if(heure == timer.getHeure() && 
				minute == timer.getMinute() &&
				seconde == timer.getSeconde())
			return true;
		return false;
	}
}

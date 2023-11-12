/**
 * 
 */
package fr.sorbonne_u.components.hem2023.timer;

/**
 * @author Yukhoi
 *
 */
public class Timer {
	private static final int SECONDS_IN_ONE_DAY = 86400;
	
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
	
	public void testAvailableHour() throws Exception {
		assert heure >= 0 && heure <= 23 : new Exception("ce n'est pas une heure de la journée !");
		assert minute >= 0 && minute <= 59 : new Exception("ce n'est pas une heure de la journée !");
		assert seconde >= 0 && seconde <= 59 : new Exception("ce n'est pas une heure de la journée !");
	}
	
	public void remove() {
		heure = minute = seconde = 0;
	}
	
	public boolean isFinish() {
		if(heure == 0 && minute == 0 && seconde == 0)
			return true;
		return false;
	}
	
	private int convertTimerToSecond() throws Exception {
		testAvailableHour();
		return heure * 3600 + minute * 60 + seconde;
	}
	
	private static Timer convertSecondToTimer(int seconde) {
		assert seconde >= 0 & seconde <= SECONDS_IN_ONE_DAY : new Exception("le nombre de seconde est invalide");
		
		int h = seconde / 3600;
		int m = (seconde % 3600) / 60;
		int s = seconde % 60;
		
		return new Timer(h, m, s);
	}
	
	public Timer differenceBeetweenTwoTimer(Timer endTime) throws Exception {
		int endTimeInSecond = endTime.convertTimerToSecond();
		int launchTimeInSecond = this.convertTimerToSecond();
		
		if(endTimeInSecond >= launchTimeInSecond)
			return convertSecondToTimer(endTimeInSecond - launchTimeInSecond);
		
		return convertSecondToTimer(SECONDS_IN_ONE_DAY - launchTimeInSecond + endTimeInSecond);
	}
	
	public boolean equals(Timer timer) {
		if(heure == timer.getHeure() && 
				minute == timer.getMinute() &&
				seconde == timer.getSeconde())
			return true;
		return false;
	}
	
	public String toString() {
		return heure + "h " + minute + "min " + seconde + "sec";
	}
	
	private void decreaseTime(int seconds) {
	    int totalSeconds = 0;
		try {
			totalSeconds = convertTimerToSecond();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    totalSeconds -= seconds;

	    if (totalSeconds < 0) {
	        totalSeconds += SECONDS_IN_ONE_DAY;
	    }

	    heure = totalSeconds / 3600;
	    minute = (totalSeconds % 3600) / 60;
	    seconde = totalSeconds % 60;
	}
	
	public void runTimer() {
	    while (!isFinish()) {
	        try {
	            System.out.println(this); // Print the current time
	            Thread.sleep(1000); // Sleep for 1 second (1000 milliseconds)
	            decreaseTime(1); // Decrease the time by 1 second
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	    System.out.println("Timer has reached 0.");
	}
}

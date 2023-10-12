package fr.sorbonne_u.components.hem2023.timer;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class TestTimer {
	@Test
	public void testGetters() {
		Timer timer = new Timer(1, 30, 45);
		assertEquals(1, timer.getHeure());
		assertEquals(30, timer.getMinute());
		assertEquals(45, timer.getSeconde());
	}
	
	@Test
	public void testRemove() {
		Timer timer = new Timer(1, 30, 45);
		timer.remove();
		assertEquals(0, timer.getHeure());
		assertEquals(0, timer.getMinute());
		assertEquals(0, timer.getSeconde());
	}
	
	@Test
	public void testIsFinish() {
		Timer timer = new Timer(1, 30, 45);
		assertTrue(!timer.isFinish());
		
		timer.remove();
		assertTrue(timer.isFinish());
	}
	
	@Test
	public void testEquals() {
		Timer timer1 = new Timer(1, 30, 45);
		Timer timer2 = new Timer(1, 30, 45);
		assertTrue(timer1.equals(timer2));
		
		timer1.remove();
		assertTrue(!timer1.equals(timer2));
	}
	
	@Test
	public void testDifferenceBeetweenTwoTimers() {
		Timer timer1 = new Timer(1, 30, 0);
		Timer timer2 = new Timer(7, 20, 50);
		Timer expectedAnswer = new Timer(5, 50, 50);
		assertTrue(timer1.differenceBeetweenTwoTimer(timer2).equals(expectedAnswer));
	}
}

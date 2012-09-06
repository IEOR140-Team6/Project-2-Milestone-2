package com.mydomain;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.*;

/* Date: September 6th, 2012
 * Class Name: Tracker
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to track lines and make figure-8 shapes around the track
 * 
 */

/*
 * Robot uses tracker.java to navigate. This contains the main method to operate tracker.
 */
public class Milestone1
{ 
	/*
	 * The main method causes the robot to run calibrate, trackLine, and figureEight.
	 * We created instance variables myPilot, myLeftSensor, myRightSensor, and tracker
	 * to run the robot.
	 */
  	public static void main(String[] args)
  	{
  		DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),4.8f,Motor.A,Motor.B,false);
  		LightSensor myLeftLightSensor = new LightSensor(SensorPort.S1);
  		LightSensor myRightLightSensor = new LightSensor(SensorPort.S4);
  		
  		Tracker tracker = new Tracker(myPilot, myLeftLightSensor, myRightLightSensor);
  	
  		tracker.calibrate(); //Calibrate tracker/the robot.
  		tracker.trackLine(); //The robot will run trackLine(), going 4 circuits, rotate, then 4 back.
  		tracker.stop(); //Robot stops.
  		Delay.msDelay(2000); //2 second delay
  		tracker.figureEight(); //Robot starts doing the figure 8.
  	}
}
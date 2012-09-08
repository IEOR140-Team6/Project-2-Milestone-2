package com.mydomain;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.*;

/* Date: September 7th, 2012
 * Class Name: Milestone2X
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to track lines and make figure-8 shapes around the track
 * 
 */

/*
 * Robot uses tracker.java to navigate. This contains the main method to operate tracker.
 */
public class Milestone2X
{ 
	/*
	 * The main method causes the robot to run calibrate, leftSquare, rightSquare, and roundTrip.
	 * We created instance variables myPilot, myLeftSensor, myRightSensor, and tracker
	 * to run the robot.
	 */
  	public static void main(String[] args)
  	{
  		DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),4.8f,Motor.A,Motor.B,false);
  		LightSensor myLeftLightSensor = new LightSensor(SensorPort.S1);
  		LightSensor myRightLightSensor = new LightSensor(SensorPort.S4);
  		
  		Tracker2 tracker = new Tracker2(myPilot, myLeftLightSensor, myRightLightSensor);
  	
  		tracker.calibrate(); //Calibrate tracker/the robot.
  		tracker.leftSquare(); //Robot completes 2x2 square, making left turns
  		tracker.rightSquare(); //Robot completes 2x2 square, making right turns
  		tracker.roundTrip(); //Robot shuffles back and forth, twice via left turns, twice via right
  	}
}
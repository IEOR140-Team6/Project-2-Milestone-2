/* Date: September 7th, 2012
 * Class Name: Tracker
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to track lines
 * 
 */
package com.mydomain;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.*;

/**
This class needs a higher level controller to implement the navigtion logic<br>
Responsibility: keep robot on the line till it senses a marker, then stop <br>
also controls turning to a new line at +- 90 or 180 deg<br>
Hardware: Two light sensors , shielded, 2 LU above floor.
Classes used:  Pilot, LightSensors<br>
Control Algorithm:  proportional control. estimate distance from centerline<br>
Calibrates both sensors to line, background
Updated 9/10/2007  NXT hardware
@author Roger Glassey
 */
public class Tracker
{
  //controls the motors
  public DifferentialPilot pilot;
  
  //set by constructor , used by trackLine()
  private LightSensor leftEye;
  
  //set by constructor , used by trackLine()
  private LightSensor rightEye; 

  /**
   *constructor - specifies which sensor ports are left and right
   *public Tracker(Pilot thePilot,SensorPort leftI,SensorPort rightI)
   */
  
  // Tracker constructor
  public Tracker(DifferentialPilot thePilot, LightSensor leftEye , LightSensor  rightEye)
  {
    pilot = thePilot;
    pilot.setTravelSpeed(6);
    pilot.setRotateSpeed(50);
    pilot.setAcceleration(20);
    this.leftEye = leftEye;
    this.leftEye.setFloodlight(true);
    this.rightEye = rightEye;
    this.rightEye.setFloodlight(true);
  }


  //Allows the robot to track the blue line until it hits black tape
  public void trackLine()
  {   
	while(true)
	{
		float gain = 0.7f;// set gain to float 0.7   	                                                 
		int lval = leftEye.getLightValue(); //gets left sensor's light value
		int rval = rightEye.getLightValue();//gets right sensor's light value
		int error = CLDistance(lval, rval); //determine error as lval-rval
		int control = (int) (error*gain);   //control is product of error and gain
		pilot.steer(control);			    //pilot uses control as a steering value	
		
		if (lval<0||rval<0) //if robot encounters black tape, loop breaks
		{
			break;
		}
	}
  }
  
  //Determines the value of the left sensor
  int lvalue()
  {
	return leftEye.getLightValue();
  }

  //Determines the value of the right sensor
  int rvalue()
  {
	return rightEye.getLightValue();
  }
  
  //Tells the robot to rotate at a certain angle
  public void rotate(int angle)
  {
	pilot.travel(2);
	pilot.rotate(angle);
  }
  
  
  /**
   * helper method for Tracker; calculates distance from centerline, used as error by trackLine()
   * @param left light reading
   * @param right light reading
   * @return  distance
   */
  int CLDistance(int left, int right)
  {	
   return left-right;
  }
   
  /*
   * Tells the robot to stop.
   */
  public void stop()
  {
    pilot.stop();
  }

  /**
  calibrates for line first, then background, then marker with left sensor.  displays light sensor readings on LCD (percent)<br>
  Then displays left sensor (scaled value).  Move left sensor  over marker, press Enter to set marker value to sensorRead()/2
   */
  
  // calibrates light sensors
  public void calibrate()
  {
      System.out.println("Calibrate Tracker");
    
      // for-loop for sensor calibration 
      for (byte i = 0; i < 3; i++)
      {
    	  // calibrating two times (first "LOW" one for blue tape, second "HIGH" one for white part)
        while (0 == Button.readButtons())//wait for press
        {
          LCD.drawInt(leftEye.getLightValue(), 4, 6, 1 + i);
          LCD.drawInt(rightEye.getLightValue(), 4, 12, 1 + i);
          if (i == 0)
          {
            LCD.drawString("LOW", 0, 1 + i);
          } else if (i == 1)
          {
            LCD.drawString("HIGH", 0, 1 + i);
          } 
        }
        
        // Varying degree of beeping sounds
        Sound.playTone(1000 + 200 * i, 100);
        if (i == 0)
        {
          leftEye.calibrateLow();
          rightEye.calibrateLow();
        } else if (i == 1)
        {
          rightEye.calibrateHigh();
          leftEye.calibrateHigh();
        } 
        while (0 < Button.readButtons())
        {
          Thread.yield();//button released
        }
       
    }
    while (0 == Button.readButtons())// while no press
    {
      int lval = leftEye.getLightValue();
      int rval = rightEye.getLightValue();
      LCD.drawInt(lval, 4, 0, 5);
      LCD.drawInt(rval, 4, 4, 5);
      LCD.drawInt(CLDistance(lval, rval), 4, 12, 5);
      LCD.refresh();
    }
    LCD.clear();
  }
  }




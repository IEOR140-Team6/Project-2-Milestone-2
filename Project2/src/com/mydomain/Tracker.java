/* Date: September 6th, 2012
 * Class Name: Tracker
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to track lines and make figure-8 shapes around the track
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

  /* 
   * Robot follows the blue tape around the oval (4 complete circuits).
   * After 4 complete circuits, the robot will turn around and complete 4 circuits
   * in the opposite direction.
   * Robot uses black tape to identify how many circuits it made.
   */
  public void trackLine()
  {    
	int i=0; //initialize i = number of times robot approaches black tape
	
	//this loop occurs until the robot approaches the black tape 16 times (or 8 loops)
	while (i<=16)
	{
		float gain = 0.7f;// set gain to float 0.7   	                                                 
		int lval = leftEye.getLightValue(); //gets left sensor's light value
		int rval = rightEye.getLightValue();//gets right sensor's light value
		int error = CLDistance(lval, rval); //determine error as lval-rval
		int control = (int) (error*gain);   //control is product of error and gain
		pilot.steer(control);			    //pilot uses control as a steering value

		//tells the robot what to do when it approaches the black tape
		if (lval<-7||rval<-7)
		{
			i=i+1; //increase the "robot black tape approach count" by 1		
			if (i==8) //if it's the 8th time the robot approaches black tape, turn around
			{
				pilot.stop();
				pilot.rotate(180);
			}
			else //else the robot just goes past the black tape and continues its journey
			{
				while(lval<-7||rval<-7) //continue steering past the black tape
				{
					gain = 0.7f;    	                                                 
					lval = leftEye.getLightValue();
					rval = rightEye.getLightValue(); 
					error = CLDistance(lval, rval);
					control = (int) (error*gain);
					pilot.steer(control);
				}
			}
		}
	}
  }

  /**
	  In figureEight() method, we implemented two different kinds of arrays,
	  lval_array (and rval_array): both are int arrays and turnRight_orNot:
	  boolean array
	  
	  1) lval_array (and rval_array):
	  
	  This array stores two different int values. One is current light
	  value, and the other one is a light value recorded immediately before
	  the current light value. Through printing out sets of light values 
	  (by using system.out.println), I discovered that BOTH left and right sensor light 
	  values decrease when the robot hits the black spot, as well as the sum.
	  
	  Now, how do we distinguish the differences between the sums when 
	  the robot is on the black spot and when it is not? 
	  
	  We employed confidence interval model, and for the first trial, we
	  implemented "if sum of left & right light values at black spot are
	  less than 80% of previous record at non-black spot, then the robot
	  must turn". But the robot stopped at black spots only for few times.
	  So we tried 90%, and it still occasionally did not recognize few black
	  spots. When we finally tried 95%, the robot recognized at every single
	  black spot.
	  
	  2) turnRight_orNot array
	   (please see the description below)
	 **/
  public void figureEight()
  {	
		pilot.forward();
		int[] lval_array = new int[2];
		int[] rval_array = new int[2];
		lval_array[0] = 0; // initializing sensor light values
		rval_array[0] = 0;
		boolean[] turnRight_orNot = new boolean[2];
		turnRight_orNot[0] = true; // initializing directions
		turnRight_orNot[1] = false;
		
		while (true) 
		{
			float gain = 0.7f;
			int lval = leftEye.getLightValue();
			int rval = rightEye.getLightValue();

			// setting a previous record as a current record
			// Below is the graphic representation of our record transfer in
			// this loop.
			//
			// _ O O _ _ _ --> _ _ O O _ _ : here is a graphic representation!

			lval_array[1] = lval_array[0];
			rval_array[1] = rval_array[0];
			lval_array[0] = lval; // recording new values for current records (left)
			rval_array[0] = rval; // recording new values for current records (right)

			int error = CLDistance(lval_array[0], rval_array[0]);
			int control = (int) (error * gain);

			// if condition for recognizing black stops
			if (lval_array[0] < lval_array[1]
					&& rval_array[0] < rval_array[1]
					&& lval_array[0] + rval_array[0] < 0.95 * (lval_array[1] + rval_array[1])) 
			{
				// first, STOP! And let the robot determine which direction it
				// should turn
				pilot.stop();
				
				/** 2) turnRight_orNot:
				
				 This array contains two entries: 
				 
				 -- 0th index is record of direction the robot JUST turned,
				 -- and 1st index is one before the current turning direction record.
				
				 After tracing the 8-figure, I discovered a pattern that the
				robot turns:
				R -> L -> L -> R -> R -> L -> L -> R ...
				
				  In other words, based on the current turning direction and
				the one before that,
				one can successfully determine what the next turning
				direction should be.
				The next four if statements are implemented simply to
				determine these turning
				directions. **/

				// last two turns were LEFT & RIGHT
				if (turnRight_orNot[1] == true && turnRight_orNot[0] == false)
				{
					pilot.travel(2);
					pilot.rotate(-90); // LEFT TURN
					turnRight_orNot[1] = false; // one direction record before the current direction
					turnRight_orNot[0] = false; // the direction that was just turned upon
				}

				// last two turns were LEFT & RIGHT
				else if (turnRight_orNot[1] == false && turnRight_orNot[0] == false)
				{
					pilot.travel(2);
					pilot.rotate(90); // RIGHT TURN
					turnRight_orNot[1] = false;
					turnRight_orNot[0] = true;
				}
				
				// last two turns were LEFT & RIGHT
				else if (turnRight_orNot[1] == false && turnRight_orNot[0] == true)
				{
					Motor.B.rotate(386); // RIGHT TURN
					turnRight_orNot[1] = true;
					turnRight_orNot[0] = true;
				}

				// last two turns were RIGHT & RIGHT
				else if (turnRight_orNot[1] == true && turnRight_orNot[0] == true)
				{
					Motor.A.rotate(386); // LEFT TURN
					turnRight_orNot[1] = true;
					turnRight_orNot[0] = false;
				}
			}
			pilot.steer(control);
		}
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




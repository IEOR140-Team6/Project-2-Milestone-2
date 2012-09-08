/* Date: September 7th, 2012
 * Class Name: Tracker2
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to track lines and make figure-8 shapes around the track
 * 
 */
package com.mydomain;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.*;
import lejos.util.Delay;

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
public class Tracker2
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
  public Tracker2(DifferentialPilot thePilot, LightSensor leftEye , LightSensor  rightEye)
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

  //Robot will follow a 2x2 square, turning left
  public void leftSquare()
  {	
	  int i=0; //initialize i = number of times robot approaches black tape
		
		//this loop occurs until the robot approaches the black tape 16 times (or 2 squares)
		while (i<16)
		{
			float gain = 0.7f;// set gain to float 0.7   	                                                 
			int lval = leftEye.getLightValue(); //gets left sensor's light value
			int rval = rightEye.getLightValue();//gets right sensor's light value
			int error = CLDistance(lval, rval); //determine error as lval-rval
			int control = (int) (error*gain);   //control is product of error and gain
			pilot.steer(control);			    //pilot uses control as a steering value

			//tells the robot what to do when it approaches the black tape
			if (lval<-8||rval<-8)
			{
				Sound.beep(); //makes a sound
				i=i+1; //increase the "robot black tape approach count" by 1		
				/*
				if it's the second time it hits black tape but not the 16th,
				turn left
				*/
				if (i%2==0&&i!=16)
				{
					pilot.travel(2);
					pilot.rotate(-90);
				}
				//if it's the 16th time (2 squares), stop, then turn 180 degrees
				else if (i==16)
				{
					pilot.stop();
					Delay.msDelay(1100);
					pilot.rotate(180);
				}
				else //else the robot just goes past the black tape and continues its journey
				{
					while(lval<-8||rval<-8) //continue steering past the black tape
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
  
  //Robot will follow a 2x2 square, turning right
  public void rightSquare()
  {	
	  int i=0; //initialize i = number of times robot approaches black tape
		
		//this loop occurs until the robot approaches the black tape 16 times (or 2 squares)
		while (i<16)
		{	
			float gain = 0.7f;// set gain to float 0.7   	                                                 
			int lval = leftEye.getLightValue(); //gets left sensor's light value
			int rval = rightEye.getLightValue();//gets right sensor's light value
			int error = CLDistance(lval, rval); //determine error as lval-rval
			int control = (int) (error*gain);   //control is product of error and gain
			pilot.steer(control);			    //pilot uses control as a steering value

			//tells the robot what to do when it approaches the black tape
			if (lval<-8||rval<-8)
			{
				Sound.beep(); //make a sound
				i=i+1; //increase the "robot black tape approach count" by 1		
				if (i%2==0) //if the nth approach is even, then turn right
				{
					pilot.travel(2);
					pilot.rotate(90);
				}
				else //else the robot just goes past the black tape and continues its journey
				{
					while(lval<-8||rval<-8) //continue steering past the black tape
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
  
  	/*
  	 Robot will shuffle between (0,2) and (0,4) twice, turning left, then between (0,2)
  	 and (0,0) twice, turning right
  	*/
	public void roundTrip()
	{
		int i=0; //initialize i = number of times robot approaches black tape
		
		//loop ends when robot completes the shuffle (passes through 16 black tapes)
		while (i<16)
		{	
			float gain = 0.7f;// set gain to float 0.7   	                                                 
			int lval = leftEye.getLightValue(); //gets left sensor's light value
			int rval = rightEye.getLightValue();//gets right sensor's light value
			int error = CLDistance(lval, rval); //determine error as lval-rval
			int control = (int) (error*gain);   //control is product of error and gain
			pilot.steer(control);			    //pilot uses control as a steering value

			//tells the robot what to do when it approaches the black tape
			if (lval<-8||rval<-8)
			{
				i=i+1; //increase the "robot black tape approach count" by 1
				Sound.beep(); //make a sound
				if (i<8&&i%2==0) //if the nth approach is even and less than 8, turn around via left
				{
					pilot.travel(2);
					pilot.rotate(-180); //rotate 180 degrees left-wise
				}
				else if (i==8) //if it's the 8th time, don't rotate
				{
					pilot.travel(2);
					pilot.rotate(0);
				}
				else if (i>8&&i%2==0) //if nth approach is even and greater than 8, turn around via right
				{
					pilot.travel(2);
					pilot.rotate(180); //rotate 180 degrees right-wise
				}
				else //else the robot just goes past the black tape and continues its journey
				{
					while(lval<-8||rval<-8) //continue steering past the black tape
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




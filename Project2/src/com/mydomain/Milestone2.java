package com.mydomain;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.*;

/* Date: September 7th, 2012
 * Class Name: Tracker
 * Authors: MoonSoo Choi and Sherman Siu
 * Program description: program a robot to make squares and shuffle around the track
 * 
 */

/*
 * Robot uses tracker.java to navigate. This contains the main method to operate tracker.
 */
public class Milestone2
{ 	
	//Constructs the tracker for Milestone2 (pulls methods from Tracker.java)
	DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),4.8f,Motor.A,Motor.B,false);
	LightSensor myLeftLightSensor = new LightSensor(SensorPort.S1);
	LightSensor myRightLightSensor = new LightSensor(SensorPort.S4);
	Tracker tracker = new Tracker(myPilot, myLeftLightSensor, myRightLightSensor);
	
	//Calibrates the robot
	void calibrate()
  	{
  		tracker.calibrate();
  	}
		
	//Robot completes two 2x2 squares, turning left each time
  	void leftSquare()
  	{
  		int i = 0; //initialize i = # of times robot approaches black tape
  		while (i<16) //loops until robot encounters 16 black tapes/2 squares
  		{
  			int lval = tracker.lvalue(); //determine left sensor light value
  			int rval = tracker.rvalue(); //determine right sensor light value
  			tracker.trackLine(); //follow the blue tape		
  			 
  			//tells the robot what to do when it approaches the black tape
  			if (lval<0||rval<0)
  			{
  				Sound.beep(); //makes a sound
  				i=i+1; //increase the "robot black tape approach count" by 1		
				//if the nth black tape approach is even, turn left
  				if (i%2==0)
  				{
  					tracker.rotate(-90);
  				}
  				//else the robot just goes past the black tape and continues its journey
  				else 
  				{
  					tracker.rotate(0);
  				}
  			}
  		}
  	}
  	 
  	//Robot completes two 2x2 squares, turning right each time 
  	void rightSquare()
  	{
   		int i = 0; //initialize i = # of times robot approaches black tape
   		while (i<16) //loops until robot encounters 16 black tapes/2 squares
   		{
   			int lval = tracker.lvalue(); //determine left sensor light value
   			int rval = tracker.rvalue(); //determine right sensor light value
   			tracker.trackLine(); //follow the blue tape		
   			 
   			//tells the robot what to do when it approaches the black tape
   			if (lval<0||rval<0)
   			{
   				Sound.beep(); //makes a sound
   				i=i+1; //increase the "robot black tape approach count" by 1		
 				//if the nth black tape approach is even, turn left
   				if (i%2==0)
   				{
   					tracker.rotate(-90);
   				}
   				//else the robot just goes past the black tape and continues its journey
   				else 
   				{
   					tracker.rotate(0);
   				}
   			}
   		}
   	}
  	
  	/*
  	Robot shuffles between the points (0,2) and (0,4) twice, turning 180 degrees
  	counterclockwise. Then the robot shuffles between (0,2) and (0,0), turning
  	180 degrees clockwise.
  	*/
    void roundTrip()
    {
      	int i = 0; //initialize i = # times robot approaches black tape
      	while (i<16) //loops until completes the entire task
      	{
      		int lval = tracker.lvalue(); //determine left sensor light value
      		int rval = tracker.rvalue(); //determine right sensor light value
      		tracker.trackLine(); //follow the blue tape
    		
      		//tells the robot what to do when it approaches the black tape
      		if (lval<0||rval<0)
      		{
      			Sound.beep(); //makes a sound
      			i=i+1; //increase the "robot black tape approach count" by 1		
      			/*
    			if the nth approach is even and less than 8, then turn 180
    			degrees counterclockwise
      			*/
      			if (i<8&&i%2==0)
      			{
      				tracker.rotate(-180);
      			}
      			/*
      			Else if nth approach is even and greater than 8, turn 180
      			degrees clockwise 
      			*/
      			else if (i>8&&i%2==0)
      			{
      				tracker.rotate(180);
      			}
           		else //else the robot just goes past the black tape and continues its journey
      			{
      				tracker.rotate(0);
      			}
      		}
      	}
   }
  	  
   /*
    * The main method causes the robot to run calibrate, leftSquare, rightSquare, and roundTrip.
    * We created instance variables myPilot and myMilestone2 to run the robot.
    */
    public static void main(String[] args)
      {	 
    	DifferentialPilot myPilot = new DifferentialPilot((float)(56/25.4),4.8f,Motor.A,Motor.B,false);
  		Milestone2 myMilestone2 = new Milestone2();
  
      	myMilestone2.calibrate(); //Calibrate robot
      	myMilestone2.leftSquare(); //Make left squares
      	myPilot.rotate(-90); //Rotate left 90 degress for setup reasons
      	myMilestone2.rightSquare(); //Make right squares
      	myMilestone2.roundTrip(); //Shuffle
      } 	  
}


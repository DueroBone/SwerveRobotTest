// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.SyncedLibraries.SystemBases.ControllerBase;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  /*
   * Wanted controls:
   * Trigger (0): Full drive speed (disable rpm based speed control)
   * 2: Disable driving and joystick controls angle
   * 3/4: Enable/disable field oriented driving
   * 5: Reset gyro
   * Throttle: Change max speed
   * POV: change rotation center
   */

  private RobotContainer m_robotContainer;
  ControllerBase joystick = m_robotContainer.controller;

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void disabledExit() {
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void autonomousExit() {
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    if (joystick.getRawButton(7)) {
      m_robotContainer.drivetrain.inputDrivingX_Y(-joystick.getLeftY(),
          joystick.getLeftX(), -joystick.getRightX(), joystick.getPOV());
    }

    SmartDashboard.putNumber("Joystick X", joystick.getLeftX());
    SmartDashboard.putNumber("Joystick Y", joystick.getLeftY());
    SmartDashboard.putNumber("Joystick Twist", joystick.getRightX());
    // SmartDashboard.putNumber("Joystick Throttle", joystick.getThrottle());
    SmartDashboard.putNumber("Joystick POV", joystick.getPOV());
  }

  @Override
  public void teleopExit() {
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {
  }

  @Override
  public void testExit() {
  }
}

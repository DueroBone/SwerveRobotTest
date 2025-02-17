// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems.Swerve;

import com.revrobotics.CANSparkMax;

import frc.robot.SyncedLibraries.SystemBases.Swerve.SwerveModuleBase;

public class SwerveModule extends SwerveModuleBase {

  public SwerveModule(CANSparkMax driveMotor, CANSparkMax turningMotor, double turningOffset, String name) {
    super(driveMotor, turningMotor, turningOffset, name);
  }
}
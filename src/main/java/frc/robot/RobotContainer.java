// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {
  Drivetrain drivetrain = new Drivetrain(3, 3, new SwerveModule[] {
      new SwerveModule(new CANSparkMax(3, MotorType.kBrushless), new CANSparkMax(4, MotorType.kBrushless)),
      new SwerveModule(new CANSparkMax(1, MotorType.kBrushless), new CANSparkMax(2, MotorType.kBrushless)),
      new SwerveModule(new CANSparkMax(5, MotorType.kBrushless), new CANSparkMax(6, MotorType.kBrushless)),
      new SwerveModule(new CANSparkMax(7, MotorType.kBrushless), new CANSparkMax(8, MotorType.kBrushless))
  });

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}

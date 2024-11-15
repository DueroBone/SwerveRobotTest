// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsystems.Swerve;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkAbsoluteEncoder;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SwerveModule extends SubsystemBase {
  private final CANSparkMax m_driveMotor;
  private final CANSparkMax m_turningMotor;

  private final RelativeEncoder m_driveEncoder;
  private final SparkAbsoluteEncoder m_turningEncoder;

  private final PIDController m_drivePIDController;
  private final PIDController m_turnPIDController;

  private final double driveGearRatio = 10 * Math.PI * 1; // 1 is the gear ratio when I find out
  private final double turnGearRatio = 27 / 5;

  /**
   * Constructs a new SwerveModule.
   *
   * @param driveMotor The motor that drives the module.
   * @param turningMotor The motor that turns the module.
   * @param turningOffset The offset for the turning encoder. Starting position
   * @param name The name of the module. Ie. "FrontLeft"
   */
  public SwerveModule(CANSparkMax driveMotor, CANSparkMax turningMotor, double turningOffset, String name) {
    this.setName(name);

    // DRIVE MOTOR SETUP
    m_driveMotor = driveMotor;

    m_driveMotor.restoreFactoryDefaults();
    m_driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_driveMotor.setSmartCurrentLimit(10); // will increase

    m_driveEncoder = m_driveMotor.getEncoder();
    m_driveEncoder.setPositionConversionFactor(driveGearRatio);
    m_driveEncoder.setVelocityConversionFactor(driveGearRatio);

    m_drivePIDController = new PIDController(0.1, 0, 0.01);

    // TURNING MOTOR SETUP
    m_turningMotor = turningMotor;

    m_turningMotor.restoreFactoryDefaults();
    m_turningMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    m_turningMotor.setSmartCurrentLimit(10);

    m_turningEncoder = m_turningMotor.getAbsoluteEncoder();
    m_turningEncoder.setPositionConversionFactor(turnGearRatio);
    m_turningEncoder.setZeroOffset(turningOffset);

    m_turnPIDController = new PIDController(0.1, 0, 0.01);
    m_turnPIDController.enableContinuousInput(0, turnGearRatio);

    m_turningMotor.burnFlash();
    m_driveMotor.burnFlash();
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    // return new SwerveModuleState(
    // m_driveEncoder.getVelocity(), new
    // Rotation2d(m_turningEncoder.getPosition()));
    return new SwerveModuleState(
        m_driveMotor.get(), new Rotation2d(convertToRadians(m_turningEncoder.getPosition())));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(
        m_driveEncoder.getPosition(), new Rotation2d(convertToRadians(m_turningEncoder.getPosition())));
  }

  public CANSparkMax getTurnMotor() {
    return m_turningMotor;
  }

  public CANSparkMax getDriveMotor() {
    return m_driveMotor;
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    Rotation2d encoderRotation = new Rotation2d(m_turningEncoder.getPosition());

    // Optimize the reference state to avoid spinning further than 90 degrees
    // SwerveModuleState state = SwerveModuleState.optimize(desiredState, encoderRotation);
    SwerveModuleState state = desiredState; // TODO: Switch back after debugging

    // Scale speed by cosine of angle error. This scales down movement perpendicular
    // to the desired direction of travel that can occur when modules change
    // directions. This results in smoother driving.
    state.speedMetersPerSecond *= state.angle.minus(encoderRotation).getCos();

    // set the wanted position, actual moving done in periodic
    m_drivePIDController.setSetpoint(state.speedMetersPerSecond);
    m_turnPIDController.setSetpoint(state.angle.getRadians());
  }

  private double covertFromRadians(double radians) {
    return (radians + Math.PI) / (2 * Math.PI) * turnGearRatio;
  }

  private double convertToRadians(double position) {
    return (position - (turnGearRatio / 2)) * (2 * Math.PI);
  }

  @Override
  public void periodic() {

    m_turningMotor.set(m_turnPIDController.calculate(
        getState().angle.getRadians()));

    // m_driveMotor.set(m_drivePIDController.calculate(
    //     m_driveEncoder.getVelocity()));\
    m_driveMotor.set(0); // TODO: Switch back after debugging

    SmartDashboard.putData(this.getName() + " swerve turning PID", m_turnPIDController);


    SmartDashboard.putData(this.getName() + " swerve driving PID", m_drivePIDController);
  }

  public void setDriveBrakeMode(boolean brake) {
    if (brake) {
      m_driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    } else {
      m_driveMotor.setIdleMode(CANSparkMax.IdleMode.kCoast);
    }
  }
}
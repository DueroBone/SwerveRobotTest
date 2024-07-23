// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Represents a swerve drive style drivetrain. */
public class Drivetrain {
    public static final double kMaxSpeed = 3.0; // 3 meters per second
    public static final double kMaxAngularSpeed = Math.PI; // 1/2 rotation per second

    // For a square 3ft x 3ft robot, the wheelbase is 0.381 meters from center to
    // module.
    private final double _robotLength = Units.feetToMeters(3);
    private final double _robotWidth = Units.feetToMeters(3);
    private final double _robotLengthOffset = _robotLength / 2;
    private final double _robotWidthOffset = _robotWidth / 2;

    private final Translation2d m_frontLeftLocation = new Translation2d(_robotWidthOffset, _robotLengthOffset);
    private final Translation2d m_frontRightLocation = new Translation2d(_robotWidthOffset, -_robotLengthOffset);
    private final Translation2d m_backLeftLocation = new Translation2d(-_robotWidthOffset, _robotLengthOffset);
    private final Translation2d m_backRightLocation = new Translation2d(-_robotWidthOffset, -_robotLengthOffset);

    private final SwerveModule m_frontLeft = new SwerveModule(1, 2);
    private final SwerveModule m_frontRight = new SwerveModule(3, 4);
    private final SwerveModule m_backLeft = new SwerveModule(5, 6);
    private final SwerveModule m_backRight = new SwerveModule(7, 8);

    int counter = 0;

    private final AHRS m_gyro = new AHRS();

    private final SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(
            m_frontLeftLocation, m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

    private final SwerveDriveOdometry m_odometry = new SwerveDriveOdometry(
            m_kinematics,
            m_gyro.getRotation2d(),
            new SwerveModulePosition[] {
                    m_frontLeft.getPosition(),
                    m_frontRight.getPosition(),
                    m_backLeft.getPosition(),
                    m_backRight.getPosition()
            });

    public Drivetrain() {
        m_gyro.reset();
    }

    /**
     * Method to drive the robot using joystick info.
     *
     * @param xSpeed        Speed of the robot in the x direction (forward).
     * @param ySpeed        Speed of the robot in the y direction (sideways).
     * @param rot           Angular rate of the robot.
     * @param fieldRelative Whether the provided x and y speeds are relative to the
     *                      field.
     */
    public void drive(
            double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
        var swerveModuleStates = m_kinematics.toSwerveModuleStates(
                ChassisSpeeds.discretize(
                        fieldRelative
                                ? ChassisSpeeds.fromFieldRelativeSpeeds(
                                        xSpeed, ySpeed, rot, m_gyro.getRotation2d())
                                : new ChassisSpeeds(xSpeed, ySpeed, rot),
                        0.02),
                new Translation2d(0, 0)); // Center of rotation // TODO: Modifiable
        SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, kMaxSpeed);
        counter++;
        if (counter % 10 == 0) {
            // System.out.println("Speeds: " + swerveModuleStates[0].speedMetersPerSecond + " "
            //         + swerveModuleStates[1].speedMetersPerSecond + " " + swerveModuleStates[2].speedMetersPerSecond
            //         + " " + swerveModuleStates[3].speedMetersPerSecond);
            System.out.println("Angles: " + swerveModuleStates[0].angle.getDegrees() + " "
                    + swerveModuleStates[1].angle.getDegrees() + " " + swerveModuleStates[2].angle.getDegrees() + " "
                    + swerveModuleStates[3].angle.getDegrees());
        }
        m_frontLeft.setDesiredState(swerveModuleStates[0]);
        // m_frontRight.setDesiredState(swerveModuleStates[1]);
        // m_backLeft.setDesiredState(swerveModuleStates[2]);
        // m_backRight.setDesiredState(swerveModuleStates[3]);

        SmartDashboard.putNumber("Speed1", m_frontLeft.getState().speedMetersPerSecond);
        SmartDashboard.putNumber("TargAngle1", m_frontLeft.getState().angle.getDegrees());
        SmartDashboard.putNumber("CurrAngle1", m_frontLeft.getPosition().angle.getDegrees());
        SmartDashboard.putNumber("Power1", m_frontLeft.getTurnMotor().get());

        updateOdometry();
    }

    /** Updates the field relative position of the robot. */
    public void updateOdometry() {
        m_odometry.update(
                m_gyro.getRotation2d(),
                new SwerveModulePosition[] {
                        m_frontLeft.getPosition(),
                        m_frontRight.getPosition(),
                        m_backLeft.getPosition(),
                        m_backRight.getPosition()
                });
    }
}
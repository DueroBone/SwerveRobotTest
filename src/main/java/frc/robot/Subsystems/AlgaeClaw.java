package frc.robot.Subsystems;

import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants;
import frc.robot.SyncedLibraries.SystemBases.ManipulatorBase;

public class AlgaeClaw extends ManipulatorBase {

    public AlgaeClaw() {
        addMotors(new CANSparkMax(Constants.Wirings.algaeClawMotor, CANSparkMax.MotorType.kBrushless));
        setBrakeMode(true);
        setCurrentLimit(Constants.AlgaeClaw.currentLimit);
    }

    public void intake() {
        setPower(1);
    }

    public void outtake() {
        setPower(-1);
    }

    @Override
    public Command test() {
        return new SequentialCommandGroup(
                new InstantCommand(() -> setPower(1)),
                new WaitCommand(1),
                new InstantCommand(() -> setPower(-1)),
                new WaitCommand(1),
                new InstantCommand(() -> setPower(0)));
    }

    @Override
    public void ESTOP() {
        setBrakeMode(false);
        fullStop();
    }
}

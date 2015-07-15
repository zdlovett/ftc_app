package org.swerverobotics.library.examples;

import com.qualcomm.robotcore.hardware.*;
import com.qualcomm.robotcore.util.*;
import org.swerverobotics.library.*;
import org.swerverobotics.library.Gamepad;

/**
 * A very simple example of a synchronous op mode.
 */
public class TeleOp extends SynchronousOpMode
    {
    // All hardware variables can only be initialized inside the main() function,
    // not here at their member variable declarations.
    DcMotor motorLeft = null;
    DcMotor motorRight = null;

    @Override protected void main()
        {
        // Initialize our hardware variables
        this.motorLeft = this.hardwareMap.dcMotor.get("motorLeft");
        this.motorRight = this.hardwareMap.dcMotor.get("motorRight");

        // Configure the knobs of the hardware according to how you've wired your
        // robot. Here, we assume that there are no encoders connected to the motors,
        // so we inform the motor objects of that fact.
        this.motorLeft.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        this.motorRight.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        // Enter an infinite loop processing all the input we receive
        for (;;)
            {
            if (this.gamePadInputAvailable())
                {
                // There is (likely) new gamepad input available.
                // Do something with that! Here, we just drive.
                this.doManualDrvingControl(this.gamepad1);
                }
            else
                {
                // There is no new gamepad input available. Give
                // other threads a chance to do something rather than
                // eating our thread scheduler quantum.
                Thread.currentThread().yield();
                }
            }
        }

    /**
     * Implement a simple two-motor driving logic using the left and right
     * right joysticks on the indicated game pad.
     */
    void doManualDrvingControl(Gamepad pad)
        {
        // Remember that the gamepad sticks range from -1 to +1, and that the motor
        // power levels range over the same amount
        float ctlPower    =  pad.left_stick_y();
        float ctlSteering = -pad.right_stick_x();

        // We're going to assume that the deadzone processing has been taken care of for us
        // already by the underlying system (that appears to be the intent). Were that not
        // the case, then we would here process ctlPower and ctlSteering to be exactly zero
        // within the deadzone.

        // Map the power and steering to have more oomph at low values (optional)
        ctlPower = this.xformDrivingPowerLevels(ctlPower);
        ctlSteering = this.xformDrivingPowerLevels(ctlSteering);

        // Dampen power to avoid clipping so we can still effectively steer even
        // under heavy throttle.
        //
        // We want
        //      -1 <= ctlPower - ctlSteering <= 1
        //      -1 <= ctlPower + ctlSteering <= 1
        // i.e
        //      ctlSteering -1 <= ctlPower <=  ctlSteering + 1
        //     -ctlSteering -1 <= ctlPower <= -ctlSteering + 1
        ctlPower = Range.clip(ctlPower,  ctlSteering -1,  ctlSteering +1);
        ctlPower = Range.clip(ctlPower, -ctlSteering -1, -ctlSteering +1);

        // Figure out how much power to send to each motor. Be sure
        // not to ask for too much, or the motor will throw an exception.
        float powerLeft  = Range.clip(ctlPower - ctlSteering, -1f, 1f);
        float powerRight = Range.clip(ctlPower + ctlSteering, -1f, 1f);

        // Tell the motors
        this.motorLeft.setPower(powerLeft);
        this.motorRight.setPower(powerRight);
        }

    float xformDrivingPowerLevels(float level)
    // A useful thing to do in some robots is to map the power levels so that
    // low power levels have more power than they otherwise would. This sometimes
    // help give better driveability.
        {
        // We use a log function here as a simple way to transform the levels.
        // You might want to try something different: perhaps construct a
        // manually specified function using a table of values over which
        // you interpolate.
        float zeroToOne = Math.abs(level);
        float oneToTen  = zeroToOne * 9 + 1;
        return (float)(Math.log10(oneToTen) * Math.signum(level));
        }

    }

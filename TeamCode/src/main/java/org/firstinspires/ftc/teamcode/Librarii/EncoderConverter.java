package org.firstinspires.ftc.teamcode.Librarii;

import com.qualcomm.robotcore.hardware.DcMotor;

public class EncoderConverter {

    // Calculate the number of centimeters based on encoder counts
    public static double countsToCentimeters(int counts, double COUNTS_PER_REV, double WHEEL_DIAMETER_CM) {
        double revolutions = counts / COUNTS_PER_REV;
        double distanceCm = revolutions * (WHEEL_DIAMETER_CM * Math.PI);
        return distanceCm;
    }

    // Convert a DcMotor object's current encoder value to centimeters
    public static double motorToCentimeters(DcMotor motor, double COUNTS_PER_REV, double WHEEL_DIAMETER_CM) {
        int currentEncoderValue = motor.getCurrentPosition();
        return countsToCentimeters(currentEncoderValue, COUNTS_PER_REV, WHEEL_DIAMETER_CM);
    }

    // Calculate the target encoder position based on a desired distance in centimeters
    public static int calculateTargetPosition(double targetDistanceCm, double COUNTS_PER_REV, double WHEEL_DIAMETER_CM) {
        double revolutions = targetDistanceCm / (WHEEL_DIAMETER_CM * Math.PI);
        return (int) (revolutions * COUNTS_PER_REV);
    }
}

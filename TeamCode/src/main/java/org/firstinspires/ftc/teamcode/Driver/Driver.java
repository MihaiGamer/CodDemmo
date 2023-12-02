package org.firstinspires.ftc.teamcode.Driver;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Librarii.EncoderConverter;

@TeleOp
public class Driver extends OpMode {

    // motoare sasiu
    DcMotor fata_stanga, fata_dreapta, spate_stanga, spate_dreapta;

    // motoare glisiera
    DcMotor glisiera_stanga, glisiera_dreapta;

    // motor brat
    DcMotor brat;

    boolean brat_sus, brat_jos;

    // colector
    DcMotor colector;

    // motoare gheara
    Servo gheara_stanga, gheara_dreapta;

    boolean leftBumperPressed, leftTriggerPressed,rightBumberPressed;

    boolean glisieraJos = true;

    EncoderConverter encoderUtils = new EncoderConverter();

    int cm = 5/7;

    @Override
    public void init() {
        configuratie();
    }

    @Override
    public void loop() {
        sasiu(gamepad1);
        glisiera(gamepad2);
        brat(gamepad2);
        colector(gamepad1);
        gheara(gamepad2);
    }

    public void configuratie(){
        // sasiu
        fata_stanga = hardwareMap.dcMotor.get("fata_stanga");
        fata_dreapta = hardwareMap.dcMotor.get("fata_dreapta");
        spate_stanga = hardwareMap.dcMotor.get("spate_stanga");
        spate_dreapta = hardwareMap.dcMotor.get("spate_dreapta");


        // glisiera
        glisiera_stanga = hardwareMap.dcMotor.get("glisiera_stanga");
        glisiera_dreapta = hardwareMap.dcMotor.get("glisiera_dreapta");

        glisiera_dreapta.setDirection(DcMotorSimple.Direction.REVERSE);

        glisiera_stanga.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        glisiera_dreapta.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        glisiera_stanga.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        glisiera_dreapta.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // brat
        brat = hardwareMap.dcMotor.get("brat");

        brat.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        brat.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // colector
        colector = hardwareMap.dcMotor.get("colector");

        colector.setDirection(DcMotorSimple.Direction.REVERSE);

        // gheara
        gheara_stanga = hardwareMap.servo.get("gheara_stanga");
        gheara_dreapta = hardwareMap.servo.get("gheara_dreapta");
    } // configuratie

    public void sasiu(Gamepad gamepad){
        // Mecanum drive is controlled with three axes: drive (front-and-back),
        // strafe (left-and-right), and twist (rotating the whole chassis).
        double drive  = -gamepad.left_stick_y;
        double strafe = gamepad.left_stick_x;
        double twist  = gamepad.right_stick_x;

        /*
         * If we had a gyro and wanted to do field-oriented control, here
         * is where we would implement it.
         *
         * The idea is fairly simple; we have a robot-oriented Cartesian (x,y)
         * coordinate (strafe, drive), and we just rotate it by the gyro
         * reading minus the offset that we read in the init() method.
         * Some rough pseudocode demonstrating:
         *
         * if Field Oriented Control:
         *     get gyro heading
         *     subtract initial offset from heading
         *     convert heading to radians (if necessary)
         *     new strafe = strafe * cos(heading) - drive * sin(heading)
         *     new drive  = strafe * sin(heading) + drive * cos(heading)
         *
         * If you want more understanding on where these rotation formulas come
         * from, refer to
         * https://en.wikipedia.org/wiki/Rotation_(mathematics)#Two_dimensions
         */

        // You may need to multiply some of these by -1 to invert direction of
        // the motor.  This is not an issue with the calculations themselves.
        double[] speeds = {
                (drive + strafe + twist),
                (drive - strafe - twist),
                (drive - strafe + twist),
                (drive + strafe - twist)
        };

        // Because we are adding vectors and motors only take values between
        // [-1,1] we may need to normalize them.

        // Loop through all values in the speeds[] array and find the greatest
        // *magnitude*.  Not the greatest velocity.
        double max = Math.abs(speeds[0]);
        for (double speed : speeds) {
            if (max < Math.abs(speed)) max = Math.abs(speed);
        }

        // If and only if the maximum is outside of the range we want it to be,
        // normalize all the other speeds based on the given speed value.
        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
        }

        // apply the calculated values to the motors.
        fata_stanga.setPower(speeds[0]);
        fata_dreapta.setPower(speeds[1]);
        spate_stanga.setPower(speeds[2]);
        spate_dreapta.setPower(speeds[3]);
    } // sasiu

    public void glisiera(Gamepad gamepad){
        if(gamepad.y){
            glisieraJos = false;
            encoder(glisiera_stanga, -50, 1, 1440, 4.5);
            encoder(glisiera_dreapta, -50, 1, 1440, 4.5);
        }else if(gamepad.a){
            glisieraJos = true;
            encoder(glisiera_stanga, 0, 1, 1440, 4.5);
            encoder(glisiera_dreapta, 0, 1, 1440, 4.5);
        }
    } // glisiera

    public void brat(Gamepad gamepad){
        if(gamepad.dpad_up && !brat_sus){
            brat_sus = true;
            brat_jos = false;
        }else if(gamepad.dpad_down && !brat_jos){
            brat_sus = false;
            brat_jos = true;
        }else{
            brat_sus = false;
            brat_jos = false;
        }

        if(brat_sus && !glisieraJos){
            encoderBrat(brat, 260, 1);
        }else if(brat_jos){
            encoderBrat(brat, -100, 1);
        }
    } // brat

    public void colector(Gamepad gamepad){
        if(gamepad.left_bumper){
            colector.setPower(1);
        }else{
            colector.setPower(0);
        }
    } // colector

    public void gheara(Gamepad gamepad){
        // Check left bumper for claw open/close
        if (gamepad.left_bumper && !leftBumperPressed) {
            leftBumperPressed = true;
            if (!leftTriggerPressed) {
                // Toggle claw open/close
                if (gheara_stanga.getPosition() == 0.0) {
                    gheara_stanga.setPosition(0.5);
                    gheara_dreapta.setPosition(0.0);
                } else {
                    gheara_stanga.setPosition(0.0);
                    gheara_dreapta.setPosition(0.5);
                }
            } else {
                // Toggle control of individual servo with left and right bumper
                if (gheara_stanga.getPosition() == 0.0) {
                    gheara_stanga.setPosition(0.5);
                } else {
                    gheara_stanga.setPosition(0.0);
                }
            }
        } else if (!gamepad.left_bumper) {
            leftBumperPressed = false;
        }

        // Check left trigger for switching between single servo control
        if (gamepad.left_trigger > 0.5 && !leftTriggerPressed) {
            leftTriggerPressed = true;
        } else if (gamepad.left_trigger <= 0.5) {
            leftTriggerPressed = false;
        }
        if (gamepad.right_bumper && !rightBumberPressed) {
            rightBumberPressed = true;
            if (!leftTriggerPressed) {
                // Toggle claw open/close
                if (gheara_dreapta.getPosition() == 0.0) {
                    gheara_dreapta.setPosition(0.5);
                    gheara_dreapta.setPosition(0.0);
                } else {
                    gheara_dreapta.setPosition(0.0);
                    gheara_dreapta.setPosition(0.5);
                }
            } else {
                // Toggle control of individual servo with left and right bumper
                if (gheara_dreapta.getPosition() == 0.0) {
                    gheara_dreapta.setPosition(0.5);
                } else {
                    gheara_dreapta.setPosition(0.0);
                }
            }
        } else if (!gamepad.right_bumper) {
            rightBumberPressed = false;
        }

        // Check left trigger for switching between single servo control
        if (gamepad.left_trigger > 0.5 && !leftTriggerPressed) {
            leftTriggerPressed = true;
        } else if (gamepad.left_trigger <= 0.5) {
            leftTriggerPressed = false;
        }
    } // gheara

    public void encoder(DcMotor motor, int position, double power, double cpr, double diameter){
        int targetPosition = EncoderConverter.calculateTargetPosition(position, cpr, diameter);
        motor.setTargetPosition(targetPosition);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    } // encoder

    public void encoderBrat(DcMotor motor, int position, double power){
        motor.setTargetPosition(position);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    } // encoderBrat
}
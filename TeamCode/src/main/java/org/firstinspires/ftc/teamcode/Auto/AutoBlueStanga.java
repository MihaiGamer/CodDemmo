package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Librarii.EncoderConverter;

@Autonomous
public class AutoBlueStanga extends LinearOpMode {

    // motoare sasiu
    DcMotor fata_stanga, fata_dreapta, spate_stanga, spate_dreapta;

    // motoare glisiera
    DcMotor glisiera_stanga, glisiera_dreapta;

    // motoare brat
    DcMotor brat;

    // motoare gheara
    Servo gheara_stanga, gheara_dreapta;

    IdentificareCaz auto;
    double ultimaValoareX;

    EncoderConverter encoderUtils = new EncoderConverter();

    @Override
    public void runOpMode() throws InterruptedException {
        configuratie();
        auto = new IdentificareCaz();
        auto.runOpMode();
        waitForStart();

        ultimaValoareX = auto.aduValoareaFinala();
        if(ultimaValoareX < 426.666666667){
            telemetry.addLine("Stanga");
        } else if(ultimaValoareX >= 426.666666667 && ultimaValoareX <= 853.333333334){
            telemetry.addLine("Centru");
        } else{
            telemetry.addLine("Dreapta");
        }
    }

    public void configuratie(){
        // sasiu
        fata_stanga = hardwareMap.dcMotor.get("fata_stanga");
        fata_dreapta = hardwareMap.dcMotor.get("fata_dreapta");
        spate_stanga = hardwareMap.dcMotor.get("spate_stanga");
        spate_dreapta = hardwareMap.dcMotor.get("spate_dreapta");

        fata_stanga.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fata_dreapta.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spate_stanga.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spate_dreapta.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fata_stanga.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fata_dreapta.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spate_stanga.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spate_dreapta.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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

        // gheara
        gheara_stanga = hardwareMap.servo.get("gheara_stanga");
        gheara_dreapta = hardwareMap.servo.get("gheara_dreapta");

        gheara_dreapta.setDirection(Servo.Direction.REVERSE);
    } // configuratie

    public void sasiu(int distanta, double putere_fs, double putere_fd, double putere_ss, double putere_sd){
        encoderSasiu(fata_stanga, distanta, putere_fs, 383.6, 10.0);
        encoderSasiu(fata_dreapta, distanta, putere_fd, 383.6, 10.0);
        encoderSasiu(spate_stanga, distanta, putere_ss, 383.6, 10.0);
        encoderSasiu(spate_dreapta, distanta, putere_sd, 383.6, 10.0);
    } // sasiu

    public void glisiera(int sus_jos){
        if(sus_jos==0){
            encoder(glisiera_stanga, 20, 1, 1440, 4.5);
            encoder(glisiera_dreapta, 20, 1, 1440, 4.5);
        }else if(sus_jos==1){
            encoder(glisiera_stanga, 0, 1, 1440, 4.5);
            encoder(glisiera_dreapta, 0, 1, 1440, 4.5);
        }
    } // glisiera

    public void brat(int sus_jos){

        if(sus_jos==0){
            encoderBrat(brat,260,1);
        }
        else {
            encoderBrat(brat,-100,1);
        }




    }
    //brat
    public void gheara(int ambele_stanga_dreapta, int position){
        if(ambele_stanga_dreapta==0){
            gheara_stanga.setPosition(position);
            gheara_dreapta.setPosition(position);
        } else if(ambele_stanga_dreapta==1){
            gheara_stanga.setPosition(position);
        }else if(ambele_stanga_dreapta==2){
            gheara_dreapta.setPosition(position);
        }
    } // gheara

    public void encoder(DcMotor motor, int position, double power, double cpr, double diameter){
        int targetPosition = EncoderConverter.calculateTargetPosition(position, cpr, diameter);
        motor.setTargetPosition(targetPosition);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sleep();
    } // encoder

    public void encoderSasiu(DcMotor motor, int distance, double power, double cpr, double diameter){
        int targetPosition = EncoderConverter.calculateTargetPosition(distance, cpr, diameter);
        motor.setTargetPosition(targetPosition);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sleep();
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    } // encoder sasiu
    void encoderBrat(DcMotor motor, int position, double power){
        motor.setTargetPosition(position);
        motor.setPower(power);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    } // encoderBrat
    public void sleep(){
        while(opModeIsActive()&&(fata_stanga.isBusy()||fata_dreapta.isBusy()||spate_stanga.isBusy()
                ||spate_dreapta.isBusy()||glisiera_stanga.isBusy()||glisiera_dreapta.isBusy())){}
    } // sleep
}

package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;


import android.util.Size;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

import org.firstinspires.ftc.teamcode.Librarii.EncoderConverter;

@Autonomous
public class AutoBlueDreapta extends LinearOpMode {

    // motoare sasiu
    DcMotor fata_stanga, fata_dreapta, spate_stanga, spate_dreapta;

    // motoare glisiera
    DcMotor glisiera_stanga, glisiera_dreapta;

    // motoare brat
    DcMotor brat;

    // motoare gheara
    Servo gheara_stanga, gheara_dreapta;
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "CenterStage.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.
    // private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/CenterStage.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "Pixel",
    };

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    public double x;
    double valoareaFinalaX;


    EncoderConverter encoderUtils = new EncoderConverter();

    @Override
    public void runOpMode() throws InterruptedException {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                //                // choose one of the following:
                //                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                //.setIsModelTensorFlow2(true)
                //.setIsModelQuantized(true)
                //.setModelInputSize(300)
                //.setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(new Size(1280, 720));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        visionPortal.setProcessorEnabled(tfod, true);

        // Wait for the DS start button to be touched.
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                List<Recognition> currentRecognitions = tfod.getRecognitions();
                telemetry.addData("# Objects Detected", currentRecognitions.size());

                // Step through the list of recognitions and display info for each one.
                for (Recognition recognition : currentRecognitions) {
                    x = (recognition.getLeft() + recognition.getRight()) / 2 ;
                    double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

                    telemetry.addData(""," ");
                    telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
                    telemetry.addData("- Position", "%.0f / %.0f", x, y);
                    telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
                }   // end for() loop


                // Push telemetry to the Driver Station.
                telemetry.update();

                // Save CPU resources; can resume streaming when needed.
                if (gamepad1.dpad_down) {
                    visionPortal.stopStreaming();
                } else if (gamepad1.dpad_up) {
                    visionPortal.resumeStreaming();
                }
                // Share the CPU.
                sleep(20);
            }
        }

        // Save more CPU resources when camera is no longer needed.
        visionPortal.close();
        configuratie();

        waitForStart();

            if(x < 426.666666667){
                telemetry.addLine("Stanga");
                valoareaFinalaX = x;
            } else if(x >= 426.666666667 && x <= 853.333333334){
                telemetry.addLine("Centru");
                valoareaFinalaX = x;
            } else{
                telemetry.addLine("Dreapta");
                valoareaFinalaX = x;
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

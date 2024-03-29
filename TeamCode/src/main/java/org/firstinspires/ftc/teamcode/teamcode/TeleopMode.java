package org.firstinspires.ftc.teamcode.teamcode;

import android.content.Context;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name = "Teleop", group = "Teleop" )
public class TeleopMode extends LinearOpMode {

    //Declare motors and servos
   public static DcMotorEx motorLeft;
   public static DcMotorEx motorRight;
   public static DcMotor motorMiddle;
   public static DcMotorEx fourBar;
   //public static DcMotorEx fourBar2;
   public static Servo leftServo;
   public static Servo rightServo;
   public static  Servo intakeLeft;
   public static Servo intakeRight;
   public static DigitalChannel limitSwitch;
    BNO055IMU imu;
    Orientation lastAngles = new Orientation();


    //Variables
    static final double maxPosition = 0.0;
    static final double minPosition = 1.0;
    static final double IntakeUp = 0.0;
    static final double IntakeDown = 1.0;
    static final double armSpeed = 1;
    static final int ToleranceAd = 20;
    double leftPower;
    double rightPower;
    static boolean positionA;
    static boolean positionB;
    static boolean positionC;
    static boolean positionD;
    double lastAngle;

    @Override
    public void runOpMode() throws InterruptedException {
        Context myApp = hardwareMap.appContext;
        motorLeft = (DcMotorEx)hardwareMap.dcMotor.get("motorLeft");
        motorRight = (DcMotorEx)hardwareMap.dcMotor.get("motorRight");
        motorMiddle = hardwareMap.dcMotor.get("motorMiddle");
        fourBar = (DcMotorEx) hardwareMap.get(DcMotor.class, "lifter");
        //fourBar2 = (DcMotorEx) hardwareMap.get(DcMotor.class, "lifter2");
        intakeLeft = hardwareMap.get(Servo.class, "intakeLeft");
        intakeRight = hardwareMap.get(Servo.class, "intakeRight");
        rightServo = hardwareMap.get(Servo.class, "rightServo");
        limitSwitch = hardwareMap.digitalChannel.get("limitSwitch");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        imu.initialize(parameters);

        motorLeft.setDirection(DcMotor.Direction.REVERSE);
        // fourBar2.setDirection(DcMotor.Direction.REVERSE);
        intakeLeft.setDirection(Servo.Direction.REVERSE);
        motorMiddle.setDirection(DcMotor.Direction.REVERSE);

        fourBar.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // fourBar2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftServo = hardwareMap.get(Servo.class, "leftServo");

        positionA = true;
        positionB = false;
        positionC = false;
        positionD = false;

        fourBar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // fourBar2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        waitForStart();
        PIDCoefficients pidOrig = fourBar.getPIDCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        // PIDCoefficients pidOrig2 = fourBar2.getPIDCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);


        while (opModeIsActive()) {

            double drive = -gamepad2.left_stick_y;
            double turn = gamepad2.right_stick_x;
            leftPower = Range.clip(drive + turn, -1.0, 1.0);
            rightPower = Range.clip(drive - turn, -1.0, 1.0);
            motorLeft.setPower(leftPower);
            motorRight.setPower(rightPower);
            motorMiddle.setPower(gamepad2.left_stick_x);

            if(gamepad2.left_stick_x > 0.1||gamepad2.left_stick_y > 0.1|| gamepad2.right_stick_y > 0.1|| gamepad2.left_stick_x <- 0.1||gamepad2.left_stick_x <- 0.1||gamepad2.left_stick_y <- 0.1|| gamepad2.right_stick_y < - 0.1|| gamepad2.left_stick_x <- 0.1)
            {
                Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                 lastAngle = angles.firstAngle;
            }


            //platform moving servos up
            if (gamepad2.dpad_up) {
                platformMoverPosition(maxPosition);
            }
            //platform moving servos down
            else if (gamepad2.dpad_down) {
                platformMoverPosition(minPosition);
            }
            if(gamepad2.dpad_right){
                motorMiddle.setPower(1);
                Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                double angle = angles.firstAngle;
                motorLeft.setPower(.05 * lastAngle- angle);
                motorRight.setPower(- .05 * lastAngle -angle);
            }
            if(gamepad2.dpad_left){
                motorMiddle.setPower(-1);
                Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
                double angle = angles.firstAngle;
                motorLeft.setPower(.05 * - angle);
                motorRight.setPower(- .05 * angle);
            }

            //Moves intake up
            if (gamepad2.left_bumper) {
                intakeMechanism(IntakeUp);
            }
            //Moves intake down
            if (gamepad2.right_bumper) {
                intakeMechanism(IntakeDown);
            }

            //Arm Code
            if (gamepad2.a) {
                moveArm(185 + ToleranceAd, armSpeed, 20);
            }
            if (gamepad2.b) {
                moveArm(70 + ToleranceAd, armSpeed, 20);
            }
            if (gamepad2.x) {
                moveArm(125 + ToleranceAd, armSpeed, 25);
            }
            if (gamepad2.y) {
                moveArm(155 + ToleranceAd, armSpeed, 15);
            }

            if (gamepad2.right_trigger > 0.1) {
                telemetry.addData("lifter on", " ");
                telemetry.update();
                raiseFourBar(gamepad2.right_trigger / 2);
            }
            if (gamepad2.left_trigger > 0.1) {
                telemetry.addData("lifter on", " ");
                telemetry.update();
                raiseFourBar(-gamepad2.left_trigger / 4);
            }


            if (gamepad2.back) {
                useSwitch();
            }


        }
    }


    public void platformMoverPosition(double position) {
        leftServo.setPosition(position);
        rightServo.setPosition(position);
    }

    public void intakeMechanism(double position) {
        intakeLeft.setPosition(position);
        intakeRight.setPosition(position);
    }

    public void raiseFourBar(double liftSpeed) {
        fourBar.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // fourBar2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fourBar.setPower(liftSpeed);
        //  fourBar2.setPower(liftSpeed);
    }


   public void armEncoderReset() {
        fourBar.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       // fourBar2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void moveArm(int position, double power, int tolerance) {
        if (fourBar.getCurrentPosition() > 200) {
        } else {
            armEncoderReset();

            fourBar.setPower(power);
          //  fourBar2.setPower(power);

            fourBar.setTargetPositionTolerance(tolerance + ToleranceAd);
          //  fourBar2.setTargetPositionTolerance(tolerance + ToleranceAd);

            fourBar.setTargetPosition(position);
          //  fourBar2.setTargetPosition(position);

            fourBar.setMode(DcMotor.RunMode.RUN_TO_POSITION);
          //  fourBar2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            while (fourBar.isBusy()) {
                telemetry.addData("Moving to", position);
                telemetry.addData("From", fourBar.getCurrentPosition());
                telemetry.addData("Power", fourBar.getPower());
                telemetry.update();
            }
            telemetry.addData("Arm Status", "done," + position);
            telemetry.update();
            fourBar.setPower(0);
           // fourBar2.setPower(0);
        }


    }
   public void useSwitch()
    {
        fourBar.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
      //  fourBar2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (limitSwitch.getState() == true)
        {
            fourBar.setPower(-.5);
         //   fourBar2.setPower(-.5);
        }
        fourBar.setPower(0);
        //fourBar2.setPower(0);
    }
}

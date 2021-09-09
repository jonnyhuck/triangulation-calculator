package uk.co.jonnyhuck.triangulation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowMetrics;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.Math;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load the main activity
        setContentView(R.layout.activity_main);

        // set listener for calculate button
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            /**
             * Listener for Calculate Button
             * @param v
             */
            public void onClick(View v) {

                // get reference to each text box
                String aEasting = ((EditText) findViewById(R.id.a_easting)).getText().toString();
                String aNorthing = ((EditText) findViewById(R.id.a_northing)).getText().toString();
                String aDirection = ((EditText) findViewById(R.id.a_direction)).getText().toString();
                String bEasting = ((EditText) findViewById(R.id.b_easting)).getText().toString();
                String bNorthing = ((EditText) findViewById(R.id.b_northing)).getText().toString();
                String bDirection = ((EditText) findViewById(R.id.b_direction)).getText().toString();

                //loop through them all and check for empty
                String inputs[] = {aEasting, aNorthing, aDirection, bEasting, bNorthing, bDirection};
                for(String s : inputs){
                    if (s.matches("")) {
                        ((TextView) findViewById(R.id.output)).setText("Please fill in all of the boxes!");
                        return;
                    }
                }

                // calculation location of target point
                double[] targetLocation = triangulation(Double.valueOf(aEasting), Double.valueOf(aNorthing), Double.valueOf(aDirection),
                        Double.valueOf(bEasting), Double.valueOf(bNorthing), Double.valueOf(bDirection));

                // update output text
                ((TextView) findViewById(R.id.output)).setText(
                        String.format(Locale.ENGLISH, "%.2f", targetLocation[0]) + ", " +
                        String.format(Locale.ENGLISH, "%.2f", targetLocation[1])
                );
            }
        });

        findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {

            /**
             * Listener for Clear Button
             * Empty all text boxes and reset output text
             * @param v
             */
            public void onClick(View v) {
                ((EditText) findViewById(R.id.a_easting)).setText("");
                ((EditText) findViewById(R.id.a_northing)).setText("");
                ((EditText) findViewById(R.id.a_direction)).setText("");
                ((EditText) findViewById(R.id.b_easting)).setText("");
                ((EditText) findViewById(R.id.b_northing)).setText("");
                ((EditText) findViewById(R.id.b_direction)).setText("");
                ((TextView) findViewById(R.id.output)).setText(R.string.output);
            }
        });
    }

    /**
     * Calculate a coordinate pair based on the direction from two known locations
     * @param aEasting
     * @param aNorthing
     * @param aDirection
     * @param bEasting
     * @param bNorthing
     * @param bDirection
     * @return
     */
    private double[] triangulation(double aEasting, double aNorthing, double aDirection, double bEasting, double bNorthing, double bDirection){

        // distance between the two known locations
        double c = getDistance(aEasting, aNorthing, bEasting, bNorthing);

        // directions between the two locations
        double AB = getDirection(bEasting, bNorthing, aEasting, aNorthing);
        double BA = getDirection(aEasting, aNorthing, bEasting, bNorthing);

        // angles at each corner of the triangle
        double A = getAngle(aDirection, AB);
        double B = getAngle(bDirection, BA);
        double C = 180 - (A + B);

        // length of the other two sides of the triangle
//        double a = (c * Math.sin(Math.toRadians(A))) / Math.sin(Math.toRadians(C));
        double b = (c * Math.sin(Math.toRadians(B))) / Math.sin(Math.toRadians(C));

        // return the coordinates of the third corner of the triangle
        return new double[]{
            aEasting + Math.sin(Math.toRadians(aDirection)) * b,
            aNorthing + Math.cos(Math.toRadians(aDirection)) * b
        };
    }

    /**
     * Calculate the distance between two locations
     * @param aEasting
     * @param aNorthing
     * @param bEasting
     * @param bNorthing
     * @return
     */
    private double getDistance(double aEasting, double aNorthing, double bEasting, double bNorthing) {
        return Math.hypot(aEasting - bEasting, aNorthing - bNorthing);
    }

    /**
     * Calculate the direction between two known locations
     * @param aEasting
     * @param aNorthing
     * @param bEasting
     * @param bNorthing
     * @return
     */
    private double getDirection(double aEasting, double aNorthing, double bEasting, double bNorthing){
        return (90 - Math.toDegrees(Math.atan2(aNorthing - bNorthing, aEasting - bEasting)) + 360) % 360;
    }

    /**
     * Calculate the angle between two directions
     * @param aDirection
     * @param bDirection
     * @return
     */
    private double getAngle(double aDirection, double bDirection) {
        double angle = Math.abs(aDirection - bDirection);
        return (angle > 180) ? 360 - angle : angle;
    }
}
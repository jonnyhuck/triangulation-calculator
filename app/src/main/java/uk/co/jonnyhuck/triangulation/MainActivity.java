package uk.co.jonnyhuck.triangulation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set listener for calculate button
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Get reference to each text box
                String aEasting = ((EditText) findViewById(R.id.a_easting)).getText().toString();
                String aNorthing = ((EditText) findViewById(R.id.a_northing)).getText().toString();
                String aDirection = ((EditText) findViewById(R.id.a_direction)).getText().toString();
                String bEasting = ((EditText) findViewById(R.id.b_easting)).getText().toString();
                String bNorthing = ((EditText) findViewById(R.id.b_northing)).getText().toString();
                String bDirection = ((EditText) findViewById(R.id.b_direction)).getText().toString();

                // Check for empty fields
                String[] inputs = {aEasting, aNorthing, aDirection, bEasting, bNorthing, bDirection};
                for (String s : inputs) {
                    if (TextUtils.isEmpty(s)) {
                        ((TextView) findViewById(R.id.output)).setText("Please fill in all of the boxes!");
                        return;
                    }
                }

                // Perform triangulation calculation
                double[] targetLocation = triangulation(
                        Double.parseDouble(aEasting),
                        Double.parseDouble(aNorthing),
                        Double.parseDouble(aDirection),
                        Double.parseDouble(bEasting),
                        Double.parseDouble(bNorthing),
                        Double.parseDouble(bDirection)
                );

                // Update output text with the calculated coordinates
                ((TextView) findViewById(R.id.output)).setText(
                        String.format(Locale.ENGLISH, "%.2f, %.2f", targetLocation[0], targetLocation[1])
                );
            }
        });

        // Set listener for clear button
        findViewById(R.id.clearButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear all text fields and reset output
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
     * Triangulation calculation: calculates the target coordinates
     */
    private double[] triangulation(double aEasting, double aNorthing, double aDirection, double bEasting, double bNorthing, double bDirection) {
        // Calculate distance between the two points
        double c = getDistance(aEasting, aNorthing, bEasting, bNorthing);

        // Directions between the points
        double AB = getDirection(bEasting, bNorthing, aEasting, aNorthing);
        double BA = getDirection(aEasting, aNorthing, bEasting, bNorthing);

        // Angles at the triangle vertices
        double A = getAngle(aDirection, AB);
        double B = getAngle(bDirection, BA);
        double C = 180 - (A + B);

        // Calculate the length of the sides and triangulate the third point
        double b = (c * Math.sin(Math.toRadians(B))) / Math.sin(Math.toRadians(C));

        return new double[]{
                aEasting + Math.sin(Math.toRadians(aDirection)) * b,
                aNorthing + Math.cos(Math.toRadians(aDirection)) * b
        };
    }

    /**
     * Calculates the distance between two points
     */
    private double getDistance(double aEasting, double aNorthing, double bEasting, double bNorthing) {
        return Math.hypot(aEasting - bEasting, aNorthing - bNorthing);
    }

    /**
     * Calculates the direction between two points
     */
    private double getDirection(double aEasting, double aNorthing, double bEasting, double bNorthing) {
        return (90 - Math.toDegrees(Math.atan2(aNorthing - bNorthing, aEasting - bEasting)) + 360) % 360;
    }

    /**
     * Calculates the angle between two directions
     */
    private double getAngle(double aDirection, double bDirection) {
        double angle = Math.abs(aDirection - bDirection);
        return (angle > 180) ? 360 - angle : angle;
    }
}

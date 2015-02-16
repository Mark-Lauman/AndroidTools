package ca.marklauman.tools;

@SuppressWarnings("ALL")
public abstract class Utils {

    /** Round a number to a given number of places
     *  @param number The number to round
     *  @param places The number of decimal places to keep
     *  @return The rounded number     */
    public static float round(float number, int places) {
        return (float)round((double)number, places);
    }

    /** Round a number to a given number of places
     *  @param number The number to round
     *  @param places The number of decimal places to keep
     *  @return The rounded number     */
    public static double round(double number, int places) {
        double trans = Math.pow(10, places);
        return Math.round(trans * number) / trans;
    }
}

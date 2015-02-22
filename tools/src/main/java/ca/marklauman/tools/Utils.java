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

    /** Join all the vals together into one string.
     *  @param seperator The string used to seprate the individual values.
     *  @param vals The values to place into the string. Values will be
     *              converted by their class' toString() method.
     *  @return A String made of all the values joined together, with each
     *          item seperated from its neighbours by the seperator.
     *          The seperator will not appear at the beginning or end
     *          of the result.                                     */
    public static <T> String join(String seperator, T... vals) {
        // input sanitization
        if(vals == null) return "" + null;
        if(seperator == null) seperator = "" + null;

        // join the strings
        String res = "";
        for(T val : vals)
            res += seperator + val;

        // remove the excess seperator at the start and return
        if(res.length() < seperator.length()) return "";
        return res.substring(seperator.length());
    }
}
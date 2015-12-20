/* Copyright (c) 2015 Mark Christopher Lauman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.                                        */
package ca.marklauman.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.TypedValue;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

@SuppressWarnings({"SameParameterValue", "WeakerAccess"})
public abstract class Utils {

    /** Retrieve an array of resource ids from the xml of the provided {@link Context}.
     *  These resources are placed in an application's xml files as an array containing
     *  individual resource ids (not an integer-array or a string-array, just an array).
     *  @param c The {@code Context} to search for the array.
     *  @param resourceId The resource id of an {@code <array>} containing a list of drawables.
     *  @return The resource ids of all the drawables in the array, in the order in which
     *  they appear in the xml. Returns null if the array does not exist. */
    public static int[] getResourceArray(Context c, int resourceId) {
        TypedArray ta = c.getResources()
                         .obtainTypedArray(resourceId);
        if(ta == null) return null;

        int[] res = new int[ta.length()];
        for(int i=0; i<ta.length(); i++)
            res[i] = ta.getResourceId(i, -1);

        ta.recycle();
        return res;
    }

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

    /** Join all the values together into one string.
     *  @param separator The string used to separate the individual values.
     *  @param values The values to place into the string. Values will be
     *                converted by their class' toString() method.
     *  @return A String made of all the values joined together, with each
     *          item separated from its neighbours by the separator.
     *          The separator will not appear at the beginning or end
     *          of the result.                                     */
    public static <T> String join(String separator, T[] values) {
        // sanitize inputs
        if(values == null) return "" + null;
        if(separator == null) separator = "" + null;

        // join the strings
        String res = "";
        for(T val : values)
            res += separator + val;

        // remove the excess separator at the start and return
        if(res.length() < separator.length()) return "";
        return res.substring(separator.length());
    }

    /** Join all the values together into one string.
     *  @param separator The string used to separate the individual values.
     *  @param values The values to place into the string. Values will be
     *                converted by their class' toString() method.
     *  @return A String made of all the values joined together, with each
     *          item separated from its neighbours by the separator.
     *          The separator will not appear at the beginning or end
     *          of the result.                                     */
    public static String join(String separator, long[] values) {
        // sanitize inputs
        if(values == null) return "" + null;
        if(separator == null) separator = "" + null;

        // join the strings
        String res = "";
        for(long val : values)
            res += separator + val;

        // remove the excess separator at the start and return
        if(res.length() < separator.length()) return "";
        return res.substring(separator.length());
    }

    /** Join all the values together into one string.
     *  @param separator The string used to separate the individual values.
     *  @param values The values to place into the string. Values will be
     *                converted by their class' toString() method.
     *  @return A String made of all the values joined together, with each
     *          item separated from its neighbours by the separator.
     *          The separator will not appear at the beginning or end
     *          of the result.                                     */
    public static <T> String join(String separator, Collection<T> values) {
        // sanitize inputs
        if(values == null) return "" + null;
        if(separator == null) separator = "" + null;

        // join the strings
        String res = "";
        for(T val : values)
            res += separator + val;

        // remove the excess separator at the start and return
        if(res.length() < separator.length()) return "";
        return res.substring(separator.length());
    }

    /** Count the occurrences of the character in the sequence.
     *  @param seq The sequence of characters to loop over.
     *  @param c The character to find.
     *  @return The total number of times c appears in seq. */
    public static int countChar(CharSequence seq, char c) {
        if(seq == null || seq.length() < 1) return 0;
        int count = 0;
        for(int i=0; i<seq.length(); i++) {
            if(c == seq.charAt(i)) count++;
        }
        return count;
    }


    /** Provide with a dp measurement, to get a pixel measurement.
     *  @param c The activity context.
     *  @param dp The measure in dp
     *  @return The measure in px. */
    public static int dp(Context c, int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                                                c.getResources().getDisplayMetrics()) + 0.5f);
    }


    /** Copy one file into another file.
     *  @param in An input stream positioned at the start of the source file.
     *  @param outs An output stream positioned at the start of the destination file.
     *  @throws IOException if any errors occur while copying. */
    public static void copyFile(InputStream in, OutputStream outs) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while(0 < (length = in.read(buffer)))
            outs.write(buffer, 0, length);
        outs.flush();
        outs.close();
        in.close();
    }


    /** Set the drawables on a TextView using valid support methods.
     * @param view The TextView that the drawables should be applied to.
     * @param start Resource identifier of the start Drawable.
     * @param top Resource identifier of the top Drawable.
     * @param end Resource identifier of the end Drawable.
     * @param bottom Resource identifier of the bottom Drawable. */
     public static void setDrawables(TextView view, int start, int top, int end, int bottom) {
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
             view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
         } else view.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
     }
}
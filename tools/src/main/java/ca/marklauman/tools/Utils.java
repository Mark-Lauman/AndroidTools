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

import java.util.Collection;

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
}
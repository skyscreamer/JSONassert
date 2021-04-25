package org.skyscreamer.jsonassert;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class SpecialCompareChange
 * Use to handle the out of bound number comparation
 * @author Maijie Xie
 * @version 1.0
 * Created on 23/4/2021
 */



public class SpecialCompareChange {
    public SpecialCompareChange() {
    }
    /**
     * Find the JsonObject's number and replace the number which is out of bound to the string.
     * @param expectedStr Expected JSON string
     * @return result of the replacement
     */
    public String change(String expectedStr) {
        String regEx = ":\\s*([1-9]\\d*.\\d*|0\\.?\\d*[1-9]\\d*)\\s*}";
        Pattern p = Pattern.compile(regEx);
        Matcher exp = p.matcher(expectedStr);
        ArrayList<String> exp_num = new ArrayList<String>();
        while (exp.find()) {
            exp_num.add(exp.group(0).replace(":", "").replaceAll(" ", "").replace("}", ""));
        }
        ArrayList<String> exp_newNum = pretreatBigNumber((ArrayList<String>) exp_num.clone());
        return replaceNum(expectedStr, exp_num, exp_newNum);
    }
    /**
     * Check the number in the JsonObject wither out of bound.
     * @param num numbers find in the sonObject
     * @return result of the num wither out of bound
     */
    static ArrayList<String> pretreatBigNumber(ArrayList<String> num) {
        ArrayList<String> pretreat = new ArrayList<String>();
        for (String i : num) {
            i = removeEndZero(i);
            if (!(String.valueOf(Double.parseDouble(i)).equals(i))) {
                i = "a" + i;
                pretreat.add(i);
            } else {
                pretreat.add(i);
            }
        }
        return pretreat;
    }
    /**
     * Remove the zero at the end of the decimal point.
     * @param num number find in the JsonObject
     * @return result of the number after remove the zero at the end of the decimal point.
     */
    static String removeEndZero(String num) {
        if (!isInteger(num)) {
            StringBuffer newNum = new StringBuffer(num);
            for (int i = num.length() - 1; i >= 0; i--) {
                if (num.charAt(i) != '0') {
                    if (i < num.length() - 1) {
                        if (num.charAt(i) != '.') {
                            newNum.replace(i + 1, num.length(), "");
                        }else{
                            newNum.replace(i, num.length(), "");
                        }
                        break;
                    } else {
                        break;
                    }
                }
            }
            return newNum.toString();
        } else {
            return num;
        }
    }
    /**
     * replace number which is out of bound in the JasonObject as a string formate.
     * @param str string of the JasonObject.
     * @param num number need to replace as string.
     * @param newNum strings the number need to replace to.
     * @return result of the string after replace.
     */
    static String replaceNum(String str, ArrayList<String> num, ArrayList<String> newNum) {
        for (int i = 0; i < num.size(); i++) {
            str = str.replace(num.get(i), newNum.get(i));
        }
        return str;
    }
    /**
     * Judge whether the string is a number
     * @param str The string in the JasonObject.
     * @return result of whether the string is anumber
     */
    public static boolean isInteger(String str) {

        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;

    }
}

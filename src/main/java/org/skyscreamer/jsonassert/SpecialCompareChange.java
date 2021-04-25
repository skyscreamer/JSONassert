package org.skyscreamer.jsonassert;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecialCompareChange {
    public SpecialCompareChange() {
    }

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

    static String replaceNum(String str, ArrayList<String> num, ArrayList<String> newNum) {
        for (int i = 0; i < num.size(); i++) {
            str = str.replaceAll(num.get(i), newNum.get(i));
        }
        return str;
    }

    public static boolean isInteger(String str) {

        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;

    }
}

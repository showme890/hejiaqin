package com.customer.framework.utils;

import android.content.Context;

import com.customer.framework.component.log.Logger;
import com.customer.framework.utils.string.HanziToPinyin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***/
public class StringUtil {
    /**
     * 整型转换错误码
     */
    public static final int ERROR_CODE = -1;

    /**
     * 行分隔符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 打印标示
     */
    private static final String TAG = "StringUtil";

    /**
     * SQL注入关键字序列
     */
    private static final String[] SQL_INJECTION_STRS = { "'", "and", "exec", "insert", "select",
                                                         "delete", "update", "count", "*", "%", "chr", "mid", "master", "truncate", "char",
                                                         "declare", ";", "or", "-", "+", "," };

    /**
     * 判断是否为null或空值
     *
     * @param str 字符串
     * @return 空为true，非空为false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相等为true，不相等为false
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        }
        return str1 != null && str1.equals(str2);
    }

    /**
     * 判断两个字符串是否相同(不区分大小写)
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 相等为true，不相等为false
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 将String按split拆分，并组装成List。默认以','拆分。<BR>
     *
     * @param srcString 源字符串
     * @param split     分隔符（正则表达式）
     * @return 返回list
     */
    public static List<String> splitStringToList(String srcString, String split) {
        List<String> list = null;
        if (!StringUtil.isNullOrEmpty(srcString)) {
            if (split == null) {
                split = ",";
            }
            String[] strArr = srcString.split(split);
            if (strArr != null && strArr.length > 0) {
                list = new ArrayList<String>(strArr.length);
                for (String str : strArr) {
                    list.add(str);
                }
            }
        }
        return list;
    }

    /**
     * 去掉url中多余的斜杠
     *
     * @param url 字符串
     * @return 去掉多余斜杠的字符串
     */
    public static String fixUrlSprit(String url) {
        if (null == url) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer(url);
        for (int i = stringBuffer.indexOf("//", stringBuffer.indexOf("//") + 2); i != -1; i = stringBuffer
                .indexOf("//", i + 1)) {
            stringBuffer.deleteCharAt(i);
        }
        return stringBuffer.toString();
    }

    /**
     * 检测密码复杂强度
     *
     * @param password 密码
     * @return 密码强度（1：低 2：中 3：高）
     */
    public static int checkPwdStrong(String password) {
        boolean num = false;
        boolean lowerCase = false;
        boolean upperCase = false;
        boolean other = false;
        int threeMode = 0;
        int fourMode = 0;
        for (int i = 0; i < password.length(); i++) {
            // 单个字符是数字
            if (password.codePointAt(i) >= 48 && password.codePointAt(i) <= 57) {
                num = true;
            } else if (password.codePointAt(i) >= 97 && password.codePointAt(i) <= 122) {// 单个字符是小写字母
                lowerCase = true;
            } else if (password.codePointAt(i) >= 65 && password.codePointAt(i) <= 90) {// 单个字符是大写字母
                upperCase = true;
            } else {// 特殊字符
                other = true;
            }
        }
        if (num) {
            threeMode++;
            fourMode++;
        }
        if (lowerCase) {
            threeMode++;
            fourMode++;
        }
        if (upperCase) {
            threeMode++;
            fourMode++;
        }
        if (other) {
            fourMode = fourMode + 1;
        }
        // 数字、大写字母、小写字母只有一个，密码强度低（只有一种特殊字符也是密码强度低）
        if (threeMode == 1 && !other || fourMode == 1) {
            return 1;
        } else if (fourMode == 2) {// 四种格式有其中两个，密码强度中
            return 2;
        } else if (fourMode >= 3) {// 四种格式有三个或者四个，密码强度高
            return 3;
        } else {// 正常情况下不会出现该判断
            return 3;
        }
    }

    /**
     * 将字符串数组转化为字符串，默认以","分隔
     * <pre>
     * StringUtil.arrayToString(null, ",")               = ""
     * StringUtil.arrayToString([], ",")                 = ""
     * StringUtil.arrayToString(["hello", "world"], ",") = "hello,world"
     * StringUtil.arrayToString(["12", "34", "56"], "+") = "12+34+56"
     * StringUtil.arrayToString(["12", "34", "56"], null)= "12,34,56"
     * </pre>
     *
     * @param values 字符串数组
     * @param split  分隔符，默认为","
     * @return 由字符串数组转化后的字符串
     */
    public static String arrayToString(String[] values, String split) {
        StringBuffer buffer = new StringBuffer();
        if (values != null) {
            if (isNullOrEmpty(split)) {
                split = ",";
            }
            int len = values.length;
            for (int i = 0; i < len; i++) {
                buffer.append(values[i]);
                if (i != len - 1) {
                    buffer.append(split);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 将字符串list转化为字符串，默认以","分隔<BR>
     *
     * @param strList 字符串list
     * @param split   分隔符，默认为","
     * @return 组装后的字符串对象
     */
    public static String listToString(Collection<String> strList, String split) {
        String[] values = null;
        if (strList != null) {
            values = new String[strList.size()];
            strList.toArray(values);
        }
        return arrayToString(values, split);
    }

    private static int getMode(int threeMode, boolean other, int fourMode) {
        // 数字、大写字母、小写字母只有一个且无特殊字符，密码强度低
        if (threeMode == 1 && !other) {
            return 1;
        } else if (fourMode == 2) {// 四种格式有其中两个，密码强度中
            return 2;
        } else if (fourMode >= 3) {// 四种格式有三个或者四个，密码强度高
            return 3;
        }
        // 正常情况下不会出现该判断
        return 0;
    }

    /**
     * 判断字符串str1是否包含指定字符串str2
     *
     * @param str1 源字符串
     * @param str2 指定字符串
     * @return true源字符串包含指定字符串，false源字符串不包含指定字符串
     */
    public static boolean contains(String str1, String str2) {
        return str1 != null && str1.contains(str2);
    }

    /**
     * 判断一个字符串是否可能是SQL注入<BR>
     * 原 maybeSqlInjection
     * @param str 被检测的字符串
     * @return true:可能是SQL注入 false:安全
     *
     */
    public static boolean checkSQLInjection(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }

        for (int i = 0; i < SQL_INJECTION_STRS.length; i++) {
            if (str.indexOf(SQL_INJECTION_STRS[i]) >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 把字符串转换成整型
     *
     * @param string      需要判断的字符串
     * @param ignoreSpace 是否忽略空格
     * @return int 如果转换失败会返回StringUtil.ERROR_CODE（-1）错误码
     */
    public static int parseInt(String string, boolean ignoreSpace) {
        int value = ERROR_CODE;
        if (string != null && isNumeric(string, ignoreSpace)) {
            value = Integer.parseInt(string);
        }
        return value;
    }

    /**
     * 判断是否是数字的字符串
     *
     * @param string      需要判断的字符串
     * @param ignoreSpace 是否忽略空格
     * @return boolean true是数字串，false不是数字串
     */
    public static boolean isNumeric(String string, boolean ignoreSpace) {
        if (ignoreSpace) {
            string = string.replaceAll(" ", "");
        }

        Pattern pattern = Pattern.compile("^-?\\d+$");
        Matcher isNum = pattern.matcher(string);
        return isNum.matches();
    }

    /**
     * 处理url请求参数里的特殊符号
     *
     * @param param url字符串
     * @return 处理后的字符串
     */
    public static String parseUrlParam(String param) {
        return param.replaceAll("%", "%25").replaceAll("\\+", "%2B").replaceAll("\\?", "%3F")
                .replaceAll(" ", "%20").replaceAll("#", "%23").replaceAll("/", "%2F")
                .replaceAll("&", "%26").replaceAll("=", "%3D");
    }

    /**
     * 基于Base64编码后的字符串构建文件名
     * 由于Base64生成的字符串中可能包含 +、/等文件名不允许包含的字符，因此需要进行转义<BR>
     * 转义规则：'+'转为'_'，'/'转为'-'<BR>
     *
     * @param base64Str Base64编码后的字符串
     * @return 合法的文件名
     */
    public static String getFileNameFromBase64Str(String base64Str) {
        if (isNullOrEmpty(base64Str)) {
            return null;
        }
        return base64Str.replace('+', '_').replace('/', '-');
    }

    /***/
    public static String bytes2Hex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (input == null || input.length <= 0) {
            return null;
        }
        for (int i = 0; i < input.length; i++) {
            int v = input[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /***/
    public static byte[] hex2Bytes(String input) {
        if (input == null || "".equals(input)) {
            return null;
        }
        input = input.toUpperCase();
        int length = input.length() / 2;
        char[] hexChars = input.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer(
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    public static boolean isMobileNO(String paramString) {
        return Pattern.compile("^((13[0-9])|(17[0-9])|(14[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$")
                .matcher(paramString).matches();
    }

    public static boolean isVerifyCode(String paramString) {
        return Pattern.compile("\\d{4}").matcher(paramString).matches();
    }

    public static boolean isPassword(String paramString) {

        return Pattern
                .compile(
                        "^(?=.*?[^\\\\s])[!\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~A-Za-z0-9]{6,32}$")
                .matcher(paramString).matches();
    }

    /**
     * 汉字转为拼音
     */
    public static String hanzi2Pinyin(Context context, String input) {
        if (null == input || null == context) {
            return input;
        }

        char[] chars = input.toCharArray();
        StringBuffer fullPinyinLetters = new StringBuffer();
        for (int i = 0, len = chars.length; i < len; i++) {
            String curChar = String.valueOf(chars[i]);
            // 获取字符对应的拼音
            String pinyin = HanziToPinyin.getInstance(context).hanziToPinyin(curChar);
            if (pinyin.length() > 0) {
                fullPinyinLetters.append(pinyin);
            }
        }
        return fullPinyinLetters.toString().toUpperCase();
    }

    /**
     * 汉字转为拼音
     */
    public static String[] hanzi2PinyinWithHead(Context context, String input) {
        String[] result = new String[2];
        if (null == input || null == context) {
            return result;
        }

        char[] chars = input.toCharArray();
        StringBuffer fullPinyinLetters = new StringBuffer();
        StringBuffer headCharLetters = new StringBuffer();
        for (int i = 0, len = chars.length; i < len; i++) {
            String curChar = String.valueOf(chars[i]);
            // 获取字符对应的拼音
            String pinyin = HanziToPinyin.getInstance(context).hanziToPinyin(curChar);
            if (pinyin.length() > 0) {
                fullPinyinLetters.append(pinyin);
                headCharLetters.append(pinyin.charAt(0));
            }
        }
        result[0] = fullPinyinLetters.toString().toUpperCase();
        result[1] = headCharLetters.toString().toUpperCase();
        return result;
    }

    public static int getIntValue(String input) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            Logger.w(TAG, "invalid int: " + input);
            return ERROR_CODE;
        }
    }
}

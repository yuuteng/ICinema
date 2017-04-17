package yuut.icinema.support.Util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.List;

/**
 * Created by yuut on 2017/4/13.
 */

public class StringUtil {
    //设置部分文字的格式
    public static SpannableString getSpannableString(String str, int color) {
        SpannableString span = new SpannableString(str);
//        setSpan(Object what, int start, int end, int flags)方法需要用户输入四个参数，
// what表示设置的格式是什么，可以是前景色、背景色也可以是可点击的文本等等，
// start表示需要设置格式的子字符串的起始下标，同理end表示终了下标，
// flags属性就有意思了，共有四种属性：
//        Spanned.SPAN_INCLUSIVE_EXCLUSIVE 从起始下标到终了下标，包括起始下标
//        Spanned.SPAN_INCLUSIVE_INCLUSIVE 从起始下标到终了下标，同时包括起始下标和终了下标
//        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE 从起始下标到终了下标，但都不包括起始下标和终了下标
//        Spanned.SPAN_EXCLUSIVE_INCLUSIVE 从起始下标到终了下标，包括终了下标
        span.setSpan(new ForegroundColorSpan(//设置文字的前景色为color
                color), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    public static SpannableString getSpannableString1(String str, Object... whats) {
        SpannableString span = new SpannableString(str);
        for (Object what:whats) {
            span.setSpan(what, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }
    /**
     * 将List中存储的String,连接成一条String
     * */
    public static String getListString(List<String> list, char s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            str.append(i == 0 ? "" : s).append(list.get(i));
        }
        return str.toString();
    }
}

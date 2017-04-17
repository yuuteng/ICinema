package yuut.icinema.support.Util;

import java.util.List;

import yuut.icinema.bean.CelebrityEntity;

/**
 * Created by yuut on 2017/4/13.
 * 将 人物List,连接成一行String
 */

public class CelebrityUtil {
    public static String list2String(List<CelebrityEntity> entities, char s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            sb.append(i == 0 ? "" : s).append(entities.get(i).getName());
        }
        return sb.toString();
    }
}

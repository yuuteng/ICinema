package yuut.icinema.bean;

/**
 * Created by yuut on 2017/4/13.
 */

public class CelebrityEntity {
    private ImagesEntity avatars;//头像
    private String alt;//条目页url
    private String id;//条目id
    private String name;//名字

    public ImagesEntity getAvatars() {
        return avatars;
    }

    public void setAvatars(ImagesEntity avatars) {
        this.avatars = avatars;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

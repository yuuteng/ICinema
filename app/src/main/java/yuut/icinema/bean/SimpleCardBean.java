package yuut.icinema.bean;

/**
 * Created by yuut on 2017/4/20.
 */
//详情界面,底部推荐电影,横向
public class SimpleCardBean {
    private String id;
    private String name;
    private String image;
    private Boolean isFilm;

    public SimpleCardBean(String id, String name, String image, Boolean isFilm) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isFilm = isFilm;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getFilm() {
        return isFilm;
    }

    public void setFilm(Boolean film) {
        isFilm = film;
    }

    public Boolean getIsFilm() {
        return isFilm;
    }
}

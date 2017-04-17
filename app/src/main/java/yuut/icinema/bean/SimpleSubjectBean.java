package yuut.icinema.bean;

import java.util.List;

/**
 * Created by yuut on 2017/4/13.
 */
//电影概述
public class SimpleSubjectBean {
    private RatingEntity rating;
    private int collect_count;//收藏数量
    private String title;//标题
    private String original_title;//原标题
    private String subtype;//条目分类, movie或者tv
    private String year;//年
    private ImagesEntity images;//海报,大中小
    private String alt;//条目页URL
    private String id;//条目id
    private List<String> genres;//种类最多3
    private List<CelebrityEntity> casts;//演员
    private List<CelebrityEntity> directors;//导演

    public RatingEntity getRating() {
        return rating;
    }

    public void setRating(RatingEntity rating) {
        this.rating = rating;
    }

    public int getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(int collect_count) {
        this.collect_count = collect_count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public ImagesEntity getImages() {
        return images;
    }

    public void setImages(ImagesEntity images) {
        this.images = images;
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

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<CelebrityEntity> getCasts() {
        return casts;
    }

    public void setCasts(List<CelebrityEntity> casts) {
        this.casts = casts;
    }

    public List<CelebrityEntity> getDirectors() {
        return directors;
    }

    public void setDirectors(List<CelebrityEntity> directors) {
        this.directors = directors;
    }
}

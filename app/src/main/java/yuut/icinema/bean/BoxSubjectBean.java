package yuut.icinema.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yuut on 2017/4/13.
 */

public class BoxSubjectBean {
    private int box;
//    new: true  修改Json :前面的名字为new
    @SerializedName("new")
    private boolean newX;
    private int rank;
    private SimpleSubjectBean subject;

    public int getBox() {
        return box;
    }

    public void setBox(int box) {
        this.box = box;
    }

    public boolean isNewX() {
        return newX;
    }

    public void setNewX(boolean newX) {
        this.newX = newX;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public SimpleSubjectBean getSubject() {
        return subject;
    }

    public void setSubject(SimpleSubjectBean subject) {
        this.subject = subject;
    }

    public boolean getNewX() {
        return newX;
    }
}

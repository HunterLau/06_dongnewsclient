package nouelleclient.ithema.com.a06_dongnewsclient.domain;

/**
 * Created by alice on 2016/11/25.
 */

public class News {
    private String title;
    private String detail;
    private String comment;
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", comment='" + comment + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}

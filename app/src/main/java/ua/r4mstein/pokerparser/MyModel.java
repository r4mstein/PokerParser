package ua.r4mstein.pokerparser;

import com.google.gson.Gson;

public class MyModel {

    private String linkTitle;
    private String link;
    private String user;

////    public MyModel(String linkTitle, String link, String user) {
////        this.linkTitle = linkTitle;
////        this.link = link;
////        this.user = user;
//    }

    public String serializeMyModel() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static MyModel createMyModel(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, MyModel.class);
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyModel model = (MyModel) o;

        return linkTitle != null ? linkTitle.equals(model.linkTitle) : model.linkTitle == null;

    }

    @Override
    public int hashCode() {
        return linkTitle != null ? linkTitle.hashCode() : 0;
    }
}

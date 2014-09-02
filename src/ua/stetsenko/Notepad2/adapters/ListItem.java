package ua.stetsenko.Notepad2.adapters;

public class ListItem {

    private int id;
    private int imageId;
    private String description;
    private String dateTime;


    public ListItem(int imageId, String description) {
        this.imageId = imageId;
        this.description = description;
    }

    public ListItem(int id, int imageId, String description, String dateTime) {
        this.id = id;
        this.imageId = imageId;
        this.description = description;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public int getImageId() {
        return imageId;
    }

    public String getDescription() {
        return description;
    }


    public String getDateTime() {
        return dateTime;
    }
}

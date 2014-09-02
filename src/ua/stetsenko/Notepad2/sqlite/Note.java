package ua.stetsenko.Notepad2.sqlite;

public class Note {
    private int _id;
    private int type;
    private String content;
    private String dateTime;
    private String uriResource;

    public Note(int type, String content, String dateTime) {
        this.type = type;
        this.content = content;
        this.dateTime = dateTime;
    }

    public Note(int _id, int type, String content, String dateTime) {
        this._id = _id;
        this.type = type;
        this.content = content;
        this.dateTime = dateTime;
    }

    public Note( int type, String content, String dateTime, String uriResource) {
        this.type = type;
        this.content = content;
        this.dateTime = dateTime;
        this.uriResource = uriResource;
    }

    public Note(int _id, int type, String content, String dateTime, String uriResource) {
        this._id = _id;
        this.type = type;
        this.content = content;
        this.dateTime = dateTime;
        this.uriResource = uriResource;
    }

    public Note() {
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getUriResource() {
        return uriResource;
    }

    public void setUriResource(String uriResource) {
        this.uriResource = uriResource;
    }

    @Override
    public String toString() {
        return "Note{" +
                "_id=" + _id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", uriResource='" + uriResource + '\'' +
                '}';
    }
}

package ch.hearc.moodymusic.model;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class Song {
    private long id;
    private String path;
    private String artist;
    private String title;
    private String album;
    private long moodId;
    private long userMoodId;

    public Song(long id, String path, String artist, String title, String album, long moodId, long userMoodId) {
        this.id = id;
        this.path = path;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.moodId = moodId;
        this.userMoodId = userMoodId;
    }

    //Getter
    public long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public long getMoodId() {
        return moodId;
    }

    public long getUserMoodId() {
        return userMoodId;
    }

    //Setter
    public void setId(long id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setMoodId(long moodId) {
        this.moodId = moodId;
    }

    public void setUserMoodId(long userMoodId) {
        this.userMoodId = userMoodId;
    }
}

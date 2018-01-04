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
    private long moodPlaylistId;
    private long userMoodPlaylistId;

    public Song(long id, String path, String artist, String title, String album, long moodPlaylistId, long userMoodPlaylistId) {
        this.id = id;
        this.path = path;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.moodPlaylistId = moodPlaylistId;
        this.userMoodPlaylistId = userMoodPlaylistId;
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

    public long getMoodPlaylistId() {
        return moodPlaylistId;
    }

    public long getUserMoodPlaylistId() {
        return userMoodPlaylistId;
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

    public void setMoodPlaylistId(long moodPlaylistId) {
        this.moodPlaylistId = moodPlaylistId;
    }

    public void setUserMoodPlaylistId(long userMoodPlaylistId) {
        this.userMoodPlaylistId = userMoodPlaylistId;
    }
}

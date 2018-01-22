package ch.hearc.moodymusic.model;

/**
 * Created by axel.rieben on 04.01.2018.
 * Class that represent a line in the table Mapping.
 */

public class Mapping {
    private long id;
    private long moodId;
    private long moodPlaylistId;

    public Mapping(long id, long moodId, long moodPlaylistId) {
        this.id = id;
        this.moodId = moodId;
        this.moodPlaylistId = moodPlaylistId;
    }

    //Getter
    public long getId() {
        return id;
    }

    public long getMoodId() {
        return moodId;
    }

    public long getMoodPlaylistId() {
        return moodPlaylistId;
    }

    //Setter
    public void setId(long id) {
        this.id = id;
    }

    public void setMoodId(long moodId) {
        this.moodId = moodId;
    }

    public void setMoodPlaylistId(long moodPlaylistId) {
        this.moodPlaylistId = moodPlaylistId;
    }
}

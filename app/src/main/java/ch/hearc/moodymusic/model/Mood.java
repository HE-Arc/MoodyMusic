package ch.hearc.moodymusic.model;

/**
 * Created by axel.rieben on 21.11.2017.
 */

public class Mood {
    private long id;
    private String name;

    public Mood(long id, String name) {
        this.id = id;
        this.name = name;
    }

    //Getter
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //Setter
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}

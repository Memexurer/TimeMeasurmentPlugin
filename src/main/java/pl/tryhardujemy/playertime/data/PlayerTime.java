package pl.tryhardujemy.playertime.data;

public class PlayerTime {
    private long joinTime;
    private long playerTime;
    private boolean needUpdate;
    private boolean needInsert;

    PlayerTime(long playerTime) {
        this.playerTime = playerTime;
    }

    PlayerTime() {
        this.playerTime = 0;
        this.needInsert = true;
    }

    void playerJoin() {
        this.joinTime = System.currentTimeMillis();
        this.needUpdate = true;
    }

    private long calculateSeconds() {
        if (joinTime == 0) return 0;
        return (System.currentTimeMillis() - joinTime) / 1000;
    }

    long getPlayerTime() {
        this.playerTime = playerTime + calculateSeconds();
        this.joinTime = System.currentTimeMillis();
        this.needUpdate = true;

        return this.playerTime;
    }

    boolean isNeedUpdate() {
        return needUpdate;
    }

    boolean isNeedInsert() {
        return needInsert;
    }
}

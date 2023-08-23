package de.emir.sql;

import java.util.UUID;

public class Stats {
    private Integer wins;

    private Integer played;

    private Integer looses;

    private Integer broke;

    private String uuid;

    private int points;

    public Stats(String uuid) {
        this.wins = MySQL.getWins(uuid);
        this.played = MySQL.getPlayed(uuid);
        this.looses = Integer.valueOf(this.played.intValue() - this.wins.intValue());
        this.broke = MySQL.getBrokenBeds(uuid);
        this.uuid = uuid;
        this.points = (new PointsSystem(UUID.fromString(uuid))).getPoints().intValue();
    }

    public Integer getWins() {
        return this.wins;
    }

    public Integer getBrokenBeds() {
        return this.broke;
    }

    public Integer getPlayedGames() {
        return this.played;
    }

    public Integer getLooses() {
        return this.looses;
    }

    public void addPlayedGame() {
        MySQL.addPlayed(this.uuid);
    }

    public void addBrokeBed() {
        MySQL.addBrokeBed(this.uuid);
    }

    public int getPoints() {
        return this.points;
    }

    public void addWin() {
        MySQL.addWin(this.uuid);
    }
}
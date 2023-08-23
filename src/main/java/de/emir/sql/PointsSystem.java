package de.emir.sql;

import de.emir.main.Main;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PointsSystem {
    private String uuid;

    public PointsSystem(UUID uid) {
        this.uuid = uid.toString();
    }

    public Integer getPoints() {
        Integer i = Integer.valueOf(0);
        if (MySQL.playerExists(this.uuid)) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + this.uuid + "'");
                if (rs.next())
                    Integer.valueOf(rs.getInt("POINTS"));
                i = Integer.valueOf(rs.getInt("POINTS"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            MySQL.createPlayer(this.uuid);
            getPoints();
        }
        return i;
    }

    public void setPoints(Integer amount) {
        if (MySQL.playerExists(this.uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET POINTS= '" + amount + "' WHERE UUID= '" + this.uuid + "';");
        } else {
            MySQL.createPlayer(this.uuid);
            setPoints(amount);
        }
    }

    public void addPoints(Integer amount) {
        if (MySQL.playerExists(this.uuid)) {
            int i = getPoints().intValue() + amount.intValue();
            Main.mysql.update("UPDATE StatsAPI SET POINTS= '" + i + "' WHERE UUID= '" + this.uuid + "';");
        } else {
            MySQL.createPlayer(this.uuid);
            addPoints(amount);
        }
    }

    public void removePoints(Integer amount) {
        if (MySQL.playerExists(this.uuid)) {
            int i = getPoints().intValue() - amount.intValue();
            Main.mysql.update("UPDATE StatsAPI SET POINTS= '" + i + "' WHERE UUID= '" + this.uuid + "';");
        } else {
            MySQL.createPlayer(this.uuid);
            addPoints(amount);
        }
    }
}

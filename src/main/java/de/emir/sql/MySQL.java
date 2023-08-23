package de.emir.sql;

import de.emir.main.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class MySQL {
    private static Connection connection;

    public MySQL(String host, String db, String user, String password){
        connect();
    }
    public void connect(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MLGRush?autoReconnect=true", "root", "idk");
            System.out.println("[MYSQL] Die Verbindung zur Datenbank wurde hergestellt!");
        }catch(SQLException e){
            System.out.println("[MySQL] Die Verbindung zur Datenbank ist fehlgeschlagen! Fehler: " + e.getMessage());
        }
    }

    public void close(){
        try {
            if (connection != null){
                connection.close();
                System.out.println("[MYSQL] Die Verbindung zur Datenbank wurde Erfolgreich beendet!");
            }
        } catch (SQLException e) {
            System.out.println("[MySQL] Fehler beim beenden der Verbindung zur MySQL! Fehler: " + e.getMessage());
        }
    }

    public static int getRank(String uuid) {
        int rank = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM StatsAPI ORDER BY POINTS DESC");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                rank++;
                String uuid2 = result.getString("UUID");
                if (uuid2.equalsIgnoreCase(uuid))
                    return rank;
            }
            result.close();
            ps.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rank;
    }

    public static UUID getRank(int id) {
        int i = 0;
        ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI ORDER BY POINTS DESC LIMIT " + id);
        try {
            while (rs.next()) {
                i++;
                if (i == id)
                    return UUID.fromString(rs.getString("UUID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isConnected() {
        return (connection != null);
    }

    public void update(String qry) {
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(qry);
            st.close();
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
    }

    public ResultSet query(String qry) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(qry);
        } catch (SQLException e) {
            connect();
            System.err.println(e);
        }
        return rs;
    }

    public static boolean playerExists(String uuid) {
        try {
            ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + uuid + "'");
            if (rs.next())
                return (rs.getString("UUID") != null);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createPlayer(String uuid) {
        if (!playerExists(uuid))
            Main.mysql.update("INSERT INTO StatsAPI (UUID, PLAY, WINS, BREAK) VALUES ('" + uuid +
                    "', '0', '0', '0');");
    }

    public static Integer getKills(String uuid) {
        Integer i = Integer.valueOf(0);
        if (playerExists(uuid)) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + uuid + "'");
                if (rs.next())
                    Integer.valueOf(rs.getInt("BREAK"));
                i = Integer.valueOf(rs.getInt("BREAK"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getKills(uuid);
        }
        return i;
    }

    public static Integer getBrokenBeds(String uuid) {
        Integer i = Integer.valueOf(0);
        if (playerExists(uuid)) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + uuid + "'");
                if (rs.next())
                    Integer.valueOf(rs.getInt("BREAK"));
                i = Integer.valueOf(rs.getInt("BREAK"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getKills(uuid);
        }
        return i;
    }

    public static Integer getWins(String uuid) {
        Integer i = Integer.valueOf(0);
        if (playerExists(uuid)) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + uuid + "'");
                if (rs.next())
                    Integer.valueOf(rs.getInt("WINS"));
                i = Integer.valueOf(rs.getInt("WINS"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getKills(uuid);
        }
        return i;
    }

    public static Integer getPlayed(String uuid) {
        Integer i = Integer.valueOf(0);
        if (playerExists(uuid)) {
            try {
                ResultSet rs = Main.mysql.query("SELECT * FROM StatsAPI WHERE UUID= '" + uuid + "'");
                if (rs.next())
                    Integer.valueOf(rs.getInt("PLAY"));
                i = Integer.valueOf(rs.getInt("PLAY"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            createPlayer(uuid);
            getKills(uuid);
        }
        return i;
    }

    public static void setKills(String uuid, Integer kills) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET KILLS= '" + kills + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKills(uuid, kills);
        }
    }

    public static void setDeaths(String uuid, Integer deaths) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET DEATHS= '" + deaths + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setDeaths(uuid, deaths);
        }
    }

    public static void setPlayed(String uuid, Integer played) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET PLAY= '" + played + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setPlayed(uuid, played);
        }
    }

    public static void setWins(String uuid, Integer wins) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET WINS= '" + wins + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKills(uuid, wins);
        }
    }

    public static void addKill(String uuid) {
        Integer i = Integer.valueOf(1);
        if (playerExists(uuid)) {
            setKills(uuid, Integer.valueOf(getKills(uuid).intValue() + i.intValue()));
        } else {
            createPlayer(uuid);
            addKill(uuid);
        }
    }

    public static void setBrokenBeds(String uuid, Integer amount) {
        if (playerExists(uuid)) {
            Main.mysql.update("UPDATE StatsAPI SET BREAK= '" + amount + "' WHERE UUID= '" + uuid + "';");
        } else {
            createPlayer(uuid);
            setKills(uuid, amount);
        }
    }

    public static void addBrokeBed(String uuid) {
        Integer i = Integer.valueOf(1);
        if (playerExists(uuid)) {
            setBrokenBeds(uuid, Integer.valueOf(getBrokenBeds(uuid).intValue() + i.intValue()));
        } else {
            createPlayer(uuid);
            addKill(uuid);
        }
    }

    public static void addWin(String uuid) {
        Integer i = Integer.valueOf(1);
        if (playerExists(uuid)) {
            setWins(uuid, Integer.valueOf(getWins(uuid).intValue() + i.intValue()));
        } else {
            createPlayer(uuid);
            addWin(uuid);
        }
    }

    public static void addPlayed(String uuid) {
        Integer i = Integer.valueOf(1);
        if (playerExists(uuid)) {
            setPlayed(uuid, Integer.valueOf(getPlayed(uuid).intValue() + i.intValue()));
        } else {
            createPlayer(uuid);
            addPlayed(uuid);
        }
    }
}

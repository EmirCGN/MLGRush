package de.emir.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.emir.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;

public class RankingSystem {
    public static HashMap<Integer, String> map = new HashMap<>();

    public static void loadRanking() {
        try {
            ResultSet rs = Main.mysql.query("SELECT UUID FROM StatsAPI ORDER BY POINTS DESC LIMIT 3");
            int zahl = 0;
            while (rs.next()) {
                zahl++;
                map.put(Integer.valueOf(zahl), rs.getString("UUID"));
            }
            ArrayList<Location> locs = new ArrayList<>();
            locs.add((Location)Main.locs.get("Head1"));
            locs.add((Location)Main.locs.get("Head2"));
            locs.add((Location)Main.locs.get("Head3"));
            for (int i = 0; i < locs.size(); i++) {
                int id = i + 1;
                ((Location)locs.get(i)).getBlock().setType(Material.SKULL);
                Skull s = (Skull)((Location)locs.get(i)).getBlock().getState();
                s.setSkullType(SkullType.PLAYER);
                boolean legit = true;
                String name = "???";
                try {
                    name = Bukkit.getOfflinePlayer(UUID.fromString(map.get(Integer.valueOf(id)))).getName();
                    s.setOwner(name);
                } catch (NullPointerException e) {
                    legit = false;
                    s.setOwner("MHF_Question");
                }
                s.update();
                Location newloc = (Location)Main.locs.get("Sign" + id);
                if (newloc.getBlock().getState() instanceof Sign) {
                    BlockState b = newloc.getBlock().getState();
                    Sign sign = (Sign)b;
                    if (legit) {
                        sign.setLine(0, "- Platz #" + id + " -");
                        sign.setLine(1, "§b"+ name);
                                sign.setLine(2, " ");
                        sign.setLine(3, "§b"+ (new PointsSystem(UUID.fromString(map.get(Integer.valueOf(id))))).getPoints() + " Points");
                                sign.update();
                    } else {
                        sign.setLine(0, "- Platz #" + id + " -");
                        sign.setLine(1, "§b???");
                                sign.setLine(2, " ");
                        sign.setLine(3, "§b??? Points");
                        sign.update();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.obyrkl.cryptominer.Utils;

import com.obyrkl.cryptominer.Main;
import com.obyrkl.cryptominer.Miner.Miner;
import com.obyrkl.cryptominer.Miner.MinerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

public class Database {

    private Main plugin;

    public Database(Main plugin) throws SQLException, ClassNotFoundException {
        this.plugin = plugin;
        createDatabaseFile();
        loadMiners();
    }

    //SQLite
    public void createDatabaseFile() throws SQLException, ClassNotFoundException {
        File SQLFile = new File(plugin.getDataFolder() + File.separator + "Database.db");

        if(!SQLFile.exists()){
            try {
                SQLFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        Connection con = getConnection();
        String sql = "CREATE TABLE IF NOT EXISTS obcrypto_miners(UUID TEXT, Owner TEXT, Mine_Value REAL, Total_Mine_Value REAL, Miner_Balance REAL, Durability REAL, Location TEXT, Status BOOLEAN, Placed BOOLEAN, LastUsed BIGINT);";
        Statement operation = con.createStatement();
        operation.execute(sql);
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        File SQLFile = new File(plugin.getDataFolder() + File.separator + "Database.db");
        DriverManager.getConnection("jdbc:sqlite:"+SQLFile).close();
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:"+SQLFile);
    }

    public void loadMiners() throws SQLException, ClassNotFoundException {
        PreparedStatement statement = null;
        ResultSet result = null;
        Connection conn = getConnection();
        if (conn != null) {
            try {
                String sql = "SELECT * FROM obcrypto_miners";
                statement = conn.prepareStatement(sql);
                result = statement.executeQuery();
                while (result.next()) {
                    String[] locationValue = result.getString("Location").split(",");
                    Location minerLocation = new Location(
                            Bukkit.getWorld(locationValue[0]),
                            Double.valueOf(locationValue[1]),
                            Double.valueOf(locationValue[2]),
                            Double.valueOf(locationValue[3]),
                            Float.valueOf(locationValue[4]),
                            Float.valueOf(locationValue[5])
                    );
                    plugin.getMinerManager().addMiner(new Miner(
                            UUID.fromString(result.getString("UUID")),
                            UUID.fromString(result.getString("Owner")),
                            result.getDouble("Mine_Value"),
                            result.getDouble("Total_Mine_Value"),
                            result.getDouble("Miner_Balance"),
                            result.getDouble("Durability"),
                            minerLocation,
                            result.getBoolean("Status"),
                            result.getBoolean("Placed"),
                            result.getLong("LastUsed"),
                            Main.instance.getConfig().getDouble("Miner.damageRange.min"),
                            Main.instance.getConfig().getDouble("Miner.damageRange.max")
                    ));
                    SimpleDateFormat dateFormat = new SimpleDateFormat();

                }
                plugin.getLogger().log(Level.INFO, plugin.getMinerManager().getMiners().size()+" miner data loaded!");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null)
                        result.close();
                    if (statement != null)
                        statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void saveMiners() throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = null;
        Connection conn = getConnection();
        if (conn != null) {
            try {
                for(Miner miner : Main.instance.getMinerManager().getMiners()){

                    String locationValue =
                            miner.getLocation().getWorld().getName()+
                            ","+
                            miner.getLocation().getX()+
                             ","+
                            miner.getLocation().getY()+
                            ","+
                            miner.getLocation().getZ()+
                            ","+
                            miner.getLocation().getYaw()+
                            ","+
                            miner.getLocation().getPitch();

                    if(minerExist(miner.getUUID())){
                        String data = "UPDATE `obcrypto_miners` SET `Owner` = ?, `Mine_Value` = ?, `Total_Mine_Value` = ?, `Miner_Balance` = ?, `Durability` = ?, `Location` = ?, `Status` = ?, `Placed` = ?, `LastUsed` = ? WHERE `UUID` = ?";
                        preparedStatement = conn.prepareStatement(data);
                        preparedStatement.setString(1, String.valueOf(miner.getOwner()));
                        preparedStatement.setDouble(2, miner.getMineValue());
                        preparedStatement.setDouble(3, miner.getTotalMineValue());
                        preparedStatement.setDouble(4, miner.getMinerBalance());
                        preparedStatement.setDouble(5, miner.getDurability());
                        preparedStatement.setString(6, locationValue);
                        preparedStatement.setBoolean(7, miner.isMinerStatus());
                        preparedStatement.setBoolean(8, miner.isPlaced());
                        preparedStatement.setLong(9, miner.getLastUsed());
                        preparedStatement.setString(10, String.valueOf(miner.getUUID()));
                        preparedStatement.executeUpdate();
                    }else{
                        String data = "INSERT INTO `obcrypto_miners` (`UUID`, `Owner`, `Mine_Value`, `Total_Mine_Value`, `Miner_Balance`, `Durability`, `Location`, `Status`, `Placed`, `LastUsed`) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        preparedStatement = conn.prepareStatement(data);
                        preparedStatement.setString(1, String.valueOf(miner.getUUID()));
                        preparedStatement.setString(2, String.valueOf(miner.getOwner()));
                        preparedStatement.setDouble(3, miner.getMineValue());
                        preparedStatement.setDouble(4, miner.getTotalMineValue());
                        preparedStatement.setDouble(5, miner.getMinerBalance());
                        preparedStatement.setDouble(6, miner.getDurability());
                        preparedStatement.setString(7, locationValue);
                        preparedStatement.setBoolean(8, miner.isMinerStatus());
                        preparedStatement.setBoolean(9, miner.isPlaced());
                        preparedStatement.setLong(10, miner.getLastUsed());
                        preparedStatement.executeUpdate();
                    }
                }
                Main.instance.getLogger().log(Level.INFO, Main.instance.getMinerManager().getMiners().size()+" miner data saved!");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null)
                        preparedStatement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteMiner(Miner miner) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = null;
        Connection conn = getConnection();
        if (conn != null) {
            try {
                String data = "DELETE FROM `obcrypto_miners` WHERE UUID = ?;";
                preparedStatement = conn.prepareStatement(data);
                preparedStatement.setString(1, String.valueOf(miner.getUUID()));
                preparedStatement.executeUpdate();
                Main.instance.getLogger().log(Level.INFO, Main.instance.getMinerManager().getMiners().size()+" miner remove sql!");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (preparedStatement != null)
                        preparedStatement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean minerExist(UUID uuid) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        Connection conn = getConnection();
        if (conn != null){
            try {
                String sql = "SELECT `UUID` FROM `obcrypto_miners` WHERE `UUID` = ? LIMIT 1";
                preparedUpdateStatement = conn.prepareStatement(sql);
                preparedUpdateStatement.setString(1, uuid.toString());
                result = preparedUpdateStatement.executeQuery();
                if (result.next()) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null)
                        result.close();
                    if (preparedUpdateStatement != null)
                        preparedUpdateStatement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}

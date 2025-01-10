package fr.fanto.fantodisbot.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import fr.fanto.fantodisbot.entities.Player;



public class PlayerRepo {

    /*
    private Connection conn;

    public PlayerRepo(Connection conn) {
        this.conn = conn;
    }

    public void createTable() {
        // Créer la table
        String sql = "CREATE TABLE IF NOT EXISTS players (\n"
                + "	riotName text PRIMARY KEY,\n"
                + "	gameName text,\n"
                + "	tagLine text,\n"
                + "	puuid text\n"
                + ");";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int getPlayerId(String riotName) {
        // Récupérer l'ID d'un joueur
        String sql = "SELECT id FROM players WHERE riotName = '" + riotName + "'";
        
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public Player getPlayer(String riotName) {
        // Récupérer un joueur
        String sql = "SELECT * FROM players WHERE riotName = '" + riotName + "'";
        
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            return new Player(rs.getString("riotName"), rs.getString("gameName"), rs.getString("tagLine"), rs.getString("puuid"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Player> getPlayers() {
        // Récupérer tous les joueurs
        String sql = "SELECT * FROM players";
        List<Player> players = new ArrayList<>();
        
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                players.add(new Player(rs.getString("riotName"), rs.getString("gameName"), rs.getString("tagLine"), rs.getString("puuid")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return players;
    }

    public void insertPlayer(Player player) {
        // Insérer un joueur
        String sql = "INSERT INTO players(riotName, gameName, tagLine, puuid) VALUES('" 
        + player.getRiotName() + "', '" 
        + player.getGameName() + "', '" 
        + player.getTagLine() + "', '" 
        + player.getPuuid() + "')";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updatePlayer(Player player) {
        // Mettre à jour un joueur
        String sql = "UPDATE players SET gameName = '" 
        + player.getGameName() + "', tagLine = '" 
        + player.getTagLine() + "', puuid = '" 
        + player.getPuuid() + "' WHERE riotName = '" 
        + player.getRiotName() + "'";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    */
}
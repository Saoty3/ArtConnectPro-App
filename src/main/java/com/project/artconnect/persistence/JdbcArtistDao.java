package com.project.artconnect.persistence;

import com.project.artconnect.util.ConnectionManager;
import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

/**
 * JDBC implementation for ArtistDao.
 * TODO: Students must implement this using JDBC and SQL.
 */
public class JdbcArtistDao implements ArtistDao {

    @Override
    public List<Artist> findAll() {
        // TODO: Implement SELECT * FROM artist
        String sql = "SELECT ArtistID, name, bio, birthYear, phone, city, website, isActive, contactEmail FROM Artiste";
        List<Artist> artists = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Artist artist = mapRowToArtist(rs);
                artists.add(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all artists", e);
        }
        return artists;
    }

    @Override
    public void save(Artist artist) {
        // TODO: Implement INSERT INTO artist(...) VALUES(...)
        String sql = "INSERT INTO Artiste (ArtistID, name, bio, birthYear, phone, city, website, isActive, contactEmail) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateArtistId(artist);
            
            stmt.setString(1, id);
            stmt.setString(2, artist.getName());
            stmt.setString(3, artist.getBio());
            stmt.setInt(4, artist.getBirthYear() != null ? artist.getBirthYear() : 0);
            stmt.setString(5, artist.getPhone());
            stmt.setString(6, artist.getCity());
            stmt.setString(7, artist.getWebsite());
            stmt.setBoolean(8, artist.isActive());
            stmt.setString(9, artist.getContactEmail());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save artist: " + artist.getName(), e);
        }
    }

    @Override
    public void update(Artist artist) {
        // TODO: Implement UPDATE artist SET ... WHERE name = ?
        String sql = "UPDATE Artiste SET name = ?, bio = ?, birthYear = ?, phone = ?, " +
                     "city = ?, website = ?, isActive = ?, contactEmail = ? WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artist.getName());
            stmt.setString(2, artist.getBio());
            stmt.setInt(3, artist.getBirthYear() != null ? artist.getBirthYear() : 0);
            stmt.setString(4, artist.getPhone());
            stmt.setString(5, artist.getCity());
            stmt.setString(6, artist.getWebsite());
            stmt.setBoolean(7, artist.isActive());
            stmt.setString(8, artist.getContactEmail());
            stmt.setString(9, artist.getName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update artist: " + artist.getName(), e);
        }
    }

    @Override
    public void delete(String artistName) {
        // TODO: Implement DELETE FROM artist WHERE name = ?
        String sql = "DELETE FROM Artiste WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artistName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete artist: " + artistName, e);
        }
    }

    @Override
    public List<Artist> findByCity(String city) {
        // TODO: Implement SELECT * FROM artist WHERE city = ?
        String sql = "SELECT ArtistID, name, bio, birthYear, phone, city, website, isActive, contactEmail " +
                     "FROM Artiste WHERE city = ?";
        List<Artist> artists = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    artists.add(mapRowToArtist(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find artists by city: " + city, e);
        }
        return artists;
    }
    // Maps a database row to an Artist object.
    private Artist mapRowToArtist(ResultSet rs) throws SQLException {
        Artist artist = new Artist();
        artist.setName(rs.getString("name"));
        artist.setBio(rs.getString("bio"));
        int birthYear = rs.getInt("birthYear");
        artist.setBirthYear(birthYear == 0 ? null : birthYear);
        artist.setPhone(rs.getString("phone"));
        artist.setCity(rs.getString("city"));
        artist.setWebsite(rs.getString("website"));
        artist.setActive(rs.getBoolean("isActive"));
        artist.setContactEmail(rs.getString("contactEmail"));
        return artist;
    }
    // Generates a unique ID for an artist based on the artist's name.
    private String generateArtistId(Artist artist) {
        String namePrefix = artist.getName().replaceAll("[^A-Za-z]", "");
        if (namePrefix.length() > 3) {
            namePrefix = namePrefix.substring(0, 3);
        }
        namePrefix = namePrefix.toUpperCase();
        if (namePrefix.isEmpty()) {
            namePrefix = "ART";
        }
        return "A" + namePrefix + System.currentTimeMillis() % 10000;
    }
}

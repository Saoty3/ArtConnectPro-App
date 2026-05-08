package com.project.artconnect.persistence;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcArtworkDao implements ArtworkDao {

    @Override
    public List<Artwork> findAll() {
        String sql = "SELECT w.artworkID, w.title, w.createYear, w.type, w.medium, w.height, " +
                     "w.description, w.price, w.status, w.width, w.depth, " +
                     "a.ArtistID, a.name, a.bio, a.birthYear, a.phone, a.city, a.website, a.isActive, a.contactEmail " +
                     "FROM Artwork w JOIN Artiste a ON w.ArtistID = a.ArtistID";
        List<Artwork> artworks = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Artwork artwork = mapRowToArtwork(rs);
                artworks.add(artwork);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all artworks", e);
        }
        return artworks;
    }

    @Override
    public void save(Artwork artwork) {
        String sql = "INSERT INTO Artwork (artworkID, title, createYear, type, medium, height, " +
                     "description, price, status, width, depth, ArtistID, galleryID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateArtworkId(artwork);
            String artistId = getArtistIdByName(conn, artwork.getArtist().getName());
            
            stmt.setString(1, id);
            stmt.setString(2, artwork.getTitle());
            stmt.setInt(3, artwork.getCreationYear() != null ? artwork.getCreationYear() : 0);
            stmt.setString(4, artwork.getType());
            stmt.setString(5, artwork.getMedium());
            stmt.setBigDecimal(6, null); // height
            stmt.setString(7, artwork.getDescription());
            stmt.setBigDecimal(8, java.math.BigDecimal.valueOf(artwork.getPrice()));
            stmt.setString(9, artwork.getStatus() != null ? artwork.getStatus().toString() : "FOR_SALE");
            stmt.setBigDecimal(10, null); // width
            stmt.setBigDecimal(11, null); // depth
            stmt.setString(12, artistId);
            stmt.setString(13, null); // galleryID
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save artwork: " + artwork.getTitle(), e);
        }
    }

    @Override
    public void update(Artwork artwork) {
        String sql = "UPDATE Artwork SET title = ?, createYear = ?, type = ?, medium = ?, " +
                     "description = ?, price = ?, status = ? WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artwork.getTitle());
            stmt.setInt(2, artwork.getCreationYear() != null ? artwork.getCreationYear() : 0);
            stmt.setString(3, artwork.getType());
            stmt.setString(4, artwork.getMedium());
            stmt.setString(5, artwork.getDescription());
            stmt.setBigDecimal(6, java.math.BigDecimal.valueOf(artwork.getPrice()));
            stmt.setString(7, artwork.getStatus() != null ? artwork.getStatus().toString() : "FOR_SALE");
            stmt.setString(8, artwork.getTitle());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update artwork: " + artwork.getTitle(), e);
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Artwork WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete artwork: " + title, e);
        }
    }

    @Override
    public List<Artwork> findByArtistName(String artistName) {
        String sql = "SELECT w.artworkID, w.title, w.createYear, w.type, w.medium, w.height, " +
                     "w.description, w.price, w.status, w.width, w.depth, " +
                     "a.ArtistID, a.name, a.bio, a.birthYear, a.phone, a.city, a.website, a.isActive, a.contactEmail " +
                     "FROM Artwork w JOIN Artiste a ON w.ArtistID = a.ArtistID WHERE a.name = ?";
        List<Artwork> artworks = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, artistName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    artworks.add(mapRowToArtwork(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find artworks by artist: " + artistName, e);
        }
        return artworks;
    }
    
    private Artwork mapRowToArtwork(ResultSet rs) throws SQLException {
        Artwork artwork = new Artwork();
        artwork.setTitle(rs.getString("title"));
        int year = rs.getInt("createYear");
        artwork.setCreationYear(year == 0 ? null : year);
        artwork.setType(rs.getString("type"));
        artwork.setMedium(rs.getString("medium"));
        artwork.setDescription(rs.getString("description"));
        artwork.setPrice(rs.getDouble("price"));
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            try {
                artwork.setStatus(Artwork.Status.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                artwork.setStatus(Artwork.Status.FOR_SALE);
            }
        }
        
        // 构建关联的 Artist 对象
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
        
        artwork.setArtist(artist);
        return artwork;
    }
    
    private String getArtistIdByName(Connection conn, String artistName) throws SQLException {
        String sql = "SELECT ArtistID FROM Artiste WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, artistName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ArtistID");
                }
            }
        }
        throw new SQLException("Artist not found: " + artistName);
    }
    
    private String generateArtworkId(Artwork artwork) {
        String titlePrefix = artwork.getTitle().replaceAll("[^A-Za-z]", "");
        if (titlePrefix.length() > 3) {
            titlePrefix = titlePrefix.substring(0, 3);
        }
        titlePrefix = titlePrefix.toUpperCase();
        if (titlePrefix.isEmpty()) {
            titlePrefix = "WRK";
        }
        return "W" + titlePrefix + System.currentTimeMillis() % 10000;
    }
}

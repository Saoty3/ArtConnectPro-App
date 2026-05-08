package com.project.artconnect.persistence;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWorkshopDao implements WorkshopDao {

    @Override
    public Optional<Workshop> findById(Long id) {
        String sql = "SELECT w.workshopID, w.title, w.date_, w.level, w.price, w.durationMinutes, " +
                     "w.location, w.description, w.maxParticipants, " +
                     "a.ArtistID, a.name, a.bio, a.birthYear, a.phone, a.city, a.website, a.isActive, a.contactEmail " +
                     "FROM Workshop w JOIN Animate an ON w.workshopID = an.workshopID " +
                     "JOIN Artiste a ON an.ArtistID = a.ArtistID WHERE w.workshopID = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "WS" + String.format("%03d", id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToWorkshop(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find workshop by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Workshop> findAll() {
        String sql = "SELECT w.workshopID, w.title, w.date_, w.level, w.price, w.durationMinutes, " +
                     "w.location, w.description, w.maxParticipants, " +
                     "a.ArtistID, a.name, a.bio, a.birthYear, a.phone, a.city, a.website, a.isActive, a.contactEmail " +
                     "FROM Workshop w JOIN Animate an ON w.workshopID = an.workshopID " +
                     "JOIN Artiste a ON an.ArtistID = a.ArtistID";
        List<Workshop> workshops = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                workshops.add(mapRowToWorkshop(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all workshops", e);
        }
        return workshops;
    }
    
    public void save(Workshop workshop) {
        String sql = "INSERT INTO Workshop (workshopID, title, date_, level, price, durationMinutes, " +
                     "location, description, maxParticipants) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateWorkshopId(workshop);
            
            stmt.setString(1, id);
            stmt.setString(2, workshop.getTitle());
            stmt.setDate(3, Date.valueOf(workshop.getDate().toLocalDate()));
            stmt.setString(4, workshop.getLevel());
            stmt.setDouble(5, workshop.getPrice());
            stmt.setInt(6, workshop.getDurationMinutes());
            stmt.setString(7, workshop.getLocation());
            stmt.setString(8, workshop.getDescription());
            stmt.setInt(9, workshop.getMaxParticipants());
            
            stmt.executeUpdate();
            
            // 插入 Animate 关联
            String animateSql = "INSERT INTO Animate (ArtistID, workshopID) VALUES (?, ?)";
            try (PreparedStatement stmt2 = conn.prepareStatement(animateSql)) {
                String artistId = getArtistIdByName(conn, workshop.getInstructor().getName());
                stmt2.setString(1, artistId);
                stmt2.setString(2, id);
                stmt2.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save workshop: " + workshop.getTitle(), e);
        }
    }
    
    public void update(Workshop workshop) {
        String sql = "UPDATE Workshop SET title = ?, date_ = ?, level = ?, price = ?, " +
                     "durationMinutes = ?, location = ?, description = ?, maxParticipants = ? WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, workshop.getTitle());
            stmt.setDate(2, Date.valueOf(workshop.getDate().toLocalDate()));
            stmt.setString(3, workshop.getLevel());
            stmt.setDouble(4, workshop.getPrice());
            stmt.setInt(5, workshop.getDurationMinutes());
            stmt.setString(6, workshop.getLocation());
            stmt.setString(7, workshop.getDescription());
            stmt.setInt(8, workshop.getMaxParticipants());
            stmt.setString(9, workshop.getTitle());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update workshop: " + workshop.getTitle(), e);
        }
    }
    
    public void delete(String title) {
        String sql = "DELETE FROM Workshop WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete workshop: " + title, e);
        }
    }
    
    private Workshop mapRowToWorkshop(ResultSet rs) throws SQLException {
        Workshop workshop = new Workshop();
        workshop.setTitle(rs.getString("title"));
        Date dateSql = rs.getDate("date_");
        if (dateSql != null) {
            workshop.setDate(dateSql.toLocalDate().atStartOfDay());
        }
        workshop.setLevel(rs.getString("level"));
        workshop.setPrice(rs.getDouble("price"));
        workshop.setDurationMinutes(rs.getInt("durationMinutes"));
        workshop.setLocation(rs.getString("location"));
        workshop.setDescription(rs.getString("description"));
        workshop.setMaxParticipants(rs.getInt("maxParticipants"));
        
        // 构建关联的 Artist 对象（指导老师）
        Artist instructor = new Artist();
        instructor.setName(rs.getString("name"));
        instructor.setBio(rs.getString("bio"));
        int birthYear = rs.getInt("birthYear");
        instructor.setBirthYear(birthYear == 0 ? null : birthYear);
        instructor.setPhone(rs.getString("phone"));
        instructor.setCity(rs.getString("city"));
        instructor.setWebsite(rs.getString("website"));
        instructor.setActive(rs.getBoolean("isActive"));
        instructor.setContactEmail(rs.getString("contactEmail"));
        
        workshop.setInstructor(instructor);
        return workshop;
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
    
    private String generateWorkshopId(Workshop workshop) {
        String titlePrefix = workshop.getTitle().replaceAll("[^A-Za-z]", "");
        if (titlePrefix.length() > 3) {
            titlePrefix = titlePrefix.substring(0, 3);
        }
        titlePrefix = titlePrefix.toUpperCase();
        if (titlePrefix.isEmpty()) {
            titlePrefix = "WSH";
        }
        return "WS" + titlePrefix + System.currentTimeMillis() % 10000;
    }
}
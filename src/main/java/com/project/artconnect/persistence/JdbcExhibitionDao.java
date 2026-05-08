package com.project.artconnect.persistence;

import com.project.artconnect.dao.ExhibitionDao;
import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcExhibitionDao implements ExhibitionDao {

    @Override
    public List<Exhibition> findAll() {
        String sql = "SELECT e.exhibitionID, e.title, e.startDate, e.endDate, e.description, e.theme, " +
                     "g.galleryID, g.name, g.address, g.openingHours, g.contactPhone, g.rating, g.website " +
                     "FROM Exhibition e JOIN Gallery g ON e.galleryID = g.galleryID";
        List<Exhibition> exhibitions = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                exhibitions.add(mapRowToExhibition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all exhibitions", e);
        }
        return exhibitions;
    }

    @Override
    public void save(Exhibition exhibition) {
        String sql = "INSERT INTO Exhibition (exhibitionID, title, startDate, endDate, description, theme, galleryID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateExhibitionId(exhibition);
            String galleryId = getGalleryIdByName(conn, exhibition.getGallery().getName());
            
            stmt.setString(1, id);
            stmt.setString(2, exhibition.getTitle());
            stmt.setDate(3, Date.valueOf(exhibition.getStartDate()));
            stmt.setDate(4, Date.valueOf(exhibition.getEndDate()));
            stmt.setString(5, exhibition.getDescription());
            stmt.setString(6, exhibition.getTheme());
            stmt.setString(7, galleryId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save exhibition: " + exhibition.getTitle(), e);
        }
    }

    @Override
    public void update(Exhibition exhibition) {
        String sql = "UPDATE Exhibition SET title = ?, startDate = ?, endDate = ?, description = ?, theme = ? " +
                     "WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, exhibition.getTitle());
            stmt.setDate(2, Date.valueOf(exhibition.getStartDate()));
            stmt.setDate(3, Date.valueOf(exhibition.getEndDate()));
            stmt.setString(4, exhibition.getDescription());
            stmt.setString(5, exhibition.getTheme());
            stmt.setString(6, exhibition.getTitle());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update exhibition: " + exhibition.getTitle(), e);
        }
    }

    @Override
    public void delete(String title) {
        String sql = "DELETE FROM Exhibition WHERE title = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete exhibition: " + title, e);
        }
    }
    
    private Exhibition mapRowToExhibition(ResultSet rs) throws SQLException {
        Exhibition exhibition = new Exhibition();
        exhibition.setTitle(rs.getString("title"));
        exhibition.setStartDate(rs.getDate("startDate").toLocalDate());
        exhibition.setEndDate(rs.getDate("endDate").toLocalDate());
        exhibition.setDescription(rs.getString("description"));
        exhibition.setTheme(rs.getString("theme"));
        
        // 构建关联的 Gallery 对象
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("name"));
        gallery.setAddress(rs.getString("address"));
        gallery.setOpeningHours(rs.getString("openingHours"));
        gallery.setContactPhone(rs.getString("contactPhone"));
        gallery.setRating(rs.getDouble("rating"));
        gallery.setWebsite(rs.getString("website"));
        
        exhibition.setGallery(gallery);
        return exhibition;
    }
    
    private String getGalleryIdByName(Connection conn, String galleryName) throws SQLException {
        String sql = "SELECT galleryID FROM Gallery WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, galleryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("galleryID");
                }
            }
        }
        throw new SQLException("Gallery not found: " + galleryName);
    }
    
    private String generateExhibitionId(Exhibition exhibition) {
        String titlePrefix = exhibition.getTitle().replaceAll("[^A-Za-z]", "");
        if (titlePrefix.length() > 3) {
            titlePrefix = titlePrefix.substring(0, 3);
        }
        titlePrefix = titlePrefix.toUpperCase();
        if (titlePrefix.isEmpty()) {
            titlePrefix = "EXH";
        }
        return "E" + titlePrefix + System.currentTimeMillis() % 10000;
    }
}
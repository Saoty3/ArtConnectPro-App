package com.project.artconnect.persistence;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcGalleryDao implements GalleryDao {

    @Override
    public Optional<Gallery> findById(Long id) {
        String sql = "SELECT galleryID, name, address, openingHours, contactPhone, rating, website FROM Gallery WHERE galleryID = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "G" + String.format("%03d", id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGallery(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find gallery by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Gallery> findAll() {
        String sql = "SELECT galleryID, name, address, openingHours, contactPhone, rating, website FROM Gallery";
        List<Gallery> galleries = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                galleries.add(mapRowToGallery(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all galleries", e);
        }
        return galleries;
    }
    
    public void save(Gallery gallery) {
        String sql = "INSERT INTO Gallery (galleryID, name, address, openingHours, contactPhone, rating, website) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateGalleryId(gallery);
            
            stmt.setString(1, id);
            stmt.setString(2, gallery.getName());
            stmt.setString(3, gallery.getAddress());
            stmt.setString(4, gallery.getOpeningHours());
            stmt.setString(5, gallery.getContactPhone());
            stmt.setDouble(6, gallery.getRating());
            stmt.setString(7, gallery.getWebsite());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save gallery: " + gallery.getName(), e);
        }
    }
    
    public void update(Gallery gallery) {
        String sql = "UPDATE Gallery SET name = ?, address = ?, openingHours = ?, contactPhone = ?, rating = ?, website = ? " +
                     "WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gallery.getName());
            stmt.setString(2, gallery.getAddress());
            stmt.setString(3, gallery.getOpeningHours());
            stmt.setString(4, gallery.getContactPhone());
            stmt.setDouble(5, gallery.getRating());
            stmt.setString(6, gallery.getWebsite());
            stmt.setString(7, gallery.getName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update gallery: " + gallery.getName(), e);
        }
    }
    
    public void delete(String name) {
        String sql = "DELETE FROM Gallery WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete gallery: " + name, e);
        }
    }
    
    private Gallery mapRowToGallery(ResultSet rs) throws SQLException {
        Gallery gallery = new Gallery();
        gallery.setName(rs.getString("name"));
        gallery.setAddress(rs.getString("address"));
        gallery.setOpeningHours(rs.getString("openingHours"));
        gallery.setContactPhone(rs.getString("contactPhone"));
        gallery.setRating(rs.getDouble("rating"));
        gallery.setWebsite(rs.getString("website"));
        return gallery;
    }
    
    private String generateGalleryId(Gallery gallery) {
        String namePrefix = gallery.getName().replaceAll("[^A-Za-z]", "");
        if (namePrefix.length() > 3) {
            namePrefix = namePrefix.substring(0, 3);
        }
        namePrefix = namePrefix.toUpperCase();
        if (namePrefix.isEmpty()) {
            namePrefix = "GLR";
        }
        return "G" + namePrefix + System.currentTimeMillis() % 10000;
    }
}
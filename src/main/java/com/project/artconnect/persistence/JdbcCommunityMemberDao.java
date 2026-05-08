package com.project.artconnect.persistence;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.util.ConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCommunityMemberDao implements CommunityMemberDao {

    @Override
    public Optional<CommunityMember> findById(Long id) {
        String sql = "SELECT memberID, name, email, birthYear, phone, city, membershipType FROM Member_ WHERE memberID = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "M" + String.format("%03d", id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find member by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<CommunityMember> findAll() {
        String sql = "SELECT memberID, name, email, birthYear, phone, city, membershipType FROM Member_";
        List<CommunityMember> members = new ArrayList<>();
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all members", e);
        }
        return members;
    }
    
    public void save(CommunityMember member) {
        String sql = "INSERT INTO Member_ (memberID, name, email, birthYear, phone, city, membershipType) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = generateMemberId(member);
            
            stmt.setString(1, id);
            stmt.setString(2, member.getName());
            stmt.setString(3, member.getEmail());
            stmt.setInt(4, member.getBirthYear() != null ? member.getBirthYear() : 0);
            stmt.setString(5, member.getPhone());
            stmt.setString(6, member.getCity());
            stmt.setString(7, member.getMembershipType() != null ? member.getMembershipType() : "Standard");
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save member: " + member.getName(), e);
        }
    }
    
    public void update(CommunityMember member) {
        String sql = "UPDATE Member_ SET name = ?, email = ?, birthYear = ?, phone = ?, city = ?, membershipType = ? " +
                     "WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setInt(3, member.getBirthYear() != null ? member.getBirthYear() : 0);
            stmt.setString(4, member.getPhone());
            stmt.setString(5, member.getCity());
            stmt.setString(6, member.getMembershipType());
            stmt.setString(7, member.getName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update member: " + member.getName(), e);
        }
    }
    
    public void delete(String name) {
        String sql = "DELETE FROM Member_ WHERE name = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete member: " + name, e);
        }
    }
    
    private CommunityMember mapRowToMember(ResultSet rs) throws SQLException {
        CommunityMember member = new CommunityMember();
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        int birthYear = rs.getInt("birthYear");
        member.setBirthYear(birthYear == 0 ? null : birthYear);
        member.setPhone(rs.getString("phone"));
        member.setCity(rs.getString("city"));
        member.setMembershipType(rs.getString("membershipType"));
        return member;
    }
    
    private String generateMemberId(CommunityMember member) {
        String namePrefix = member.getName().replaceAll("[^A-Za-z]", "");
        if (namePrefix.length() > 3) {
            namePrefix = namePrefix.substring(0, 3);
        }
        namePrefix = namePrefix.toUpperCase();
        if (namePrefix.isEmpty()) {
            namePrefix = "MBR";
        }
        return "M" + namePrefix + System.currentTimeMillis() % 10000;
    }
}
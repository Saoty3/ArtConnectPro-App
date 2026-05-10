package com.project.artconnect.dao;

import com.project.artconnect.model.CommunityMember;
import java.util.List;
import java.util.Optional;

public interface CommunityMemberDao {
    Optional<CommunityMember> findById(Long id);

    List<CommunityMember> findAll();

    public void save(CommunityMember member);

    public void update(CommunityMember member);

    public void delete(String name);
}

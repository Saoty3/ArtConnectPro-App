package com.project.artconnect.service;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    List<CommunityMember> getAllMembers();

    Optional<CommunityMember> getMemberByEmail(String name);

    List<Review> getReviewsByMember(CommunityMember member);

    public void createMember(CommunityMember member);

    public void updateMember(CommunityMember member);

    public void deleteMember(String email);
}

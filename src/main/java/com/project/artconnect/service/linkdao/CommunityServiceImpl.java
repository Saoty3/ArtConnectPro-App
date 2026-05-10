package com.project.artconnect.service.linkdao;

import com.project.artconnect.dao.CommunityMemberDao;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Review;
import com.project.artconnect.persistence.JdbcCommunityMemberDao;
import com.project.artconnect.service.CommunityService;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class CommunityServiceImpl implements CommunityService {

    private final CommunityMemberDao communityMemberDao;

    public CommunityServiceImpl() {
        this.communityMemberDao = new JdbcCommunityMemberDao();
    }

    @Override
    public List<CommunityMember> getAllMembers() {
        return communityMemberDao.findAll();
    }

    @Override
    public Optional<CommunityMember> getMemberByEmail(String email) {
        return communityMemberDao.findAll()
                .stream()
                .filter(member -> member.getEmail() != null
                        && member.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Review> getReviewsByMember(CommunityMember member) {
        return new ArrayList<>();
    }

    @Override
    public void createMember(CommunityMember member) {
        communityMemberDao.save(member);
    }

    @Override
    public void updateMember(CommunityMember member) {
        communityMemberDao.update(member);
    }

    @Override
    public void deleteMember(String email) {
        communityMemberDao.delete(email);
    }
}
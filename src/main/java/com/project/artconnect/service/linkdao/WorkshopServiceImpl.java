package com.project.artconnect.service.linkdao;

import com.project.artconnect.dao.WorkshopDao;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.model.Booking;
import com.project.artconnect.persistence.JdbcWorkshopDao;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.model.CommunityMember;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class WorkshopServiceImpl implements WorkshopService {

    private final WorkshopDao workshopDao;

    public WorkshopServiceImpl() {
        this.workshopDao = new JdbcWorkshopDao();
    }

    @Override
    public List<Workshop> getAllWorkshops() {
        return workshopDao.findAll();
    }

    @Override
    public void bookWorkshop(Workshop workshop, CommunityMember member) {

    }

    @Override
    public List<Booking> getBookingsByMember(CommunityMember member) {
        return new ArrayList<>();
    }

    @Override
    public Optional<Workshop> getWorkshopByTitle(String title) {
        return workshopDao.findAll()
                .stream()
                .filter(workshop -> workshop.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public void createWorkshop(Workshop workshop) {
        workshopDao.save(workshop);
    }

    @Override
    public void updateWorkshop(Workshop workshop) {
        workshopDao.update(workshop);
    }

    @Override
    public void deleteWorkshop(String title) {
        workshopDao.delete(title);
    }
}
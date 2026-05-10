package com.project.artconnect.dao;

import com.project.artconnect.model.Workshop;
import java.util.List;
import java.util.Optional;

public interface WorkshopDao {
    Optional<Workshop> findById(Long id);

    List<Workshop> findAll();

    public void save(Workshop workshop);

    public void update(Workshop workshop);

    public void delete(String title);
}

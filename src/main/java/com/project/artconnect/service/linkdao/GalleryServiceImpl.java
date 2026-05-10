package com.project.artconnect.service.linkdao;

import com.project.artconnect.dao.GalleryDao;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.persistence.JdbcGalleryDao;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.model.Exhibition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GalleryServiceImpl implements GalleryService {

    private final GalleryDao galleryDao;

    @Override
    public List<Exhibition> getExhibitionsByGallery(Gallery gallery) {
        return new ArrayList<>();
    }

    public GalleryServiceImpl() {
        this.galleryDao = new JdbcGalleryDao();
    }

    @Override
    public List<Gallery> getAllGalleries() {
        return galleryDao.findAll();
    }

    @Override
    public Optional<Gallery> getGalleryByName(String name) {
        return galleryDao.findAll()
                .stream()
                .filter(gallery -> gallery.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void createGallery(Gallery gallery) {
        galleryDao.save(gallery);
    }

    @Override
    public void updateGallery(Gallery gallery) {
        galleryDao.update(gallery);
    }

    @Override
    public void deleteGallery(String name) {
        galleryDao.delete(name);
    }
}
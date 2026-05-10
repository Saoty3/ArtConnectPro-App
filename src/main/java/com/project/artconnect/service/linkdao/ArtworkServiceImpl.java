package com.project.artconnect.service.linkdao;

import com.project.artconnect.dao.ArtworkDao;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.model.Artist;
import com.project.artconnect.persistence.JdbcArtworkDao;
import com.project.artconnect.service.ArtworkService;

import java.util.List;
import java.util.Optional;

public class ArtworkServiceImpl implements ArtworkService {

    private final ArtworkDao artworkDao;

    @Override
    public List<Artwork> getArtworksByArtist(Artist artist) {

        return artworkDao.findAll()
                .stream()
                .filter(artwork ->
                        artwork.getArtist() != null &&
                                artwork.getArtist().equals(artist))
                .toList();
    }

    public ArtworkServiceImpl() {
        this.artworkDao = new JdbcArtworkDao();
    }

    @Override
    public List<Artwork> getAllArtworks() {
        return artworkDao.findAll();
    }

    @Override
    public Optional<Artwork> getArtworkByTitle(String title) {
        return artworkDao.findAll()
                .stream()
                .filter(artwork -> artwork.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public void createArtwork(Artwork artwork) {
        artworkDao.save(artwork);
    }

    @Override
    public void updateArtwork(Artwork artwork) {
        artworkDao.update(artwork);
    }

    @Override
    public void deleteArtwork(String title) {
        artworkDao.delete(title);
    }
}

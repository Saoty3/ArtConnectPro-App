package com.project.artconnect.service.linkdao;

import com.project.artconnect.dao.ArtistDao;
import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.persistence.JdbcArtistDao;
import com.project.artconnect.service.ArtistService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArtistServiceImpl implements ArtistService {
    private final ArtistDao artistDao;

    public ArtistServiceImpl() {
        this.artistDao = new JdbcArtistDao();
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistDao.findAll();
    }

    @Override
    public Optional<Artist> getArtistByName(String name) {

        return artistDao.findAll()
                .stream()
                .filter(artist -> artist.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public void createArtist(Artist artist) {
        artistDao.save(artist);
    }

    @Override
    public void updateArtist(Artist artist) {
        artistDao.update(artist);
    }

    @Override
    public void deleteArtist(String name) {
        artistDao.delete(name);
    }

    public List<Discipline> getAllDisciplines() {

        List<Discipline> disciplines = new ArrayList<>();

        for (Artist artist : artistDao.findAll()) {

            if (artist.getDisciplines() != null) {

                for (Discipline discipline : artist.getDisciplines()) {

                    if (!disciplines.contains(discipline)) {
                        disciplines.add(discipline);
                    }
                }
            }
        }

        return disciplines;
    }

    @Override
    public List<Artist> searchArtists(String query,
                                      String disciplineName,
                                      String city) {

        List<Artist> results = new ArrayList<>();

        for (Artist artist : artistDao.findAll()) {

            boolean matches = true;

            if (query != null && !query.isBlank()) {
                matches = artist.getName()
                        .toLowerCase()
                        .contains(query.toLowerCase());
            }

            if (matches &&
                    city != null &&
                    !city.isBlank()) {

                matches = artist.getCity() != null &&
                        artist.getCity()
                                .equalsIgnoreCase(city);
            }

            if (matches &&
                    disciplineName != null &&
                    !disciplineName.isBlank()) {

                matches = false;

                if (artist.getDisciplines() != null) {

                    for (Discipline discipline : artist.getDisciplines()) {
                        if (discipline.getName().equalsIgnoreCase(disciplineName)) {
                            matches = true;
                            break;
                        }
                    }
                }
            }

            if (matches) {
                results.add(artist);
            }
        }

        return results;
    }
}

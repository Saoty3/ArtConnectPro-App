package com.project.artconnect;

import com.project.artconnect.model.Artist;
import com.project.artconnect.persistence.JdbcArtistDao;

import java.util.List;

public class TestDatabase {

    public static void main(String[] args) {

        JdbcArtistDao dao = new JdbcArtistDao();

        try {

            // TEST INSERT
            Artist artist = new Artist();

            artist.setName("Test Artist");
            artist.setBio("Bio de test");
            artist.setBirthYear(1995);
            artist.setCity("Paris");
            artist.setPhone("0102030405");
            artist.setWebsite("www.test.com");
            artist.setActive(true);
            artist.setContactEmail("test@test.com");

            dao.save(artist);

            System.out.println("Artist inserted successfully!");

            // TEST SELECT
            List<Artist> artists = dao.findAll();

            System.out.println("Artists in database:");

            for (Artist a : artists) {
                System.out.println(a.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
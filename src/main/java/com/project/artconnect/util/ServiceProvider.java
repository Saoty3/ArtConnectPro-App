package com.project.artconnect.util;

import com.project.artconnect.service.*;
import com.project.artconnect.service.impl.*;
import com.project.artconnect.service.linkdao.*;

/**
 * Service Provider to manage singleton instances of services and handle their
 * initialization.
 */
public class ServiceProvider {
    private static final ArtistService artistService = new ArtistServiceImpl();
    private static final ArtworkService artworkService = new ArtworkServiceImpl();
    private static final GalleryService galleryService = new GalleryServiceImpl();
    private static final WorkshopService workshopService = new WorkshopServiceImpl();
    private static final CommunityService communityService = new CommunityServiceImpl();
    private static final ExhibitionService exhibitionService = new ExhibitionServiceImpl();



    public static ArtistService getArtistService() {
        return artistService;
    }

    public static ArtworkService getArtworkService() {
        return artworkService;
    }

    public static GalleryService getGalleryService() {
        return galleryService;
    }

    public static WorkshopService getWorkshopService() {
        return workshopService;
    }

    public static CommunityService getCommunityService() {
        return communityService;
    }

    public static ExhibitionService getExhibitionService() { return exhibitionService;}
}

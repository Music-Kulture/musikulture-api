package br.com.musikulture.music;

import java.util.List;

public class TrackAnalyzed {

    private String spotifyTrackId;
    private String musixMatchTrackId;
    private String trackName;
    private String principalArtist;
    private List<String> allArtists;
    private String language;
    private List<String> genres;


    public TrackAnalyzed(String spotifyTrackId, String musixMatchTrackId, String trackName, String principalArtist, List<String> allArtists, String language, List<String> genres) {
        this.spotifyTrackId = spotifyTrackId;
        this.musixMatchTrackId = musixMatchTrackId;
        this.trackName = trackName;
        this.principalArtist = principalArtist;
        this.allArtists = allArtists;
        this.language = language;
        this.genres = genres;
    }

    public TrackAnalyzed() {
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getSpotifyTrackId() {
        return spotifyTrackId;
    }

    public void setSpotifyTrackId(String spotifyTrackId) {
        this.spotifyTrackId = spotifyTrackId;
    }

    public String getMusixMatchTrackId() {
        return musixMatchTrackId;
    }

    public void setMusixMatchTrackId(String musixMatchTrackId) {
        this.musixMatchTrackId = musixMatchTrackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getPrincipalArtist() {
        return principalArtist;
    }

    public void setPrincipalArtist(String principalArtist) {
        this.principalArtist = principalArtist;
    }

    public List<String> getAllArtists() {
        return allArtists;
    }

    public void setAllArtists(List<String> allArtists) {
        this.allArtists = allArtists;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "TrackAnalyzed{" +
                "spotifyTrackId='" + spotifyTrackId + '\'' +
                ", musixMatchTrackId='" + musixMatchTrackId + '\'' +
                ", trackName='" + trackName + '\'' +
                ", principalArtist='" + principalArtist + '\'' +
                ", allArtists=" + allArtists +
                ", language='" + language + '\'' +
                ", genres=" + genres +
                '}';
    }
}

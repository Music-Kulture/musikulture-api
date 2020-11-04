package br.com.musikulture.music;

import br.com.musikulture.spotify.WebAPISpotifyRequest;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackAnalyzed {

    private String spotifyTrackId;
    private String musixMatchTrackId;
    private String trackName;
    private String principalArtist;
    private String principalArtistId;
    private Map<String, String> allArtists;
    private String language;
    private List<String> genres;


    public TrackAnalyzed(String spotifyTrackId, String musixMatchTrackId, String trackName, String principalArtist, String principalArtistId, Map<String, String> allArtists, String language, List<String> genres) {
        this.spotifyTrackId = spotifyTrackId;
        this.musixMatchTrackId = musixMatchTrackId;
        this.trackName = trackName;
        this.principalArtist = principalArtist;
        this.principalArtistId = principalArtistId;
        this.allArtists = allArtists;
        this.language = language;
        this.genres = genres;
    }

    public TrackAnalyzed(TrackSimplified trackSimplified, String language, int musixMatchTrackId) {
        this.language = language;
        this.musixMatchTrackId = Integer.toString(musixMatchTrackId);
        allArtists = new HashMap<>();
        if (trackSimplified.getArtists() != null && trackSimplified.getArtists()[0].getId() != null && trackSimplified.getArtists()[0].getName() != null){
            for (ArtistSimplified artist : trackSimplified.getArtists()) {
                this.allArtists.put(artist.getName(), artist.getId());
            }
            this.principalArtist = trackSimplified.getArtists()[0].getName();
            this.principalArtistId = trackSimplified.getArtists()[0].getId();
        }
        else {
            this.allArtists.put("no value", "no value");
            this.principalArtist = "no value";
            this.principalArtistId = "no value";
        }
        this.trackName = trackSimplified.getName();
        this.spotifyTrackId = trackSimplified.getId();

        this.genres = WebAPISpotifyRequest.getGenres(trackSimplified.getArtists());
    }

    public TrackAnalyzed() {
    }

    public String getPrincipalArtistId() {
        return principalArtistId;
    }

    public void setPrincipalArtistId(String principalArtistId) {
        this.principalArtistId = principalArtistId;
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

    public Map<String, String> getAllArtists() {
        return allArtists;
    }

    public void setAllArtists(Map<String, String> allArtists) {
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
                ", principalArtistId='" + principalArtistId + '\'' +
                ", allArtists=" + allArtists +
                ", language='" + language + '\'' +
                ", genres=" + genres +
                '}';
    }
}

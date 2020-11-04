package br.com.musikulture.musixmatch;

import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.spotify.WebAPISpotifyRequest;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.apache.tika.language.detect.LanguageDetector;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusixMatchApiRequest {

    public static final String apiKey = "9ee1a5df4a4c810accbf5821d848c122";


    public static List<TrackAnalyzed> analyzeSavedTracks(List<TrackSimplified> tracks,
                                                    String lang) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        tracks.forEach(trackSimplified -> {
            String trackName = trackSimplified.getName();
            String artistName = trackSimplified.getArtists()[0].getName();

            Track track;
            try {
                track = musixMatch.getMatchingTrack(trackName, artistName);

                TrackData data = track.getTrack();

                int trackID = data.getTrackId();

                Lyrics lyrics = musixMatch.getLyrics(trackID);

                String language = LanguageDetector.getDefaultLanguageDetector().loadModels().detect(lyrics.getLyricsBody()).getLanguage();

                System.out.print(".");


                if (language.equalsIgnoreCase(lang)){
                    trackAnalyzeds.add(new TrackAnalyzed(trackSimplified,language,trackID));
                    System.out.println("IT MATCHES THE LANGUAGE");
                }


            } catch (MusixMatchException | IOException | RuntimeException e ) {
                System.out.println("m=analyzeSavedTracks msg= Error: "+ e.getMessage()+", no music");
            }
        });
        return trackAnalyzeds;
    }


    public static List<TrackAnalyzed> analyze(List<SavedTrack> tracks) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        tracks.forEach(savedTrack -> {
            String trackName = savedTrack.getTrack().getName();
            String artistName = savedTrack.getTrack().getArtists()[0].getName();
            String artistId = savedTrack.getTrack().getArtists()[0].getId();
            List<String> genres = WebAPISpotifyRequest.getGenres(savedTrack.getTrack().getArtists());

            Track track = null;
            try {
                track = musixMatch.getMatchingTrack(trackName, artistName);

                TrackData data = track.getTrack();


                int trackID = data.getTrackId();

                Lyrics lyrics = musixMatch.getLyrics(trackID);

                String language = LanguageDetector.getDefaultLanguageDetector().loadModels().detect(lyrics.getLyricsBody()).getLanguage();

                System.out.println(language);


                Map<String,String>allArtists = new HashMap<>();

                for (ArtistSimplified artist : savedTrack.getTrack().getArtists()) {
                    allArtists.put(artist.getName(),artist.getId());
                }
                trackAnalyzeds.add(new TrackAnalyzed(savedTrack.getTrack().getId(),
                        (Integer.toString(trackID)),
                        trackName,
                        artistName,
                        artistId,
                        allArtists,
                        language,
                        genres)
                );


            } catch (MusixMatchException | IOException e) {
                e.printStackTrace();
                System.out.println("No Music found!");
            }
        });

        return trackAnalyzeds;
    }

    public MusixMatchApiRequest() {
    }
}

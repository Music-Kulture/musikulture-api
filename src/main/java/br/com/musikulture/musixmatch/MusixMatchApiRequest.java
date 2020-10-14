package br.com.musikulture.musixmatch;

import br.com.musikulture.music.TrackAnalyzed;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import org.apache.tika.language.detect.LanguageDetector;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusixMatchApiRequest {

    public final String apiKey = "9ee1a5df4a4c810accbf5821d848c122";

    public List<TrackAnalyzed> analyze(List<SavedTrack> tracks,
                                              String lang) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        tracks.forEach(savedTrack -> {
            String trackName = savedTrack.getTrack().getName();
            String artistName = savedTrack.getTrack().getArtists()[0].getName();

            Track track = null;
            try {
                track = musixMatch.getMatchingTrack(trackName, artistName);

                TrackData data = track.getTrack();


                int trackID = data.getTrackId();

                Lyrics lyrics = musixMatch.getLyrics(trackID);

                String language = LanguageDetector.getDefaultLanguageDetector().loadModels().detect(lyrics.getLyricsBody()).getLanguage();

                System.out.println(language);


                List<String> allArtists = new ArrayList<>();

                for (ArtistSimplified artist : savedTrack.getTrack().getArtists()) {
                    allArtists.add(artist.getName());
                }

                if (language.equals(lang))
                    trackAnalyzeds.add(new TrackAnalyzed(savedTrack.getTrack().getId(),
                            (Integer.toString(trackID)),
                            trackName,
                            artistName,
                            allArtists,
                            language));


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

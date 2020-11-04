package br.com.musikulture.spotify;

import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.musixmatch.MusixMatchApiRequest;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class RecommendationBuilder {


    List<TrackAnalyzed> trackAnalyzedList = new ArrayList<>();
    List<TrackAnalyzed> recommendationList = new ArrayList<>();


    public List<TrackAnalyzed> getRecommendationListFromTrackAnalyzedList(String lang) {

        AtomicReference<StringBuilder> artistSeed = new AtomicReference<>(new StringBuilder());
        AtomicReference<StringBuilder> genreSeed = new AtomicReference<>(new StringBuilder());
        AtomicReference<StringBuilder> trackSeed = new AtomicReference<>(new StringBuilder());

        trackAnalyzedList.forEach(trackAnalyzed -> {
            genreSeed.set(new StringBuilder());
            artistSeed.set(new StringBuilder());
            trackSeed.set(new StringBuilder());

            artistSeed.get().append(trackAnalyzed.getPrincipalArtistId());
            trackAnalyzed.getGenres().subList(0, Math.min(trackAnalyzed.getGenres().size(), 3)).forEach(s -> {
                genreSeed.get().append(s).append(",");
            });


            trackSeed.get().append(trackAnalyzed.getSpotifyTrackId());
            genreSeed.get().deleteCharAt(genreSeed.get().length() - 1);


            System.out.println(artistSeed.toString());
            System.out.println(genreSeed.toString());
            System.out.println(trackSeed.toString());

            List<TrackSimplified> preRecommendationList = WebAPISpotifyRequest.getRecommendations(artistSeed.toString(), genreSeed.toString(), trackSeed.toString(), lang);


            recommendationList.addAll(MusixMatchApiRequest.analyzeSavedTracks(preRecommendationList, lang));


        });


        recommendationList.forEach(System.out::println);


        return recommendationList;
    }


    public RecommendationBuilder() {
    }

    public List<TrackAnalyzed> getTrackAnalyzedList() {
        return trackAnalyzedList;
    }

    public void setTrackAnalyzedList(List<TrackAnalyzed> trackAnalyzedList) {
        this.trackAnalyzedList = trackAnalyzedList;
    }

    public List<TrackAnalyzed> getRecommendationList() {
        return recommendationList;
    }

    public void setRecommendationList(List<TrackAnalyzed> recommendationList) {
        this.recommendationList = recommendationList;
    }

    public RecommendationBuilder(List<TrackAnalyzed> trackAnalyzedList) {
        this.trackAnalyzedList = trackAnalyzedList;
    }
}

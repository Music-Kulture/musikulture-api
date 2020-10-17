package br.com.musikulture.spotify;

import br.com.musikulture.music.TrackAnalyzed;
import com.wrapper.spotify.model_objects.specification.SavedTrack;

import java.util.ArrayList;
import java.util.List;

public class RecommendationBuilder {


    List<TrackAnalyzed> trackAnalyzedList = new ArrayList<>();
    List<SavedTrack> recommendationList = new ArrayList<>();


    public List<SavedTrack> getRecommendationListFromTrackAnalyzedList(){

        trackAnalyzedList.forEach(trackAnalyzed -> {






        });


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

    public List<SavedTrack> getRecommendationList() {
        return recommendationList;
    }

    public void setRecommendationList(List<SavedTrack> recommendationList) {
        this.recommendationList = recommendationList;
    }

    public RecommendationBuilder(List<TrackAnalyzed> trackAnalyzedList, List<SavedTrack> recommendationList) {
        this.trackAnalyzedList = trackAnalyzedList;
        this.recommendationList = recommendationList;
    }
}

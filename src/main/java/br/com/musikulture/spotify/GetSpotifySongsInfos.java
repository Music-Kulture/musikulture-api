package br.com.musikulture.spotify;

import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.musixmatch.MusixMatchApiRequest;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class GetSpotifySongsInfos {

    @GetMapping("/tracks")
    public List<TrackAnalyzed> objects(@RequestParam(name = "token") String token,
                                       @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<SavedTrack> savedTracks = WebAPISpotifyRequest.getUsersSavedTracks_Sync(token);

        RecommendationBuilder recommendationBuilder = new RecommendationBuilder(
                MusixMatchApiRequest.analyze(Objects.requireNonNull(savedTracks)));


        return recommendationBuilder.getRecommendationListFromTrackAnalyzedList(lang);


    }

    @GetMapping("/")
    public String home() {
        return "DEU BOM, INICIOU!";
    }

}

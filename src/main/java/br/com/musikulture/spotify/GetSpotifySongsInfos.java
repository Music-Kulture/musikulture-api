package br.com.musikulture.spotify;

import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.musixmatch.MusixMatchApiRequest;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.message.HttpResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class GetSpotifySongsInfos {

    @Autowired
    WebAPISpotifyRequest webAPISpotifyRequest;

    @Autowired
    MusixMatchApiRequest musixMatchApiRequest;

    @GetMapping("/tracks")
    public ResponseEntity<?> objects(@RequestParam(name = "token") String token,
                                  @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<SavedTrack> savedTracks = webAPISpotifyRequest.getUsersSavedTracks_Sync(token);

        RecommendationBuilder recommendationBuilder = new RecommendationBuilder(
                musixMatchApiRequest.analyze(Objects.requireNonNull(savedTracks), webAPISpotifyRequest));


        return ResponseEntity.ok(recommendationBuilder.getRecommendationListFromTrackAnalyzedList(lang, webAPISpotifyRequest, musixMatchApiRequest));


    }

    @GetMapping("/")
    public String home() {
        return "DEU BOM, INICIOU!";
    }

}

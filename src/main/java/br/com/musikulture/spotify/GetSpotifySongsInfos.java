package br.com.musikulture.spotify;

import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.musixmatch.MusicxMatchApiRequest;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import org.jmusixmatch.MusixMatchException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class GetSpotifySongsInfos {

    @GetMapping("/tracks")
    public List<TrackAnalyzed> objects(@RequestParam(name = "token") String token,
                                       @RequestParam(name = "market", defaultValue = "BR") String market) throws MusixMatchException {
        List<SavedTrack> savedTracks = WebAPISpotifyRequest.getUsersSavedTracks_Sync(token, market);

        return MusicxMatchApiRequest.analyze(savedTracks);


    }

}

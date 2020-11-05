package br.com.musikulture.spotify;

import br.com.musikulture.entity.Genre;
import br.com.musikulture.repository.ArtistRepository;
import br.com.musikulture.repository.GenreRepository;
import br.com.musikulture.repository.MusicLanguageRepository;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class WebAPISpotifyRequest {

    private final Logger LOGGER = LoggerFactory.getLogger(WebAPISpotifyRequest.class);

    @Autowired
    private MusicLanguageRepository musicLanguageRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    private SpotifyApi spotifyApi;

    private final String clientId = System.getenv("SPOTIFY_WEB_API_CLIENT-ID");

    private final String clientSecret = System.getenv("SPOTIFY_WEB_API_SECRET-ID");

    private void createApi(String accessToken) {
        spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .build();
    }

    private GetUsersSavedTracksRequest getUsersSavedTracksRequest = null;

    private GetArtistRequest getArtistRequest = null;

    private GetRecommendationsRequest getRecommendationsRequest = null;

    public List<TrackSimplified> getRecommendations(String artistSeed, String genreSeed, String trackSeed, String lang) {
        List<TrackSimplified> recommendationTracks = new ArrayList<>();

        try {
            getRecommendationsRequest = spotifyApi
                    .getRecommendations()
//                    .market(CountryCode.getByCodeIgnoreCase(lang))
                    .limit(100)
                    .seed_artists(artistSeed)
                    .seed_genres(genreSeed)
                    .seed_tracks(trackSeed)
                    .build();
            recommendationTracks.addAll(Arrays.asList(getRecommendationsRequest.execute().getTracks()));


        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.error("m=getRecommendations, msg=Error: {}",e.getMessage());
        }


        return recommendationTracks;
    }

    public List<SavedTrack> getUsersSavedTracks_Sync(String token) {
        try {
            createApi(token);
            getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .limit(50)
                    .offset(0)
//                    .market(CountryCode.BR)
                    .build();

            return Arrays.asList(getUsersSavedTracksRequest.execute().getItems());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            LOGGER.error("m=getRecommendations, msg=Error: {}",e.getMessage());
        }
        return null;
    }

    public List<String> getGenres(ArtistSimplified[] artistsId) {
        List<String> genres = new ArrayList<>();
        List<String> artists = new ArrayList<>();

        Arrays.asList(artistsId).forEach(artist -> artists.add(artist.getId()));

        Arrays.asList(artistsId).forEach(artist -> {

            getArtistRequest = spotifyApi
                    .getArtist(artist.getId())
                    .build();
            try {
                List<String> byArtists = new ArrayList<>();
                for (int i = 0; i < artists.size(); i++) {
                    String s1 = artists.get(i);
                    String artistsAndOffset = genreRepository.findByArtistsAndOffset(s1, i);
                    if (artistsAndOffset != null)
                        byArtists.add(artistsAndOffset);
                }
                if (byArtists.size() == 0 && artistsId.length < 5) {
                    List<String> genres1 = Arrays.asList(getArtistRequest.execute().getGenres());
                    List<Genre> genreList = new ArrayList<>();
                    genres1.forEach(s -> {
                        if (!genreRepository.existsByName(s))
                            genreList.add(new Genre(s));
                    });
                    if (!genreList.isEmpty()) {
                        genreRepository.saveAll(genreList);
                        genreRepository.flush();
                    }
                    genres.addAll(genres1);
                } else
                    genres.addAll(byArtists);

            } catch (IOException | SpotifyWebApiException | ParseException e) {
                LOGGER.error("m=getRecommendations, msg=Error: {}",e.getMessage());
            }
        });

        return genres;
    }
}

package br.com.musikulture.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
import com.wrapper.spotify.requests.data.browse.GetRecommendationsRequest;
import com.wrapper.spotify.requests.data.library.GetUsersSavedTracksRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class WebAPISpotifyRequest {

    private static SpotifyApi spotifyApi;

    private static void createApi(String accessToken) {
        spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken)
                .setClientId("c7d4ed444f9a4dd691f6bc93c4760568")
                .setClientSecret("f959b87840334d0282cd579eb8ffcc2d")
                .build();
    }

    private static GetUsersSavedTracksRequest getUsersSavedTracksRequest = null;

    private static GetArtistRequest getArtistRequest = null;

    private static GetRecommendationsRequest getRecommendationsRequest= null;

    public static List<TrackSimplified> getRecommendations(String artistSeed, String genreSeed, String trackSeed, String lang){
        List<TrackSimplified> recommendationTracks = new ArrayList<>();

        try{
            getRecommendationsRequest = spotifyApi
                    .getRecommendations()
//                    .market(CountryCode.getByCodeIgnoreCase(lang))
                    .limit(5)
                    .seed_artists(artistSeed)
                    .seed_genres(genreSeed)
                    .seed_tracks(trackSeed)
                    .build();
            recommendationTracks.addAll(Arrays.asList(getRecommendationsRequest.execute().getTracks()));


        }catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("m=getRecommendations, msg=Error: " + e.getMessage());
        }




        return recommendationTracks;
    }

    public static List<SavedTrack> getUsersSavedTracks_Sync(String token) {
        try {
            createApi(token);
            getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .limit(50)
                    .offset(0)
//                    .market(CountryCode.BR)
                    .build();

            return Arrays.asList(getUsersSavedTracksRequest.execute().getItems());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    public static List<String> getGenres(ArtistSimplified[] artistsId) {
        List<String> genres = new ArrayList<>();

        Arrays.asList(artistsId).forEach(artist -> {

            getArtistRequest = spotifyApi
                    .getArtist(artist.getId())
                    .build();
            try {
                genres.addAll(Arrays.asList(getArtistRequest.execute().getGenres()));
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                System.out.println("Error: " + e.getMessage());
            }
        });

        return genres;
    }

    public static void getUsersSavedTracks_Async(String token, String market) {
        try {

            createApi(token);
            getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .offset(0)
                    .market(CountryCode.getByCode(market))
                    .build();


            final CompletableFuture<Paging<SavedTrack>> pagingFuture = getUsersSavedTracksRequest.executeAsync();
            final Paging<SavedTrack> savedTrackPaging = pagingFuture.join();

            System.out.println("Total: " + savedTrackPaging.getTotal());
        } catch (CompletionException e) {
            System.out.println("Error: " + e.getCause().getMessage());
        } catch (CancellationException e) {
            System.out.println("Async operation cancelled.");
        }
    }
}

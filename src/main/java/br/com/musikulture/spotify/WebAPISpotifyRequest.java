package br.com.musikulture.spotify;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.requests.data.artists.GetArtistRequest;
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
        spotifyApi = new SpotifyApi.Builder().setAccessToken(accessToken).build();
    }

    private static GetUsersSavedTracksRequest getUsersSavedTracksRequest = null;

    private static GetArtistRequest getArtistRequest = null;

    public static List<SavedTrack> getUsersSavedTracks_Sync(String token) {
        try {
            createApi(token);
            getUsersSavedTracksRequest = spotifyApi.getUsersSavedTracks()
                    .limit(20)
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
                    .limit(10)
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

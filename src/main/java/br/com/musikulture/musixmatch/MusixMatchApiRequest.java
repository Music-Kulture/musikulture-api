package br.com.musikulture.musixmatch;

import br.com.musikulture.entity.Artist;
import br.com.musikulture.entity.Genre;
import br.com.musikulture.entity.MusicLanguage;
import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.repository.ArtistRepository;
import br.com.musikulture.repository.GenreRepository;
import br.com.musikulture.repository.MusicLanguageRepository;
import br.com.musikulture.spotify.WebAPISpotifyRequest;
import com.ctc.wstx.evt.WEntityDeclaration;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class MusixMatchApiRequest {

    @Autowired
    private MusicLanguageRepository musicLanguageRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;


    public String apiKey = System.getenv("MUSIX_MATCH_API_KEY");

    public List<TrackAnalyzed> analyzeSavedTracks(List<TrackSimplified> tracks,
                                                  String lang, WebAPISpotifyRequest webAPISpotifyRequest) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        for (TrackSimplified trackSimplified : tracks) {
            String trackName;
            String artistName;
            String artistId;
            Integer trackId = 0;
            String language = "";
            Map<String, String> allArtists = new HashMap<>();
            List<String> genres = new ArrayList<>();

            trackName = trackSimplified.getName();
            artistName = trackSimplified.getArtists()[0].getName();
            artistId = trackSimplified.getArtists()[0].getId();

            if (musicLanguageRepository.existsBySpotifyId(trackSimplified.getId())) {

                MusicLanguage musicLanguage = musicLanguageRepository.findBySpotifyId(trackSimplified.getId()).get();
                ArtistSimplified[] artists = trackSimplified.getArtists();
                for (int i = 0; i < artists.length; i++) {
                    ArtistSimplified artist = artists[i];
                    genres.add(genreRepository.findByArtistsAndOffset(artist.getId(), i));
                }

                trackId = musicLanguage.getMusixId();
                language = musicLanguage.getLanguage();

                if (language.equalsIgnoreCase(lang)) {
                    trackAnalyzeds.add(new TrackAnalyzed(trackSimplified, language, trackId, webAPISpotifyRequest));
                    System.out.println("IT MATCHES THE LANGUAGE");
                }
            } else {


                Track track;
                MusicLanguage musicLanguage;

                genres = webAPISpotifyRequest.getGenres(trackSimplified.getArtists());
                track = null;
                try {
                    track = musixMatch.getMatchingTrack(trackName, artistName);

                    TrackData data = track.getTrack();


                    trackId = data.getTrackId();

                    language = musixMatch.getSnippet(trackId).getSnippetLanguage();

                    if (language.length() > 3) continue;

                    System.out.println(language);

                    List<Genre> genreSet = new ArrayList<>();

                    genres.forEach(s -> {
                        Genre genre = new Genre(s);
                        if (!genreRepository.existsByName(s)) {
                            genreRepository.save(genre);
                            genreRepository.flush();
                        }
                        genreSet.add(genreRepository.findByName(s).get());
                    });

                    Set<Artist> artistSet = new HashSet<>();
                    for (ArtistSimplified artist : trackSimplified.getArtists()) {
                        if (!artistRepository.existsById(artist.getId())) {
                            Artist newArtist = new Artist(artist.getId(), artist.getName());
                            newArtist.setGenres(genreSet);
                            artistSet.add(newArtist);
                        }
                        allArtists.put(artist.getName(), artist.getId());
                    }
//                    artistSet.forEach(artist -> {
//                        artistRepository.saveAndFlush(artist);
//                    });
                    musicLanguage = new MusicLanguage(
                            trackName,
                            trackId,
                            trackSimplified.getId(),
                            language);
                    musicLanguage.setArtists(artistSet);
                    if (!musicLanguageRepository.existsBySpotifyId(trackSimplified.getId()))
                        musicLanguageRepository.saveAndFlush(musicLanguage);

                    System.out.print(".");


                    if (language.equalsIgnoreCase(lang)) {
                        trackAnalyzeds.add(new TrackAnalyzed(trackSimplified, language, trackId, webAPISpotifyRequest));
                        System.out.println("IT MATCHES THE LANGUAGE");
                    }


                } catch (MusixMatchException | RuntimeException e) {
                    System.out.println("m=analyzeSavedTracks msg= Error: " + e.getMessage() + ", no music");
                }
            }
        }
        return trackAnalyzeds;
    }


    public List<TrackAnalyzed> analyze(List<SavedTrack> tracks, WebAPISpotifyRequest webAPISpotifyRequest) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        tracks.forEach(savedTrack -> {
            String trackName;
            String artistName;
            String artistId;
            Integer trackId = 0;
            String language = "";
            Map<String, String> allArtists = new HashMap<>();
            List<String> genres = new ArrayList<>();

            trackName = savedTrack.getTrack().getName();
            artistName = savedTrack.getTrack().getArtists()[0].getName();
            artistId = savedTrack.getTrack().getArtists()[0].getId();
            if (musicLanguageRepository.existsBySpotifyId(savedTrack.getTrack().getId())) {
                MusicLanguage musicLanguage = musicLanguageRepository.findBySpotifyId(savedTrack.getTrack().getId()).get();
                ArtistSimplified[] artists = savedTrack.getTrack().getArtists();
                for (int i = 0; i < artists.length; i++) {
                    ArtistSimplified artist = artists[i];
                    genres.add(genreRepository.findByArtistsAndOffset(artist.getId(), i));
                }

                trackId = musicLanguage.getMusixId();
                language = musicLanguage.getLanguage();

                musicLanguage.getArtists().forEach(artist -> {
                    allArtists.put(artist.getId(), artist.getName());
                });


            } else {
                MusicLanguage musicLanguage;

                genres = webAPISpotifyRequest.getGenres(savedTrack.getTrack().getArtists());
                Track track = null;
                try {
                    track = musixMatch.getMatchingTrack(trackName, artistName);

                    TrackData data = track.getTrack();


                    trackId = data.getTrackId();

                    language = musixMatch.getSnippet(trackId).getSnippetLanguage();

                    System.out.println(language);

                    List<Genre> genreSet = new ArrayList<>();

                    genres.forEach(s -> {
                        Genre genre = new Genre(s);
                        if (!genreRepository.existsByName(s)) {
                            genreRepository.save(genre);
                            genreRepository.flush();
                        }
                        genreSet.add(genreRepository.findByName(s).get());
                    });

                    Set<Artist> artistSet = new HashSet<>();
                    for (ArtistSimplified artist : savedTrack.getTrack().getArtists()) {
                        if (!artistRepository.existsById(artist.getId())) {
                            Artist newArtist = new Artist(artist.getId(), artist.getName());
                            newArtist.setGenres(genreSet);
                            artistSet.add(newArtist);
                        }
                        allArtists.put(artist.getName(), artist.getId());
                    }
                    musicLanguage = new MusicLanguage(
                            trackName,
                            trackId,
                            savedTrack.getTrack().getId(),
                            language);
                    musicLanguage.setArtists(artistSet);
                    musicLanguageRepository.saveAndFlush(musicLanguage);


                } catch (MusixMatchException e) {
                    e.printStackTrace();
                    System.out.println("No Music found!");
                }
            }

            trackAnalyzeds.add(new TrackAnalyzed(
                    savedTrack.getTrack().getId(),
                    (Integer.toString(trackId)),
                    trackName,
                    artistName,
                    artistId,
                    allArtists,
                    language,
                    genres)
            );
        });

        return trackAnalyzeds;
    }

    public MusixMatchApiRequest() {
    }
}

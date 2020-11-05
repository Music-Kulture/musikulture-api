package br.com.musikulture.musixmatch;

import br.com.musikulture.entity.Artist;
import br.com.musikulture.entity.Genre;
import br.com.musikulture.entity.MusicLanguage;
import br.com.musikulture.music.TrackAnalyzed;
import br.com.musikulture.repository.ArtistRepository;
import br.com.musikulture.repository.GenreRepository;
import br.com.musikulture.repository.MusicLanguageRepository;
import br.com.musikulture.spotify.WebAPISpotifyRequest;
import com.wrapper.spotify.model_objects.specification.ArtistSimplified;
import com.wrapper.spotify.model_objects.specification.SavedTrack;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger LOGGER = LoggerFactory.getLogger(MusixMatchApiRequest.class);

    @Autowired
    private MusicLanguageRepository musicLanguageRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private GenreRepository genreRepository;

    String trackName;
    String artistName;
    String artistId;
    Integer trackId = 0;
    String language = "";
    Map<String, String> allArtists = new HashMap<>();
    List<String> genres = new ArrayList<>();


    public String apiKey = System.getenv("MUSIX_MATCH_API_KEY");

    @SuppressWarnings("DuplicatedCode")
    public List<TrackAnalyzed> analyzeSavedTracks(List<TrackSimplified> tracks,
                                                  String lang, WebAPISpotifyRequest webAPISpotifyRequest) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        for (TrackSimplified trackSimplified : tracks) {
            allArtists = new HashMap<>();
            genres = new ArrayList<>();

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

                LOGGER.info("m=analyze, msg=Fluxo do Banco de Dados");

            } else {


                Track track;
                MusicLanguage musicLanguage;

                genres = webAPISpotifyRequest.getGenres(trackSimplified.getArtists());
                try {
                    track = musixMatch.getMatchingTrack(trackName, artistName);

                    TrackData data = track.getTrack();


                    trackId = data.getTrackId();

                    language = musixMatch.getSnippet(trackId).getSnippetLanguage();

                    if (language.length() > 3) continue;


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

                    musicLanguage = new MusicLanguage(
                            trackName,
                            trackId,
                            trackSimplified.getId(),
                            language);
                    musicLanguage.setArtists(artistSet);
                    if (!musicLanguageRepository.existsBySpotifyId(trackSimplified.getId()))
                        musicLanguageRepository.saveAndFlush(musicLanguage);
                    LOGGER.info("m=analyze, msg=Fluxo da API");
                } catch (MusixMatchException | RuntimeException e) {
                    LOGGER.error("m=analyzeSavedTracks msg= Error: {}",e.getMessage());
                }
            }
            if (language.equalsIgnoreCase(lang)) {
                trackAnalyzeds.add(new TrackAnalyzed(trackSimplified, language, trackId, webAPISpotifyRequest));
                LOGGER.info("m=analyzeSavedTracks, msg=Found a match for track \"{}\"", trackName);
            }
        }
        return trackAnalyzeds;
    }


    public List<TrackAnalyzed> analyze(List<SavedTrack> tracks, WebAPISpotifyRequest webAPISpotifyRequest) {

        List<TrackAnalyzed> trackAnalyzeds = new ArrayList<>();

        MusixMatch musixMatch = new MusixMatch(apiKey);

        tracks.forEach(savedTrack -> {
            allArtists = new HashMap<>();
            genres = new ArrayList<>();

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

                LOGGER.info("m=analyze, msg=Fluxo do Banco de Dados");

            } else {
                MusicLanguage musicLanguage;

                genres = webAPISpotifyRequest.getGenres(savedTrack.getTrack().getArtists());
                Track track = null;
                try {
                    track = musixMatch.getMatchingTrack(trackName, artistName);

                    TrackData data = track.getTrack();


                    trackId = data.getTrackId();

                    language = musixMatch.getSnippet(trackId).getSnippetLanguage();

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


                    LOGGER.info("m=analyze, msg=Fluxo da API");

                } catch (MusixMatchException e) {
                    LOGGER.error("m=analyze, msg=No Music found!, error={}",e.getMessage());
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

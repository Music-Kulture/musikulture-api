package br.com.musikulture.repository;

import br.com.musikulture.entity.MusicLanguage;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicLanguageRepository extends JpaRepository<MusicLanguage, Long> {

    @NotNull
    List<MusicLanguage> findAll();

    @NotNull
    Optional<MusicLanguage> findById(@NotNull Long aLong);

    Optional<MusicLanguage> findByLanguage(String language);

    Optional<MusicLanguage> findBySpotifyId(String spotifyId);

    Optional<MusicLanguage> findByMusixId(Integer musixId);

    boolean existsBySpotifyId(String spotifyId);

    @Query(value = "select artist.name from music, music_artists, artist where music_artists.artist_id = artist.id AND music_artists.music_id = music.id AND music.spotify_id = \':id\'", nativeQuery = true)
    String findArtistsBySpotifyId(String id);
}

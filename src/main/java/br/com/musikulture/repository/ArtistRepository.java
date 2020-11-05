package br.com.musikulture.repository;

import br.com.musikulture.entity.Artist;
import br.com.musikulture.entity.Genre;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

    @NotNull
    List<Artist> findAll();

    @NotNull
    Optional<Artist> findById(@NotNull String aString);

    Optional<Artist> findByName(String name);

    @Query(value = "select artist.*\n" +
            "from genre,\n" +
            "     genre_artists,\n" +
            "     artist\n" +
            "where genre_artists.artist_id = artist.id\n" +
            "  AND genre_artists.genre_id = genre.id\n" +
            "  AND genre.name IN (:genres)", nativeQuery = true)
    Optional<Artist> findByGenres(Set<Genre> genres);
}


package br.com.musikulture.repository;

import br.com.musikulture.entity.Genre;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    @NotNull
    List<Genre> findAll();

    @NotNull
    Optional<Genre> findById(@NotNull Long aLong);

    Optional<Genre> findByName(String name);

    @Query(value = "select genre.name\n" +
            "from genre,\n" +
            "     genre_artists,\n" +
            "     artist\n" +
            "where genre_artists.artist_id = artist.id\n" +
            "  AND genre_artists.genre_id = genre.id\n" +
            "  AND artist.id = :artist\n" +
            "OFFSET :offset\n" +
            "LIMIT 1", nativeQuery = true)
    String findByArtistsAndOffset(String artist, Integer offset);

    boolean existsByName(String name);
}


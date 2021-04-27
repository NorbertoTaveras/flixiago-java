package com.norbertotaveras.flixiago.models.base;

import java.util.ArrayList;
import java.util.HashMap;

public class GenreList {
    private ArrayList<Genre> genres;

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public String genreNameFromId(long id) {
        for (Genre genre: genres) {
            if (genre.getId() == id)
                return genre.getName();
        }
        return "???";
    }

    public HashMap<Long, String> genreNameLookupTable() {
        HashMap<Long, String> lookupTable = new HashMap<Long, String>(genres.size());
        for (Genre genre: genres) {
            lookupTable.put(genre.getId(), genre.getName());
        }
        return lookupTable;
    }
}

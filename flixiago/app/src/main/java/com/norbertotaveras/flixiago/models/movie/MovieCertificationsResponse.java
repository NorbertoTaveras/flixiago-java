package com.norbertotaveras.flixiago.models.movie;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieCertificationsResponse {
    private HashMap<String, ArrayList<MovieCertification>> certifications;

    public HashMap<String, ArrayList<MovieCertification>> getCertifications() {
        return certifications;
    }

    public ArrayList<MovieCertification> getCertificationForCountry(String countryCode) {
        return certifications.get(countryCode);
    }
}

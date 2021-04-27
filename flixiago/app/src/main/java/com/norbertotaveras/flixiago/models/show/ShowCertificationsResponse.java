package com.norbertotaveras.flixiago.models.show;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowCertificationsResponse {
    private HashMap<String, ArrayList<ShowCertification>> certifications;

    public HashMap<String, ArrayList<ShowCertification>> getCertifications() {
        return certifications;
    }

    public ArrayList<ShowCertification> getCertificationForCountry(String countryCode) {
        return certifications.get(countryCode);
    }
}

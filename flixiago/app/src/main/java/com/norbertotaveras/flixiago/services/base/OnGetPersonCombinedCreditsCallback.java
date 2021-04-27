package com.norbertotaveras.flixiago.services.base;

import com.norbertotaveras.flixiago.models.base.PersonCombinedCredits;

public interface OnGetPersonCombinedCreditsCallback {
    void onSuccess(PersonCombinedCredits combinedCredits);
    void onFailure(Throwable error);
}

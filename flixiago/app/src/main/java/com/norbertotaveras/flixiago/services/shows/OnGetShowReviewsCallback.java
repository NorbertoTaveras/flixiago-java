package com.norbertotaveras.flixiago.services.shows;

import com.norbertotaveras.flixiago.models.show.ShowReview;

import java.util.ArrayList;

public interface OnGetShowReviewsCallback {
    void onSuccess(ArrayList<ShowReview> showReviews);
    void onFailure(Throwable error);
}

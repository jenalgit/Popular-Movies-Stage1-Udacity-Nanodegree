package com.example.blancomm.popularmoviesstage1.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.blancomm.popularmoviesstage1.R;
import com.example.blancomm.popularmoviesstage1.VolleyListeners;
import com.example.blancomm.popularmoviesstage1.model.MovieDetailInfo;
import com.example.blancomm.popularmoviesstage1.network.VolleyRequest;
import com.example.blancomm.popularmoviesstage1.utils.Constant;
import com.example.blancomm.popularmoviesstage1.utils.JSONActions;
import com.example.blancomm.popularmoviesstage1.utils.UtilsView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements VolleyListeners {

    private String mIdMovie;
    private NetworkImageView mImageDetail, mThumbnail;
    private TextView mTextDetail, mTextTitle;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout mAppBar;
    private String TAG = DetailFragment.class.getSimpleName();
    private String mTitle;
    private CardView mCardTitle, mCardHeader;
    private int mInitialTitle = 0;

    public DetailFragment() {
    }

    public static DetailFragment newInstance(String id) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(Constant.TAG_ID_MOVIE, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mIdMovie = getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String urlDetail = Constant.URL_DETAIL_MOVIE + mIdMovie + "?" + Constant.API_KEY;

        VolleyRequest.requestJson(this, urlDetail);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        instantiateObjects(rootView);


        return rootView;
    }

    private void instantiateObjects(View view) {

        mImageDetail = (NetworkImageView) view.findViewById(R.id.image_detail);
        mThumbnail = (NetworkImageView)view.findViewById(R.id.thumbnail_film2);
        mTextDetail = (TextView) view.findViewById(R.id.text_detail);
        mAppBar = (AppBarLayout) view.findViewById(R.id.view);
        mCardTitle = (CardView)view.findViewById(R.id.card_title);
        mCardHeader = (CardView)view.findViewById(R.id.card_header);
        mTextTitle = (TextView)view.findViewById(R.id.title_detail);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((DetailActivity) getActivity()).setSupportActionBar(toolbar);
        ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        UtilsView.makeCollapsingToolbarLayoutTypeface(collapsingToolbar, getActivity());

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {

                Log.e(TAG,"Valor de i: " + i);
                if (i >= -418 && i < 0)
                {
                   // mThumbnail.animate().translationY(0 - mThumbnail.getHeight());
                    collapsingToolbar.setTitle(mTitle);
                    collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
                    collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
                    mInitialTitle++;


                }else if (i == 0){

                    if (mInitialTitle == 0) {
                        mCardTitle.setVisibility(View.VISIBLE);
                        mTextTitle.setText(mTitle);
                        mTextTitle.setVisibility(View.VISIBLE);
                        mCardHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    } else {

                        mCardTitle.setVisibility(View.GONE);
                        mCardHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mThumbnail.getHeight() - mThumbnail.getHeight()/7));
                    }
                }
                else {

                    mInitialTitle++;
                    mThumbnail.animate().translationY(mThumbnail.getHeight() / 3);
                    mCardHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mThumbnail.getHeight() + 10));
                    mCardTitle.setVisibility(View.GONE);

                }
            }
        });
    }

    @Override
    public void onFinishJsonRequest(JSONObject jsonObject) {

        MovieDetailInfo movieDetail;

        try {
            movieDetail = JSONActions.parseDetail(jsonObject);
            injectData(movieDetail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void injectData(MovieDetailInfo movieDetailInfo) {

        String urlImage = Constant.URL_DETAIL_IMAGE + movieDetailInfo.getImageDetail();
        VolleyRequest.requestImage(Constant.URL_DETAIL_IMAGE + movieDetailInfo.getImageDetail(), mImageDetail);
        VolleyRequest.requestImage(Constant.URL_THUMNAIL_IMAGE + getActivity().getString(R.string.width_image_thumb) + movieDetailInfo.getThumnail(), mThumbnail);
        mTextDetail.setText(movieDetailInfo.getDescription());
        mTitle = movieDetailInfo.getTitle();
        collapsingToolbar.setTitle("");
        //collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        //collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);


    }

}

package com.example.ashutosh.music_player;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ashutosh.music_player.ITunes.Adapter.CustomAdapter;
import com.example.ashutosh.music_player.ITunes.Model.Pojo;
import com.example.ashutosh.music_player.SoundCloud.Config;
import com.example.ashutosh.music_player.SoundCloud.SCService2;
import com.example.ashutosh.music_player.SoundCloud.SCService3;
import com.example.ashutosh.music_player.SoundCloud.SoundCloud;
import com.example.ashutosh.music_player.SoundCloud.Track;
import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
{
    private List<Track> mListItems ;
    private CustomAdapter mAdapter ;
    private ImageView iv ;
    private TextView text ;
    private AnimatedVectorDrawable searchToBar ;
    private AnimatedVectorDrawable barToSearch ;
    private Interpolator interp ;
    private TextView mSelectedTrackTitle ;
    private ImageView mSelectedTrackImage ;
    private MediaPlayer mMediaPlayer ;
    private ImageView mPlayerControl ;
    private ImageView mforward ;
    public SCService3 scService3 ;
    private Toolbar tb ;
    private int duration ;
    private float offset ;
    private boolean expanded = true;
    String s ;
    private ArrayList<String> al ;
    private ArrayList<String> artists ;
    private ArrayList<String> genres ;
    private ListView listView ;
    private ArrayList<Pojo> pojoList = new ArrayList<Pojo>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        View vtb = findViewById(R.id.viewT4) ;
        tb = (Toolbar) vtb.findViewById(R.id.bar_player) ;
        tb.setVisibility(View.GONE);

        mSelectedTrackTitle = (TextView) vtb.findViewById(R.id.selected_track_title) ;
        mSelectedTrackImage = (ImageView) vtb.findViewById(R.id.selected_track_image) ;
        mPlayerControl = (ImageView) vtb.findViewById(R.id.player_control) ;
        mforward = (ImageView) vtb.findViewById(R.id.forward) ;

        mPlayerControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                togglePlayPause();
            }


        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build() ;

        scService3 = SoundCloud.getService3() ;

        mMediaPlayer = new MediaPlayer() ;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlayPause() ;
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.ic_play);
            }
        });

        mforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition() + 30000);
            }
        });



        listView = (ListView) findViewById(R.id.track_list_view) ;
        iv = (ImageView) findViewById(R.id.search) ;
        text = (TextView) findViewById(R.id.text1) ;
        Button btn = (Button) findViewById(R.id.btn) ;
        searchToBar = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_search_to_bar) ;
        barToSearch = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.anim_bar_to_search) ;
        interp = AnimationUtils.loadInterpolator(this, android.R.interpolator.linear_out_slow_in) ;
        duration = 2000 ;
        offset = -71f * (int) getResources().getDisplayMetrics().scaledDensity ;
        iv.setTranslationX(offset);
        text.setAlpha(1f);

        al = new ArrayList<String>() ;
        artists = new ArrayList<String>() ;
        genres = new ArrayList<String>() ;
        mListItems = new ArrayList<Track>() ;
     //   final ListView listView = (ListView) findViewById(R.id.track_list_view) ;
        mAdapter = new CustomAdapter(this,R.layout.item, pojoList) ;
     //   listView.setAdapter(mAdapter);


        final SCService2 scService = SoundCloud.getService2();

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER :
                        case KeyEvent.KEYCODE_ENTER:
                             s = text.getText().toString() ;
                             return true ;
                        default:
                            break;
                    }
                }
                return false ;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     try
                {
                    FileInputStream fin = openFileInput("userdata") ;
                    int c ;
                    String temp = "" ;
                    while( (c = fin.read()) != -1)
                    {
                        temp = temp + Character.toString((char) c) ;
                    }
                    System.out.println(temp);
                    fin.close() ;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }*/
                s = text.getText().toString() ;
                pojoList.clear();
                mAdapter.notifyDataSetChanged();
                getData(s);
            }
        });

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                   Pojo item = pojoList.get(position) ;
                String t = item.getArtistName() ;
                t = t.replace("," , "");
                t = t.replace(" " , "+");
                if(al.contains(t))
                {
                    artists.remove(t) ;
                    al.remove(t) ;
                    TastyToast.makeText(getApplicationContext(), "Song Removed !", TastyToast.LENGTH_SHORT, TastyToast.ERROR) ;
                }
                else
                {
                    if(!artists.contains(t))
                        artists.add(t) ;
                    al.add(t) ;
                    TastyToast.makeText(getApplicationContext(), "Song Added !", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS) ;
                }
                if(al.size() == 5)
                {
                    Intent intent = new Intent(SearchActivity.this, Recommended.class);
                    intent.putStringArrayListExtra("artist", artists) ;
                    startActivity(intent);
                }

            }
        }); */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                final String s = pojoList.get(position).getTrackName() ;
                final String artist = pojoList.get(position).getArtistName() ;
                AccessToken accessToken = AccessToken.getCurrentAccessToken() ;
                final String email =  accessToken.getUserId() ;

                Response.Listener<String> responseListener = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response) ;
                            boolean success = jsonObject.getBoolean("success") ;
                            if(success)
                            {
                                System.out.println("Successful");
                            }
                            else
                            {
                                System.out.println("Not Successful");
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                } ;

                SongPushRequest songPushRequest = new SongPushRequest(artist, email, responseListener) ;
                RequestQueue queue = Volley.newRequestQueue(SearchActivity.this) ;
                queue.add(songPushRequest) ;

            /*    try
                {
                    FileOutputStream fos = openFileOutput("userdata", MODE_APPEND) ;
                    String t = pojoList.get(position).getArtistName() + " " ;
                    fos.write(t.getBytes());
                    fos.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                } */
                scService3.getRecentTracks(s).enqueue(new Callback<List<Track>>() {
                    @Override
                    public void onResponse(Call<List<Track>> call, retrofit2.Response<List<Track>> response) {
                        if(response.isSuccessful())
                        {
                            List<Track> tracks = response.body() ;
                            Track track = tracks.get(0) ;
                            mSelectedTrackTitle.setText(s);
                            Picasso.with(SearchActivity.this).load(pojoList.get(position).getImageView()).into(mSelectedTrackImage);

                            if(mMediaPlayer.isPlaying())
                            {
                                mMediaPlayer.stop();
                                mMediaPlayer.reset();
                            }

                            try
                            {
                                tb.setVisibility(View.VISIBLE);
                                mMediaPlayer.setDataSource(track.getStreamURL() + "?client_id=" + Config.CLIENT_ID);
                                mMediaPlayer.prepareAsync();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            showMessage("Error code " + response.code()) ;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Track>> call, Throwable t) {

                    }
                }) ;
            }
        }) ;


        iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void togglePlayPause()
    {
        if(mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
            mPlayerControl.setImageResource(R.drawable.ic_play);
        }
        else
        {
            mMediaPlayer.start();
            mPlayerControl.setImageResource(R.drawable.ic_pause);
            mforward.setImageResource(R.drawable.forward3);
        }
    }

    private void getData(String s) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://itunes.apple.com/search?term=" + s.replace(" ", "+");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    for(int i = 0; i < jsonArray.length(); i++){
                        Pojo pojo = new Pojo();
                        JSONObject object = jsonArray.getJSONObject(i);
                        pojo.setArtistName(object.optString("artistName","Unknown"));
                        pojo.setTrackName(object.optString("trackName","Unknown"));
                        pojo.setCollectionName(object.optString("collectionName","Unknown"));
                        pojo.setImageView(object.optString("artworkUrl100","Unknown"));
                        pojoList.add(pojo);
                        fillList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new com.android.volley.Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    public void fillList()
    {
        listView.setAdapter(mAdapter);
    }


    public void animate(View view)
    {
        if(!expanded)
        {
            iv.setImageDrawable(searchToBar);
            searchToBar.start();
            iv.animate().translationX(0f).setDuration(duration).setInterpolator(interp) ;
            text.animate().alpha(1f).setStartDelay(duration-100).setDuration(100).setInterpolator(interp) ;
        }
        else
        {
            iv.setImageDrawable(barToSearch);
            barToSearch.start();
            iv.animate().translationX(offset).setDuration(duration).setInterpolator(interp) ;
            text.setAlpha(0f);
        }
        expanded = !expanded ;
    }

    private void loadTracks(List<Track> tracks)
    {
        mListItems.clear();
        mListItems.addAll(tracks) ;
        mAdapter.notifyDataSetChanged();
    }
    private void showMessage(String message)
    {
        Toast.makeText(SearchActivity.this, message,Toast.LENGTH_LONG).show();
    }
}

package com.gtappdevelopers.wallpaperapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeScreenActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface, ColorRVAdapter.ColorClickInterface {
    //creating variables for recyclerview,progress bar, adapter and array list, edittext and others.
    private RecyclerView  categoryRV, colorRV, wallpaperRV;
    private ProgressBar loadingPB;
    private ArrayList<CategoryRVModal> categoryRVModals;
    private ArrayList<ColorRVModal> colorRVModals;
    private ArrayList<String> wallpaperArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private ColorRVAdapter colorRVAdapter;
    private WallpaperRVAdapter wallpaperRVAdapter;
    private EditText searchEdt;
    private ImageView searchIV;


    private long backPressedTime;
    private Toast backToast;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        //initializing all variables on below line.
        categoryRV = findViewById(R.id.idRVCategories);
        colorRV = findViewById(R.id.idRVColor);
        wallpaperRV = findViewById(R.id.idRVWallpapers);
        searchEdt = findViewById(R.id.idEdtSearch);
        searchIV = findViewById(R.id.idIVSearch);
        loadingPB = findViewById(R.id.idPBLoading);
        wallpaperArrayList = new ArrayList<>();
        categoryRVModals = new ArrayList<>();
        colorRVModals = new ArrayList<>();
        //creating a layout manager for recycler view which is our category.
        LinearLayoutManager manager1 = new LinearLayoutManager(HomeScreenActivity.this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager manager2 = new LinearLayoutManager(HomeScreenActivity.this, RecyclerView.HORIZONTAL, false);
        //initializing our adapter class on below line.
        wallpaperRVAdapter = new WallpaperRVAdapter(wallpaperArrayList, this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModals, this, this);
        colorRVAdapter = new ColorRVAdapter(colorRVModals, this, this);
        //setting layout manager to our category recycler view as horizontal.
        categoryRV.setLayoutManager(manager1);
        categoryRV.setAdapter(categoryRVAdapter);
        colorRV.setLayoutManager(manager2);
        colorRV.setAdapter(colorRVAdapter);
        //creating a grid layout manager for our wallpaper recycler view.
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        //setting layout manager and adapter to our recycler view.
        wallpaperRV.setLayoutManager(layoutManager);
        wallpaperRV.setAdapter(wallpaperRVAdapter);
        //on below line we are calling method to get categories to add data in array list.
        getCategories();
        getColor();
        //on below line we are calling get wallpaper method to get data in wallpaper array list.
        getWallpapers();
        //on below line we are adding on click listner for search image view on below line.
        searchIV.setOnClickListener(v -> {
            //inside on click method we are getting data from our search edittext and validating if the input field is empty or not.
            String searchStr = searchEdt.getText().toString();
            if (searchStr.isEmpty()) {
                Toast.makeText(HomeScreenActivity.this, "Please enter something to search", Toast.LENGTH_SHORT).show();
            } else {
                //on below line we are calling a get wallpaper method to get wallpapers by category.
                getWallpapersByCategory(searchStr);
            }
        });
    }

    //on below line we are creating a method to get the wallpaper by category.
    private void getWallpapersByCategory(String category) {
        //on below line we are clearing our array list.
        wallpaperArrayList.clear();
        //on below line we are making visibility of our progress bar as gone.
        loadingPB.setVisibility(View.VISIBLE);
        //on below line we are creating a string variable for our url and adding url to it.
        String url = "https://api.pexels.com/v1/search?query=" + category + "&per_page=80&page=1";
        //on below line we are creating a new variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreenActivity.this);
        //on below line we are making a json object request to get the data from url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //on below line we are extracting the data from our response and passing it to our array list.
                try {
                    loadingPB.setVisibility(View.GONE);
                    //on below line we are extracting json data.
                    JSONArray photos = response.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photoObj = photos.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        //on below line we are passing data to our array list
                        wallpaperArrayList.add(imgUrl);
                    }
                    //here we are notifying adapter that data has changed in our list.
                    wallpaperRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    //handling json exception on below line.
                    e.printStackTrace();
                }
            }
        }, error -> {
            //displaying a simple toast message on error response.
            Toast.makeText(HomeScreenActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                //in this method passing headers as key along with value as API keys.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f91700001000001c306e836823749b4a9aa8269b30adecf");
                //at last returning headers.
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }

    private void getWallpapersByColor(String color) {
        //on below line we are clearing our array list.
        wallpaperArrayList.clear();
        //on below line we are making visibility of our progress bar as gone.
        loadingPB.setVisibility(View.VISIBLE);
        //on below line we are creating a string variable for our url and adding url to it.
        String url = "https://api.pexels.com/v1/search?query=" + color + "&per_page=80&page=1";
        //on below line we are creating a new variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreenActivity.this);
        //on below line we are making a json object request to get the data from url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //on below line we are extracting the data from our response and passing it to our array list.
                try {
                    loadingPB.setVisibility(View.GONE);
                    //on below line we are extracting json data.
                    JSONArray photos = response.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photoObj = photos.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        //on below line we are passing data to our array list
                        wallpaperArrayList.add(imgUrl);
                    }
                    //here we are notifying adapter that data has changed in our list.
                    wallpaperRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    //handling json exception on below line.
                    e.printStackTrace();
                }
            }
        }, error -> {
            //displaying a simple toast message on error response.
            Toast.makeText(HomeScreenActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                //in this method passing headers as key along with value as API keys.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f91700001000001c306e836823749b4a9aa8269b30adecf");
                //at last returning headers.
                return headers;
            }
        };
        queue.add(jsonObjectRequest);

    }


    private void getWallpapers() {
        //on below line we are clearing our array list.
        wallpaperArrayList.clear();
        //changing visiblity of our progress bar to gone.
        loadingPB.setVisibility(View.VISIBLE);
        //creating a variable for our url.
        String url = "https://api.pexels.com/v1/curated?per_page=80&page=1";
        //on below line we are creating a new variable for our request queue.
        RequestQueue queue = Volley.newRequestQueue(HomeScreenActivity.this);
        //on below line we are making a json object request to get the data from url .
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //on below line we are extracting the data from our response and passing it to our array list.
                loadingPB.setVisibility(View.GONE);
                try {
                    //on below line we are extracting json data.
                    JSONArray photos = response.getJSONArray("photos");
                    for (int i = 0; i < photos.length(); i++) {
                        JSONObject photoObj = photos.getJSONObject(i);
                        String imgUrl = photoObj.getJSONObject("src").getString("portrait");
                        //on below line we are passing data to our array list
                        wallpaperArrayList.add(imgUrl);
                    }
                    wallpaperRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    //handling json exception on below line.
                    e.printStackTrace();
                }
            }
        }, error -> {
            //displaying a toast message on error respone.
            Toast.makeText(HomeScreenActivity.this, "Fail to get data..", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() {
                //in this method passing headers as key along with value as API keys.
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "563492ad6f91700001000001c306e836823749b4a9aa8269b30adecf");
                //at last returning headers.
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void getCategories() {
        //on below lines we are adding data to our category array list.

        categoryRVModals.add(new CategoryRVModal("Abstract", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSjlPBV9cgukwIJh0smRqUOG6fUZDQg_6MGDBMmDar4Ba0-K6LDGmbQaS390atYwOTSUSA&usqp=CAU"));
        categoryRVModals.add(new CategoryRVModal("City", "https://img.freepik.com/free-vector/new-york-cityscape-illustration-cartoon-new-york-landmarks-night-freedom-tower_33099-294.jpg"));
        categoryRVModals.add(new CategoryRVModal("Art", "https://rhythmartgallery.com/wp-content/uploads/2021/10/Jagannath-Paul-Artist-Rhythm-Art-Gallery-Mumbai.jpg"));
        categoryRVModals.add(new CategoryRVModal("Fauna", "https://d2gg9evh47fn9z.cloudfront.net/1600px_COLOURBOX5182947.jpg"));
        categoryRVModals.add(new CategoryRVModal("Floral", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQC0IkcEBSrZ6f-SP0o4JAcBrBMMKevfrsJCTSlizrsRhQvBd2dlv7RPQD4xuT330dyrAQ&usqp=CAU"));
        categoryRVModals.add(new CategoryRVModal("God", "https://wallpaperaccess.com/full/706842.jpg"));
        categoryRVModals.add(new CategoryRVModal("Minimal", "https://wallpaperaccess.com/full/1784334.png"));
        categoryRVModals.add(new CategoryRVModal("Nature", "https://wallpapercave.com/wp/wp8583519.jpg"));
        categoryRVModals.add(new CategoryRVModal("Space", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXj1T6cH1meZXNWNA9fEFs4XAHOF0oN-g_xw&usqp=CAU"));
        categoryRVModals.add(new CategoryRVModal("Robot", "https://p.turbosquid.com/ts-thumb/pM/0A6JXa/6A/01/jpg/1626897546/600x600/fit_q87/2247c6ac5d0bc0d04327388304eca8012cb028fd/01.jpg"));
    }

    private void getColor(){
        colorRVModals.add(new ColorRVModal("Black", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/49/A_black_image.jpg/1200px-A_black_image.jpg"));
        colorRVModals.add(new ColorRVModal("Red", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/10/Red_Color.jpg/1536px-Red_Color.jpg"));
        colorRVModals.add(new ColorRVModal("Blue", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Solid_blue.svg/225px-Solid_blue.svg.png"));
        colorRVModals.add(new ColorRVModal("Green", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/29/Solid_green.svg/1024px-Solid_green.svg.png"));
        colorRVModals.add(new ColorRVModal("Silver", "https://cdn.crispedge.com/8c8d91.png"));
        colorRVModals.add(new ColorRVModal("Violet", "https://htmlcolorcodes.com/assets/images/colors/violet-color-solid-background-1920x1080.png"));
        colorRVModals.add(new ColorRVModal("Gold", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSaM7aGxG5Oyxx5DWCn-Zqm5VB5WWF27aIIcg&usqp=CAU"));
    }


    @Override
    public void onCategoryClick(int position) {
        //on below line we are getting category from our array list and calling a method to get wallpapers by category.
        String category = categoryRVModals.get(position).getCategory();
        getWallpapersByCategory(category);
    }

    public void onColorClick(int position) {
        //on below line we are getting category from our array list and calling a method to get wallpapers by category.
        String color = colorRVModals.get(position).getColor();
        getWallpapersByColor(color);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
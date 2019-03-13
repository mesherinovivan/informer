package ru.rus.iamescherinov.informer;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;



public class MainActivity extends Activity {
    ListView listView;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        font = Typeface.createFromAsset(getAssets(), "font/annabelle.ttf");
        listView = (ListView) findViewById(R.id.listview);
        TextView emptyTxt = (TextView)findViewById(R.id.empty_list_item);
        emptyTxt.setTypeface(font);
        emptyTxt.setTextColor(0xFF030303);
        listView.setEmptyView(findViewById(R.id.empty_list_item));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                final String url = "http://api.forismatic.com/api/1.0/";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    String str = new String(obj.getString("quoteText").getBytes("ISO-8859-1"), "UTF-8");
                                    String autor = new String(obj.getString("quoteAuthor").getBytes("ISO-8859-1"), "UTF-8");

                                    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                                    HashMap<String, String> map;
                                    map = new HashMap<>();
                                    map.put("Text", str);
                                    map.put("Autor", autor);
                                    arrayList.add(map);
                                    SimpleAdapter adapter;
                                    adapter = new SimpleAdapter(MainActivity.this,
                                            arrayList, R.layout.list_quote, new String[]{"Text", "Autor"},
                                            new int[]{R.id.text_quote, R.id.autor_quote}){
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent){
                                            View view = super.getView(position, convertView, parent);
                                            TextView item = (TextView) view.findViewById(R.id.text_quote);

                                            item.setTypeface(font);
                                            item.setRight(50);
                                            item.setLeft(50);
                                            return view;
                                        }
                                    };

                                    listView.setAdapter(adapter);

                                } catch (JSONException e) {
                                    Toast toast = makeText(MainActivity.this,"Уппс, попробуйте позднее",Toast.LENGTH_SHORT);
                                    toast.show();

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", error.getMessage());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("method", "getQuote");
                        params.put("key", "457653");
                        params.put("format", "json");
                        params.put("lang", "ru");

                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/x-www-form-urlencoded");
                        params.put("Content-Encoding","charset=utf-8");
                        return params;
                    }

                };
                queue.add(postRequest);
            }
        });
    }

}

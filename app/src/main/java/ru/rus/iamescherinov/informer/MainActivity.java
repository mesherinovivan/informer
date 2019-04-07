package ru.rus.iamescherinov.informer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
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



public class MainActivity extends AppCompatActivity {
    ListView listView;
    Typeface font;
    DBHelperInformer dbHelper;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.menu, menu );
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String text_qu = "", autor_qu = "";
        HashMap dataQu;
        if (listView != null) {
            ListAdapter adapter = listView.getAdapter();
            try {
                dataQu = (HashMap) adapter.getItem(0);
                text_qu = (String) dataQu.get("Text");
                autor_qu = (String) dataQu.get("Autor");
            } catch (Exception e) {

                text_qu = "";
                autor_qu = "";
                return super.onOptionsItemSelected(item);
            }

            switch (id) {
                case R.id.btnShare:
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text_qu + "Автор : " + autor_qu);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
                    break;
                case R.id.btnFavourites:
                    // создаем объект для данных
                    ContentValues cv = new ContentValues();
                    cv.put("text_qu", text_qu);
                    cv.put("autor_qu", autor_qu);
                    // подключаемся к БД
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    // вставляем запись и получаем ее ID
                    long rowID = db.insert("informer", null, cv);
                    Toast.makeText(this,"Статья добавлена в избранное",Toast.LENGTH_LONG).show();
                    dbHelper.close();
                    break;


            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelperInformer(this);
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

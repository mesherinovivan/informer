package ru.rus.iamescherinov.informer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ExampleAdapter extends ArrayAdapter<String> {
    private final Context context;

    public ExampleAdapter(Context context,  String[] objects, int textViewResourceId) {
        super( context, textViewResourceId, objects );
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = super.getView( position, convertView, parent );
        Typeface font = Typeface.createFromAsset(this.context.getAssets(), "font/annabelle.ttf");
        TextView tv = (TextView) view.findViewById(R.id.text_quote );
        tv.setTypeface(font);

        return view;
    }
}
package helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import integration.comm.project.localsearch.R;


/**
 * Created by ayushrawat on 17/04/17.
 */

public class DataAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    ArrayList<PersonalData> list;
    ArrayList<PersonalData> listToDisplay;
    public DataAdapter(Context context, ArrayList<PersonalData> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.listToDisplay = list;
    }

    public class ViewHolder {
        TextView name;
        TextView details;
    }
    @Override
    public int getCount() {
        return listToDisplay.size();
    }

    @Override
    public PersonalData getItem(int position) {
        return listToDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if( convertView == null ) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.data, parent);
            holder.name = (TextView) convertView.findViewById(R.id.nameArea);
            holder.details = (TextView) convertView.findViewById(R.id.details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getName() + " - " + getItem(position).getArea());
        holder.details.setText("Address: "+getItem(position).getLine1()
                + "\nPhone: " + getItem(position).getPhone()
                + "\nReference: " + getItem(position).getReference()
                + "\nContact Time: " + getItem(position).getTime());



        return null;
    }
}

package helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import integration.comm.project.localsearch.R;


/**
 * Created by ayushrawat on 17/04/17.
 */

public class DataAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;
    ArrayList<PersonalData> list;
    ArrayList<PersonalData> listToDisplay = new ArrayList<PersonalData>();
    public DataAdapter(Context context, ArrayList<PersonalData> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.listToDisplay.addAll(list);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if( convertView == null ) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.data, null);
            holder.name = (TextView) convertView.findViewById(R.id.nameArea);
            holder.details = (TextView) convertView.findViewById(R.id.details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(getItem(position).getName() + " - " + getItem(position).getArea());
        holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
        holder.details.setText("Address: "+getItem(position).getLine1()
                + "\nPhone: " + getItem(position).getPhone()
                + "\nReference: " + getItem(position).getReference()
                + "\nContact Time: " + getItem(position).getTime());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage(R.string.dialog_text);
                builder.setTitle(R.string.dialog_title);

                builder.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Go to Maps Activity with Area/Address
                        try {
                            String intentUriStr = "geo:26.9124,75.7873?q=_line1_"+getItem(position).getArea();
                            if (getItem(position).getLine1() !=null && !getItem(position).getLine1().trim().isEmpty())
                            {
                                intentUriStr = intentUriStr.replace("_line1_",getItem(position).getLine1()+", ");
                            } else {
                                intentUriStr = intentUriStr.replace("_line1_","");
                            }
                            Log.i("DataAdapter",intentUriStr);
                            Uri intentUri = Uri.parse(intentUriStr);

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            if(mapIntent.resolveActivity(context.getPackageManager()) != null)
                            {
                                context.startActivity(mapIntent);
                            } else throw new Exception();
                        } catch (Exception e) {
                            Toast.makeText(context, "Unable to Open Google Maps!", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }
                });

                builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phone = getItem(position).getPhone();
                        phone = phone.replace("-","");
                        String[] phones = phone.split("/");
                        phone = phones[0];
                        if(phone !=null && !phone.trim().isEmpty() && !phone.equalsIgnoreCase("NA")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+phone));
                            context.startActivity(intent);
                        }
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        return convertView;
    }

    public void Filter(String text, String key) {
        listToDisplay.clear();
        if (text.trim().isEmpty()) {
            listToDisplay.addAll(list);
        } else {
            for(PersonalData pd: list) {
                String toMatch = "";
                switch (key) {
                    case "Area": toMatch=pd.getArea(); break;
                    case "Name": toMatch=pd.getName(); break;
                    case "Address Line1": toMatch=pd.getLine1(); break;
                    case "Reference": toMatch=pd.getReference(); break;
                    case "Phone": toMatch=pd.getPhone(); break;
                    case "Contact Time": toMatch=pd.getTime(); break;
                    default: toMatch=pd.getArea(); break;
                }
                if (toMatch != null && !toMatch.trim().isEmpty()) {
                    toMatch = toMatch.toLowerCase();
                    if (toMatch.contains(text)) {
                        listToDisplay.add(pd);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

}

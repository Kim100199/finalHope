package viva.kimeshan.viva;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vivala.vivaladestino.R;


//Method taken from/adapted from Youtube
//Author: Stevdza-San
//Link: https://www.youtube.com/watch?v=18VcnYN5_LM
public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
    String data1[];
    Context context;


    public recyclerAdapter(Context c, String s1[]){
    context = c;
data1 = s1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(context);
       View view = inflater.inflate(R.layout.history,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       holder.t.setText(data1[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView t;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            t = itemView.findViewById(R.id.textView2);


        }
    }

}